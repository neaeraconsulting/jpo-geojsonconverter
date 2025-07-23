package us.dot.its.jpo.geojsonconverter.converter.map;

import com.networknt.schema.ValidationMessage;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.asn.j2735.r2024.Common.MinuteOfTheYear;
import us.dot.its.jpo.asn.j2735.r2024.Common.NodeOffsetPointXY;
import us.dot.its.jpo.asn.j2735.r2024.Common.NodeSetXY;
import us.dot.its.jpo.asn.j2735.r2024.Common.NodeXY;
import us.dot.its.jpo.asn.j2735.r2024.Common.Node_LLmD_64b;
import us.dot.its.jpo.asn.j2735.r2024.MapData.Connection;
import us.dot.its.jpo.asn.j2735.r2024.MapData.GenericLane;
import us.dot.its.jpo.asn.j2735.r2024.MapData.IntersectionGeometry;
import us.dot.its.jpo.asn.j2735.r2024.MapData.MapData;
import us.dot.its.jpo.asn.j2735.r2024.MapData.MapDataMessageFrame;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.connectinglanes.*;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.*;
import us.dot.its.jpo.geojsonconverter.utils.ProcessedSchemaVersions;
import us.dot.its.jpo.geojsonconverter.validator.CTI4501Validator;
import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;

