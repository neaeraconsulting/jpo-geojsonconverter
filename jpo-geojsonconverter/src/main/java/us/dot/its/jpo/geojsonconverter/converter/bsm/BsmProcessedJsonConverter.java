package us.dot.its.jpo.geojsonconverter.converter.bsm;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networknt.schema.ValidationMessage;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuLogKey;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.DeserializedRawBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsmAccelerationSet4Way;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsmPositionalAccuracy;
import us.dot.its.jpo.geojsonconverter.utils.ProcessedSchemaVersions;
import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;

import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.asn.j2735.r2024.BasicSafetyMessage.BasicSafetyMessageMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.Common.BSMcoreData;

public class BsmProcessedJsonConverter
        implements Transformer<Void, DeserializedRawBsm, KeyValue<RsuLogKey, ProcessedBsm<Point>>> {
    private static final Logger logger = LoggerFactory.getLogger(BsmProcessedJsonConverter.class);

    @Override
    public void init(ProcessorContext arg0) {}

    /**
     * Transform an ODE BSM POJO to Processed BSM POJO.
     * 
     * @param rawKey - Void type because ODE topics have no specified key
     * @param rawBsm - The raw POJO
     * @return A key value pair: the key a RsuLogKey containing the RSU IP address or the BSM log file name
     */
    @Override
    public KeyValue<RsuLogKey, ProcessedBsm<Point>> transform(Void rawKey, DeserializedRawBsm rawBsm) {
        try {
            if (!rawBsm.getValidationFailure()) {
                OdeMessageFrameData rawValue = new OdeMessageFrameData();
                rawValue.setMetadata(rawBsm.getOdeBsmMessageFrameData().getMetadata());
                OdeMessageFrameMetadata bsmMetadata = rawValue.getMetadata();

                rawValue.setPayload(rawBsm.getOdeBsmMessageFrameData().getPayload());
                BasicSafetyMessageMessageFrame bsmMessageFrame =
                        (BasicSafetyMessageMessageFrame) rawValue.getPayload().getData();

                ProcessedBsm<Point> processedBsm =
                        createProcessedBsm(bsmMetadata, bsmMessageFrame, rawBsm.getValidatorResults());

                // Set the schema version
                processedBsm.getProperties().setSchemaVersion(ProcessedSchemaVersions.PROCESSED_BSM_SCHEMA_VERSION);
                RsuLogKey key = new RsuLogKey();
                key.setRsuId(bsmMetadata.getOriginIp());
                key.setLogId(bsmMetadata.getLogFileName());
                key.setBsmId(bsmMessageFrame.getValue().getCoreData().getId().toString());

                return KeyValue.pair(key, processedBsm);
            } else {
                ProcessedBsm<Point> processedBsm =
                        createFailureProcessedBsm(rawBsm.getValidatorResults(), rawBsm.getFailedMessage());
                RsuLogKey key = new RsuLogKey();
                key.setBsmId("ERROR");
                return KeyValue.pair(key, processedBsm);
            }
        } catch (Exception e) {
            String errMsg = String.format("Exception converting ODE BSM to Processed BSM! Message: %s", e.getMessage());
            logger.error(errMsg, e);
            // KafkaStreams knows to remove null responses before allowing further steps from occurring
            RsuLogKey key = new RsuLogKey();
            key.setBsmId("ERROR");
            return KeyValue.pair(key, null);
        }
    }

    @Override
    public void close() {
        // Nothing to do here
    }

    public ProcessedBsm<Point> createProcessedBsm(OdeMessageFrameMetadata metadata,
            BasicSafetyMessageMessageFrame bsmMessageFrame, JsonValidatorResult validationMessages) {

        ProcessedBsm<Point> processedBsm = createProcessedBsmGeometryAndProperties(bsmMessageFrame);
        // ISO 8601: 2022-11-11T16:36:10.529530Z
        processedBsm.getProperties().setOdeReceivedAt(metadata.getOdeReceivedAt());
        processedBsm.getProperties().setAsn1(metadata.getAsn1());
        if (metadata.getOriginIp() != null && !metadata.getOriginIp().isEmpty())
            processedBsm.getProperties().setOriginIp(metadata.getOriginIp());
        if (metadata.getLogFileName() != null && !metadata.getLogFileName().isEmpty())
            processedBsm.getProperties().setLogName(metadata.getLogFileName());

        List<ProcessedValidationMessage> processedBsmValidationMessages = new ArrayList<ProcessedValidationMessage>();
        for (Exception exception : validationMessages.getExceptions()) {
            ProcessedValidationMessage object = new ProcessedValidationMessage();
            object.setMessage(exception.getMessage());
            object.setException(exception.getStackTrace().toString());
            processedBsmValidationMessages.add(object);
        }
        for (ValidationMessage vm : validationMessages.getValidationMessages()) {
            ProcessedValidationMessage object = new ProcessedValidationMessage();
            object.setMessage(vm.getMessage());
            object.setSchemaPath(vm.getSchemaPath());
            object.setJsonPath(vm.getPath());

            processedBsmValidationMessages.add(object);
        }

        ZonedDateTime odeDate = Instant.parse(metadata.getOdeReceivedAt()).atZone(ZoneId.of("UTC"));

        processedBsm.getProperties().setValidationMessages(processedBsmValidationMessages);
        processedBsm.getProperties().setTimeStamp(
                generateOffsetUTCTimestamp(odeDate, bsmMessageFrame.getValue().getCoreData().getSecMark().getValue()));

        return processedBsm;
    }

    public ProcessedBsm<Point> createFailureProcessedBsm(JsonValidatorResult validatorResult, String message) {
        ProcessedBsm<Point> processedBsm = new ProcessedBsm<Point>(null, null, new BsmProperties());
        ProcessedValidationMessage object = new ProcessedValidationMessage();
        List<ProcessedValidationMessage> processedBsmValidationMessages = new ArrayList<ProcessedValidationMessage>();

        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);

        object.setMessage(message);
        object.setException(ExceptionUtils.getStackTrace(validatorResult.getExceptions().get(0)));

        processedBsmValidationMessages.add(object);
        processedBsm.getProperties().setValidationMessages(processedBsmValidationMessages);
        processedBsm.getProperties().setTimeStamp(utcDateTime);

        return processedBsm;
    }

    private ProcessedBsm<Point> createProcessedBsmGeometryAndProperties(
            BasicSafetyMessageMessageFrame bsmMessageFrame) {
        BSMcoreData coreData = bsmMessageFrame.getValue().getCoreData();

        // Create the Geometry Point
        Double bsmLong = BsmFieldConversions.convertLong(coreData.getLong_().getValue());
        Double bsmLat = BsmFieldConversions.convertLat(coreData.getLat().getValue());
        Point bsmPoint = new Point(bsmLong, bsmLat);

        // Create the BSM Properties
        BsmProperties bsmProps = new BsmProperties();
        bsmProps.setAccelSet(new ProcessedBsmAccelerationSet4Way(
                BsmFieldConversions.convertAccelLatLong(coreData.getAccelSet().getLat().getValue()),
                BsmFieldConversions.convertAccelLatLong(coreData.getAccelSet().getLong_().getValue()),
                BsmFieldConversions.convertAccelVert(coreData.getAccelSet().getVert().getValue()),
                BsmFieldConversions.convertAccelYaw(coreData.getAccelSet().getYaw().getValue())));
        bsmProps.setAccuracy(new ProcessedBsmPositionalAccuracy(
                BsmFieldConversions.convertSemiMajor(coreData.getAccuracy().getSemiMajor().getValue()),
                BsmFieldConversions.convertSemiMinor(coreData.getAccuracy().getSemiMinor().getValue()),
                BsmFieldConversions.convertOrientation(coreData.getAccuracy().getOrientation().getValue())));
        bsmProps.setAngle(BsmFieldConversions.convertAngle(coreData.getAngle().getValue()));
        bsmProps.setBrakes(coreData.getBrakes());
        bsmProps.setHeading(BsmFieldConversions.convertHeading(coreData.getHeading().getValue()));
        bsmProps.setId(coreData.getId().getValue());
        bsmProps.setMsgCnt(coreData.getMsgCnt().getValue());
        bsmProps.setSecMark(coreData.getSecMark().getValue());
        bsmProps.setSize(coreData.getSize());
        bsmProps.setSpeed(BsmFieldConversions.convertSpeed(coreData.getSpeed().getValue()));
        bsmProps.setTransmission(coreData.getTransmission());

        return new ProcessedBsm<Point>(null, bsmPoint, bsmProps);
    }

    public ZonedDateTime generateOffsetUTCTimestamp(ZonedDateTime odeReceivedAt, Long secMark) {
        try {
            if (secMark != null) {
                int millis = (int) (secMark % 1000);
                int seconds = (int) (secMark / 1000);
                ZonedDateTime date = odeReceivedAt;
                if (secMark == 65535) {

                    // Return UTC time zero if the Zoned Date time is marked as unknown, UTC time zero chosen so that a
                    // null value can represent an empty field in the BSM. But 65535, can represent an intentionally
                    // unidentified field.
                    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("UTC"));

                } else {
                    // If we are within 10 seconds of the next minute, and the timeMark is a large number, it probably
                    // means that the time rolled over before reception.
                    // In this case, subtract a minute from the odeReceivedAt so that the true time represents the
                    // minute in the past.
                    if (odeReceivedAt.getSecond() < 10 && secMark > 50000) {
                        date = date.minusMinutes(1);
                    }

                    date = date.withSecond(seconds);
                    date = date.withNano(0);
                    date = date.plus(millis, ChronoUnit.MILLIS);
                    return date;
                }


            } else {
                return null;
            }
        } catch (Exception e) {
            String errMsg = String.format(
                    "Failed to generateOffsetUTCTimestamp - BSMProcessedJsonConverter. Message: %s", e.getMessage());
            logger.error(errMsg, e);
            return null;
        }
    }
}
