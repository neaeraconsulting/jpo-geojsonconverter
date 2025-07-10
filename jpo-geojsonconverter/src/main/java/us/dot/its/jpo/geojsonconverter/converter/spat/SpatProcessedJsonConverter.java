package us.dot.its.jpo.geojsonconverter.converter.spat;

import us.dot.its.jpo.asn.j2735.r2024.SPAT.IntersectionState;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.SPATMessageFrame;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementEvent;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementState;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.spat.DeserializedRawSpat;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.pojos.spat.TimingChangeDetails;
import us.dot.its.jpo.geojsonconverter.utils.ProcessedSchemaVersions;
import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networknt.schema.ValidationMessage;

public class SpatProcessedJsonConverter
        implements Transformer<Void, DeserializedRawSpat, KeyValue<RsuIntersectionKey, ProcessedSpat>> {
    private static final Logger logger = LoggerFactory.getLogger(SpatProcessedJsonConverter.class);

    @Override
    public void init(ProcessorContext arg0) {}

    /**
     * Transform an ODE SPaT POJO to Processed SPaT POJO.
     *
     * @param rawKey - Void type because ODE topics have no specified key
     * @param rawSpat - The raw POJO
     * @return A key value pair: the key an {@link RsuIntersectionKey} containing the RSU IP address and Intersection ID
     *         and the value is the GeoJSON FeatureCollection POJO
     */
    @Override
    public KeyValue<RsuIntersectionKey, ProcessedSpat> transform(Void rawKey, DeserializedRawSpat rawSpat) {
        try {
            if (!rawSpat.getValidationFailure()) {
                OdeMessageFrameData rawValue = new OdeMessageFrameData();
                rawValue.setMetadata(rawSpat.getOdeSpatMessageFrameData().getMetadata());
                OdeMessageFrameMetadata spatMetadata = rawValue.getMetadata();

                rawValue.setPayload(rawSpat.getOdeSpatMessageFrameData().getPayload());
                SPATMessageFrame spatMessageFrame = (SPATMessageFrame) rawValue.getPayload().getData();
                IntersectionState intersectionState = spatMessageFrame.getValue().getIntersections().get(0);

                ProcessedSpat processedSpat =
                        createProcessedSpat(intersectionState, spatMetadata, rawSpat.getValidatorResults());

                // Set the schema version
                processedSpat.setSchemaVersion(ProcessedSchemaVersions.PROCESSED_SPAT_SCHEMA_VERSION);

                var key = new RsuIntersectionKey();
                key.setRsuId(spatMetadata.getOriginIp());
                key.setIntersectionReferenceID(intersectionState.getId());
                return KeyValue.pair(key, processedSpat);
            } else {
                ProcessedSpat processedSpat =
                        createFailureProcessedSpat(rawSpat.getValidatorResults(), rawSpat.getFailedMessage());
                var key = new RsuIntersectionKey();
                key.setRsuId("ERROR");

                return KeyValue.pair(key, processedSpat);
            }
        } catch (Exception e) {
            String errMsg =
                    String.format("Exception converting ODE SPaT to Processed SPaT! Message: %s", e.getMessage());
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

    public ProcessedSpat createProcessedSpat(IntersectionState intersectionState, OdeMessageFrameMetadata metadata,
            JsonValidatorResult validationMessages) {
        ProcessedSpat processedSpat = new ProcessedSpat();
        processedSpat.setOdeReceivedAt(metadata.getOdeReceivedAt()); // ISO 8601: 2022-11-11T16:36:10.529530Z
        processedSpat.setOriginIp(metadata.getOriginIp());
        processedSpat.setName(intersectionState.getName() != null ? intersectionState.getName().getValue() : null);
        // Set region and intersection ID from the IntersectionReferenceID if available
        processedSpat.setIntersectionReferenceID(intersectionState.getId());
        processedSpat.setCti4501Conformant(validationMessages.isValid());

        List<ProcessedValidationMessage> processedSpatValidationMessages = new ArrayList<ProcessedValidationMessage>();
        for (Exception exception : validationMessages.getExceptions()) {
            ProcessedValidationMessage object = new ProcessedValidationMessage();
            object.setMessage(exception.getMessage());
            object.setException(exception.getStackTrace().toString());
            processedSpatValidationMessages.add(object);
        }
        for (ValidationMessage vm : validationMessages.getValidationMessages()) {
            ProcessedValidationMessage object = new ProcessedValidationMessage();
            object.setMessage(vm.getMessage());
            object.setSchemaPath(vm.getSchemaPath());
            object.setJsonPath(vm.getPath());

            processedSpatValidationMessages.add(object);
        }
        processedSpat.setValidationMessages(processedSpatValidationMessages);
        processedSpat.setRevision((int) intersectionState.getRevision().getValue());
        processedSpat.setStatus(intersectionState.getStatus());

        // Set UTC timestamp from the SPaT message if avaialable (Optional in J2735)
        ZonedDateTime utcTimestamp = null;
        if (intersectionState.getMoy() != null && intersectionState.getTimeStamp() != null) {
            utcTimestamp = generateUTCTimestamp((int) intersectionState.getMoy().getValue(),
                    (int) intersectionState.getTimeStamp().getValue(), metadata.getOdeReceivedAt());
            processedSpat.setUtcTimeStamp(utcTimestamp);
        }
        processedSpat.setEnabledLanes(
                intersectionState.getEnabledLanes() != null ? intersectionState.getEnabledLanes() : null);

        List<ProcessedMovementState> processedMovementStateList = new ArrayList<ProcessedMovementState>();
        for (MovementState signalGroupState : intersectionState.getStates()) {
            ProcessedMovementState processedMovementState = new ProcessedMovementState();
            processedMovementState.setMovementName(
                    signalGroupState.getMovementName() != null ? signalGroupState.getMovementName().getValue() : null);
            processedMovementState.setSignalGroup((int) signalGroupState.getSignalGroup().getValue());

            List<ProcessedMovementEvent> processedMovementEventList = new ArrayList<ProcessedMovementEvent>();
            if (signalGroupState.getState_time_speed() != null) {
                for (MovementEvent incomingMovementEvent : signalGroupState.getState_time_speed()) {
                    ProcessedMovementEvent processedMovementEvent = new ProcessedMovementEvent();
                    processedMovementEvent.setEventState(incomingMovementEvent.getEventState());

                    if (utcTimestamp != null) {
                        TimingChangeDetails spatTimingDetails = new TimingChangeDetails();
                        spatTimingDetails.setStartTime(generateOffsetUTCTimestamp(utcTimestamp,
                                (int) incomingMovementEvent.getTiming().getStartTime().getValue()));
                        spatTimingDetails.setMinEndTime(generateOffsetUTCTimestamp(utcTimestamp,
                                (int) incomingMovementEvent.getTiming().getMinEndTime().getValue()));
                        spatTimingDetails.setMaxEndTime(generateOffsetUTCTimestamp(utcTimestamp,
                                (int) incomingMovementEvent.getTiming().getMaxEndTime().getValue()));
                        spatTimingDetails.setLikelyTime(generateOffsetUTCTimestamp(utcTimestamp,
                                (int) incomingMovementEvent.getTiming().getLikelyTime().getValue()));
                        spatTimingDetails
                                .setConfidence((int) incomingMovementEvent.getTiming().getConfidence().getValue());

                        spatTimingDetails.setNextTime(generateOffsetUTCTimestamp(utcTimestamp,
                                (int) incomingMovementEvent.getTiming().getNextTime().getValue()));
                        processedMovementEvent.setTiming(spatTimingDetails);
                    }
                    processedMovementEvent.setSpeeds(
                            incomingMovementEvent.getSpeeds() != null ? incomingMovementEvent.getSpeeds() : null);

                    processedMovementEventList.add(processedMovementEvent);
                }
            }
            processedMovementState.setStateTimeSpeed(processedMovementEventList);
            processedMovementStateList.add(processedMovementState);
        }

        processedSpat.setStates(processedMovementStateList);
        return processedSpat;
    }

    public ProcessedSpat createFailureProcessedSpat(JsonValidatorResult validatorResult, String message) {
        ProcessedSpat processedSpat = new ProcessedSpat();
        ProcessedValidationMessage object = new ProcessedValidationMessage();
        List<ProcessedValidationMessage> processedSpatValidationMessages = new ArrayList<ProcessedValidationMessage>();

        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);

        object.setMessage(message);
        object.setException(ExceptionUtils.getStackTrace(validatorResult.getExceptions().get(0)));

        processedSpatValidationMessages.add(object);
        processedSpat.setValidationMessages(processedSpatValidationMessages);
        processedSpat.setUtcTimeStamp(utcDateTime);

        return processedSpat;
    }


    public ZonedDateTime generateUTCTimestamp(Integer moy, Integer dSecond, String odeTimestamp) { //
        // 2022-10-31T15:40:26.687292Z
        ZonedDateTime date = null;
        try {
            ZonedDateTime odeDate = Instant.parse(odeTimestamp).atZone(ZoneId.of("UTC"));
            int year = odeDate.getYear();
            String dateString;
            long milliseconds;
            if (moy != null) {
                long minutes = moy;
                milliseconds = (long) dSecond; // milliseconds in current minute
                dateString = String.format("%d-01-01T00:00:00.00Z", year);
                date = Instant.parse(dateString).atZone(ZoneId.of("UTC"));
                date = date.plusMinutes(minutes);
                date = date.plus(milliseconds, ChronoUnit.MILLIS);
            } else {
                date = odeDate;
                if (dSecond != null) {
                    milliseconds = dSecond; // milliseconds from beginning of minute
                    date = date.withSecond(0);
                    date = date.withNano(0);
                    date = date.plus(milliseconds, ChronoUnit.MILLIS);
                }
            }

        } catch (Exception e) {
            String errMsg = String.format("Failed to generateUTCTimestamp - SpatProcessedJsonConverter. Message: %s",
                    e.getMessage());
            logger.error(errMsg, e);
        }

        return date;
    }

    public ZonedDateTime generateOffsetUTCTimestamp(ZonedDateTime originTimestamp, Integer timeMark) {
        try {
            if (timeMark != null) {
                long millis = Long.valueOf(timeMark) * 100;
                ZonedDateTime date = originTimestamp;
                if (timeMark == 36011 || timeMark == 36001) {

                    // Return UTC time zero if the Zoned Date time is marked as unknown, UTC time zero chosen so that a
                    // null value can represent an empty field in the SPaT. But 36011, can represent an intentionally
                    // unidentified field.
                    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("UTC"));

                } else {
                    // If we are within 10 minutes of the next hour, and the timeMark is a small number, it probably
                    // means that the time is rolling over.
                    // In this case, add an hour to the UTC timestamp so that it appears in the future instead of in the
                    // past.s
                    if (originTimestamp.getMinute() > 50 && timeMark < 6000) {
                        date = date.plusHours(1);
                    }

                    date = date.withMinute(0);
                    date = date.withSecond(0);
                    date = date.withNano(0);
                    date = date.plus(millis, ChronoUnit.MILLIS);
                    return date;
                }


            } else {
                return null;
            }
        } catch (Exception e) {
            String errMsg = String.format(
                    "Failed to generateOffsetUTCTimestamp - SpatProcessedJsonConverter. Message: %s", e.getMessage());
            logger.error(errMsg, e);
            return null;
        }
    }
}