public class MapProcessedJsonConverter
        implements Transformer<Void, DeserializedRawMap, KeyValue<RsuIntersectionKey, ProcessedMap<LineString>>> {
    private static final Logger logger = LoggerFactory.getLogger(MapProcessedJsonConverter.class);

    @Override
    public void init(ProcessorContext arg0) {}

    /**
     * Transform an ODE MAP POJO to MAP GeoJSON POJO.
     *
     * @param rawKey - Void type because ODE topics have no specified key
     * @param rawMap - The raw POJO
     * @return A key value pair: the key an {@link RsuIntersectionKey} containing the RSU IP address and Intersection ID
     *         and the value is the GeoJSON FeatureCollection POJO
     */
    @Override
    public KeyValue<RsuIntersectionKey, ProcessedMap<LineString>> transform(Void rawKey, DeserializedRawMap rawMap) {
        try {
            if (!rawMap.getValidationFailure()) {
                OdeMessageFrameData rawValue = new OdeMessageFrameData();
                rawValue.setMetadata(rawMap.getOdeMapMessageFrameData().getMetadata());
                OdeMessageFrameMetadata mapMetadata = rawValue.getMetadata();

                rawValue.setPayload(rawMap.getOdeMapMessageFrameData().getPayload());
                MapDataMessageFrame mapMessageFrame = (MapDataMessageFrame) rawValue.getPayload().getData();
                IntersectionGeometry intersection = mapMessageFrame.getValue().getIntersections().get(0);

                MapSharedProperties sharedProps = createProperties(mapMessageFrame.getValue(), mapMetadata,
                        intersection, rawMap.getValidatorResults());

                // Set the schema version
                sharedProps.setSchemaVersion(ProcessedSchemaVersions.PROCESSED_MAP_SCHEMA_VERSION);

                MapFeatureCollection<LineString> mapFeatureCollection = createFeatureCollection(intersection);
                ConnectingLanesFeatureCollection<LineString> connectingLanesFeatureCollection =
                        createConnectingLanesFeatureCollection(mapMetadata, intersection);

                ProcessedMap<LineString> processedMapObject = new ProcessedMap<LineString>();
                processedMapObject.setMapFeatureCollection(mapFeatureCollection);
                processedMapObject.setConnectingLanesFeatureCollection(connectingLanesFeatureCollection);
                processedMapObject.setProperties(sharedProps);

                var key = new RsuIntersectionKey();
                key.setRsuId(mapMetadata.getOriginIp());
                key.setIntersectionReferenceID(intersection.getId());

                logger.debug("Successfully created MAP GeoJSON for {}", key);
                return KeyValue.pair(key, processedMapObject);
            } else {
                ProcessedMap<LineString> processedMapObject =
                        createFailureProcessedMap(rawMap.getValidatorResults(), rawMap.getFailedMessage());

                var key = new RsuIntersectionKey();
                key.setRsuId("ERROR");

                return KeyValue.pair(key, processedMapObject);
            }
        } catch (Exception e) {
            String errMsg = String.format("Exception converting ODE MAP to GeoJSON! Message: %s", e.getMessage());
            logger.error(errMsg, e);
            // KafkaStreams knows to remove null responses before allowing further steps from occurring
            var key = new RsuIntersectionKey();
            key.setRsuId("ERROR");
            return KeyValue.pair(key, null);
        }
    }

    @Override
    public void close() {
        // Nothing to do here
    }

    public MapSharedProperties createProperties(MapData mapData, OdeMessageFrameMetadata metadata,
            IntersectionGeometry intersection, JsonValidatorResult validationMessages) {
        // Save for geometry calculations
        MapRefPoint refPoint = new MapRefPoint();
        refPoint.setFromPosition3D(intersection.getRefPoint());

        String odeTimestamp = metadata.getOdeReceivedAt();
        ZonedDateTime odeDate = Instant.parse(odeTimestamp).atZone(ZoneId.of("UTC"));

        // Handle validation messages for the J2735 and CTI-4501 SPaT conformance validation
        List<ProcessedValidationMessage> processedSpatValidationMessages = new ArrayList<ProcessedValidationMessage>();
        for (Exception exception : validationMessages.getExceptions()) {
            ProcessedValidationMessage object = new ProcessedValidationMessage();
            object.setMessage(exception.getMessage());
            object.setException(Arrays.toString(exception.getStackTrace()));
            processedSpatValidationMessages.add(object);
        }
        for (ValidationMessage vm : validationMessages.getValidationMessages()) {
            ProcessedValidationMessage object = new ProcessedValidationMessage();
            object.setMessage(vm.getMessage());
            object.setSchemaPath(vm.getSchemaPath());
            object.setJsonPath(vm.getPath());

            processedSpatValidationMessages.add(object);
        }
        processedSpatValidationMessages.addAll(CTI4501Validator.mapValidation(mapData));

        // Build the MapSharedProperties object
        MapSharedProperties sharedProps = new MapSharedProperties();

        sharedProps.setOriginIp(metadata.getOriginIp());
        sharedProps.setAsn1(metadata.getAsn1());
        sharedProps.setOdeReceivedAt(odeDate);
        sharedProps.setIntersectionName(intersection.getName() != null ? intersection.getName().getValue() : null);
        sharedProps.setIntersectionReferenceID(intersection.getId());
        sharedProps.setMsgIssueRevision(
                mapData.getMsgIssueRevision() != null ? (int) mapData.getMsgIssueRevision().getValue() : null);
        sharedProps
                .setRevision(intersection.getRevision() != null ? (int) intersection.getRevision().getValue() : null);
        sharedProps.setRefPoint(refPoint);
        sharedProps.setLaneWidth(
                intersection.getLaneWidth() != null ? (int) intersection.getLaneWidth().getValue() : null);
        sharedProps.setSpeedLimits(intersection.getSpeedLimits() != null ? intersection.getSpeedLimits() : null);
        sharedProps.setMapSource(metadata.getSource());
        sharedProps.setTimeStamp(generateUTCTimestamp(mapData.getTimeStamp(), odeDate));
        // Setting validation fields
        sharedProps.setValidationMessages(processedSpatValidationMessages);
        sharedProps.setCti4501Conformant(sharedProps.getValidationMessages().size() == 0);

        return sharedProps;
    }

    @SuppressWarnings("unchecked")
    public MapFeatureCollection<LineString> createFeatureCollection(IntersectionGeometry intersection) {
        // Save for geometry calculations
        MapRefPoint refPoint = new MapRefPoint();
        refPoint.setFromPosition3D(intersection.getRefPoint());

        List<MapFeature<LineString>> mapFeatures = new ArrayList<>();
        for (GenericLane lane : intersection.getLaneSet()) {
            // Create MAP properties
            MapProperties mapProps = new MapProperties();
            if (lane.getNodeList().getNodes() != null) {
                mapProps.setNodes(nodeConversionList(lane.getNodeList().getNodes()));
            }
            mapProps.setLaneId(lane.getLaneID() != null ? (int) lane.getLaneID().getValue() : null);
            mapProps.setLaneName(lane.getName() != null ? lane.getName().getValue() : null);
            mapProps.setLaneType(
                    lane.getLaneAttributes().getLaneType() != null ? lane.getLaneAttributes().getLaneType() : null);
            mapProps.setSharedWith(lane.getLaneAttributes().getSharedWith());
            mapProps.setIngressPath(lane.getLaneAttributes().getDirectionalUse().isIngressPath());
            mapProps.setEgressPath(lane.getLaneAttributes().getDirectionalUse().isEgressPath());
            mapProps.setIngressApproach(
                    lane.getIngressApproach() != null ? (int) lane.getIngressApproach().getValue() : 0);
            mapProps.setEgressApproach(
                    lane.getEgressApproach() != null ? (int) lane.getEgressApproach().getValue() : 0);
            mapProps.setManeuvers(lane.getManeuvers());
            mapProps.setConnectsTo(lane.getConnectsTo() != null ? lane.getConnectsTo() : null);

            // Create MAP geometry
            LineString geometry = createGeometry(lane, refPoint);

            // Create MAP feature and add it to the feature list
            mapFeatures.add(new MapFeature<LineString>(mapProps.getLaneId(), geometry, mapProps));
        }

        return new MapFeatureCollection<LineString>(mapFeatures.toArray(new MapFeature[0]));
    }

    @SuppressWarnings("unchecked")
    public ConnectingLanesFeatureCollection<LineString> createConnectingLanesFeatureCollection(
            OdeMessageFrameMetadata metadata, IntersectionGeometry intersection) {
        // Save for geometry calculations
        MapRefPoint refPoint = new MapRefPoint();
        refPoint.setFromPosition3D(intersection.getRefPoint());

        HashMap<Integer, double[]> lanePoints = new HashMap<Integer, double[]>();
        for (GenericLane lane : intersection.getLaneSet()) {
            if (!lanePoints.containsKey((int) lane.getLaneID().getValue())) {
                LineString laneGeometry = createGeometry(lane, refPoint);
                double coordinate[] = {laneGeometry.getCoordinates()[0][0], laneGeometry.getCoordinates()[0][1]};
                lanePoints.put((int) lane.getLaneID().getValue(), coordinate);
            }
        }

        List<ConnectingLanesFeature<LineString>> lanesFeatures = new ArrayList<>();
        for (GenericLane lane : intersection.getLaneSet()) {
            if (lane.getLaneAttributes().getDirectionalUse().isIngressPath() == true) {
                double[] laneCoordinates = lanePoints.get((int) lane.getLaneID().getValue()); // first point
                if (lane.getConnectsTo() == null)
                    continue;

                for (Connection connection : lane.getConnectsTo()) {
                    ConnectingLanesProperties laneProps = new ConnectingLanesProperties();
                    laneProps.setIngressLaneId(lane.getLaneID() != null ? (int) lane.getLaneID().getValue() : null);
                    laneProps.setEgressLaneId(connection.getConnectingLane().getLane() != null
                            ? (int) connection.getConnectingLane().getLane().getValue()
                            : null);
                    laneProps.setSignalGroupId(
                            connection.getSignalGroup() != null ? (int) connection.getSignalGroup().getValue() : null);

                    // Point
                    double[] connectionCoordinates =
                            lanePoints.get((int) connection.getConnectingLane().getLane().getValue()); // last point
                    double[][] coordinates = new double[][] {laneCoordinates, connectionCoordinates};
                    LineString geometry = new LineString(coordinates);

                    String id = String.format("%s-%s", laneProps.getIngressLaneId(), laneProps.getEgressLaneId());
                    lanesFeatures.add(new ConnectingLanesFeature<LineString>(id, geometry, laneProps));
                }
            }
        }

        return new ConnectingLanesFeatureCollection<LineString>(lanesFeatures.toArray(new ConnectingLanesFeature[0]));
    }

    public LineString createGeometry(GenericLane lane, MapRefPoint refPoint) {
        // Calculate coordinates from reference point
        Double anchorLat = refPoint.getLatitude();
        Double anchorLong = refPoint.getLongitude();
        List<List<Double>> coordinatesList = new ArrayList<>();
        for (NodeXY nodeXY : lane.getNodeList().getNodes()) {
            NodeOffsetPointXY nodeOffset = nodeXY.getDelta();

            if (nodeOffset.getNode_LatLon() != null) {
                Node_LLmD_64b nodeLatLong = nodeOffset.getNode_LatLon();
                Double lat = MapFieldConversions.convertLat(nodeLatLong.getLat().getValue());
                Double lon = MapFieldConversions.convertLong(nodeLatLong.getLon().getValue());

                List<Double> coordinate = new ArrayList<>();
                coordinate.add(lon);
                coordinate.add(lat);
                coordinatesList.add(coordinate);

                // Reset the anchor point for following offset nodes
                // J2735 is not clear if only one of these nodelatlon types is allowed in the lane path nodes
                anchorLat = lat;
                anchorLong = lon;
            } else {
                // Get the NodeXY object or skip node if entirely null
                Double offsetX = null;
                Double offsetY = null;
                if (nodeOffset.getNode_XY1() != null) {
                    offsetX = (double) nodeOffset.getNode_XY1().getX().getValue();
                    offsetY = (double) nodeOffset.getNode_XY1().getY().getValue();
                } else if (nodeOffset.getNode_XY2() != null) {
                    offsetX = (double) nodeOffset.getNode_XY2().getX().getValue();
                    offsetY = (double) nodeOffset.getNode_XY2().getY().getValue();
                } else if (nodeOffset.getNode_XY3() != null) {
                    offsetX = (double) nodeOffset.getNode_XY3().getX().getValue();
                    offsetY = (double) nodeOffset.getNode_XY3().getY().getValue();
                } else if (nodeOffset.getNode_XY4() != null) {
                    offsetX = (double) nodeOffset.getNode_XY4().getX().getValue();
                    offsetY = (double) nodeOffset.getNode_XY4().getY().getValue();
                } else if (nodeOffset.getNode_XY5() != null) {
                    offsetX = (double) nodeOffset.getNode_XY5().getX().getValue();
                    offsetY = (double) nodeOffset.getNode_XY5().getY().getValue();
                } else if (nodeOffset.getNode_XY6() != null) {
                    offsetX = (double) nodeOffset.getNode_XY6().getX().getValue();
                    offsetY = (double) nodeOffset.getNode_XY6().getY().getValue();
                } else {
                    continue;
                }

                // Calculate offset lon,lat values
                // Equations may become less accurate the further N/S the coordinate is
                // (offsetX * 0.01) / (math.cos((Math.PI / 180.0) * anchorLat) * 111111.0)
                // Step 1. (offsetX * 0.01)
                // Step 2. (math.cos((Math.PI/180.0) * anchorLat) * 111111.0)
                // Step 3. Step 1 / Step 2
                double offsetX_step1 = offsetX * 0.01;
                double offsetX_step2 = Math.cos(((double) (Math.PI / 180.0)) * anchorLat) * 111111.0;
                double offsetXDegrees = offsetX_step1 / offsetX_step2;

                // (offsetY * 0.01) / 111111.0
                double offsetYDegrees = (offsetY * 0.01) / 111111.0;

                // return (reference_point[0] + dx_deg, reference_point[1] + dy_deg)
                double offsetLong = anchorLong + offsetXDegrees;
                double offsetLat = anchorLat + offsetYDegrees;

                List<Double> coordinate = new ArrayList<>();
                coordinate.add(offsetLong);
                coordinate.add(offsetLat);
                coordinatesList.add(coordinate);

                // Reset the anchor point for following offset nodes
                anchorLat = offsetLat;
                anchorLong = offsetLong;
            }
        }

        double[][] coordinatesArray = coordinatesList.stream()
                .map(l -> l.stream().mapToDouble(Double::doubleValue).toArray()).toArray(double[][]::new);

        return new LineString(coordinatesArray);
    }

    public ProcessedMap<LineString> createFailureProcessedMap(JsonValidatorResult validatorResult, String message) {
        ProcessedMap<LineString> processedMapObject = new ProcessedMap<LineString>();

        MapSharedProperties processedMapProps = new MapSharedProperties();
        ProcessedValidationMessage object = new ProcessedValidationMessage();
        List<ProcessedValidationMessage> processedMapValidationMessages = new ArrayList<ProcessedValidationMessage>();

        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);

        object.setMessage(message);
        object.setException(ExceptionUtils.getStackTrace(validatorResult.getExceptions().get(0)));

        processedMapValidationMessages.add(object);
        processedMapProps.setValidationMessages(processedMapValidationMessages);
        processedMapProps.setTimeStamp(utcDateTime);

        processedMapObject.setProperties(processedMapProps);

        return processedMapObject;
    }

    public ZonedDateTime generateUTCTimestamp(MinuteOfTheYear moy, ZonedDateTime odeDate) { // 2022-10-31T15:40:26.687292Z
        ZonedDateTime date = null;
        try {
            int year = odeDate.getYear();
            String dateString;
            long minutes;
            if (moy != null) {
                minutes = moy.getValue(); // minutes from beginning of year
                dateString = String.format("%d-01-01T00:00:00.00Z", year);
                date = Instant.parse(dateString).atZone(ZoneId.of("UTC"));
                date = date.plusMinutes(minutes);
            } else {
                date = odeDate;
            }

        } catch (Exception e) {
            String errMsg = String.format("Failed to generateUTCTimestamp - SpatProcessedJsonConverter. Message: %s",
                    e.getMessage());
            logger.error(errMsg, e);
        }

        return date;
    }

    public List<MapNode> nodeConversionList(NodeSetXY nodeXYs) { // 2022-10-31T15:40:26.687292Z
        List<MapNode> mapNodes = new ArrayList<MapNode>();
        try {
            for (NodeXY nodeXy : nodeXYs) {
                MapNode mapNode = new MapNode();
                if (nodeXy.getAttributes() != null) {
                    mapNode.setDWidth(nodeXy.getAttributes().getDWidth() != null
                            ? (int) nodeXy.getAttributes().getDWidth().getValue()
                            : null);
                    mapNode.setDElevation(nodeXy.getAttributes().getDElevation() != null
                            ? (int) nodeXy.getAttributes().getDElevation().getValue()
                            : null);
                }

                Integer offsetX = null;
                Integer offsetY = null;
                NodeOffsetPointXY nodeOffset = nodeXy.getDelta();
                if (nodeOffset.getNode_XY1() != null) {
                    offsetX = (int) nodeOffset.getNode_XY1().getX().getValue();
                    offsetY = (int) nodeOffset.getNode_XY1().getY().getValue();
                } else if (nodeOffset.getNode_XY2() != null) {
                    offsetX = (int) nodeOffset.getNode_XY2().getX().getValue();
                    offsetY = (int) nodeOffset.getNode_XY2().getY().getValue();
                } else if (nodeOffset.getNode_XY3() != null) {
                    offsetX = (int) nodeOffset.getNode_XY3().getX().getValue();
                    offsetY = (int) nodeOffset.getNode_XY3().getY().getValue();
                } else if (nodeOffset.getNode_XY4() != null) {
                    offsetX = (int) nodeOffset.getNode_XY4().getX().getValue();
                    offsetY = (int) nodeOffset.getNode_XY4().getY().getValue();
                } else if (nodeOffset.getNode_XY5() != null) {
                    offsetX = (int) nodeOffset.getNode_XY5().getX().getValue();
                    offsetY = (int) nodeOffset.getNode_XY5().getY().getValue();
                } else if (nodeOffset.getNode_XY6() != null) {
                    offsetX = (int) nodeOffset.getNode_XY6().getX().getValue();
                    offsetY = (int) nodeOffset.getNode_XY6().getY().getValue();
                } else if (nodeOffset.getNode_LatLon() != null) {
                    offsetX =
                            MapFieldConversions.convertLong(nodeOffset.getNode_LatLon().getLon().getValue()).intValue();
                    offsetY =
                            MapFieldConversions.convertLat(nodeOffset.getNode_LatLon().getLat().getValue()).intValue();
                } else {
                    continue;
                }

                Integer[] delta = {offsetX, offsetY};
                mapNode.setDelta(delta);
                mapNodes.add(mapNode);
            }
        } catch (Exception e) {
            String errMsg = String.format("Failed to nodeConversionList - SpatProcessedJsonConverter. Message: %s",
                    e.getMessage());
            logger.error(errMsg, e);
        }
        return mapNodes;
    }
}
