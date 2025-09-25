package us.dot.its.jpo.geojsonconverter.converter.ssm;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessage;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.common.DeserializedRawMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;
import us.dot.its.jpo.geojsonconverter.utils.ProcessedSchemaVersions;
import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata;
import us.dot.its.jpo.ode.model.OdeMessageFramePayload;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * KeyValueMapper transforms one SSM to one or more ProcessedSsms, for use in flatMap in the Topology.
 * Implement this as a KeyValueMapper, because the Transformer interface is deprecated.
 * The transformation is not stateful, so this doesn't need to be a processor.
 */
@Slf4j
public class SsmTransformer implements KeyValueMapper<Void, DeserializedRawMessageFrame, Iterable<KeyValue<RsuIntersectionKey, ProcessedSsm>>> {

    private final SsmConverter converter;

    public SsmTransformer(SsmConverter converter) {
        this.converter = converter;
    }

    @Override
    public Iterable<KeyValue<RsuIntersectionKey, ProcessedSsm>> apply(Void rawKey, DeserializedRawMessageFrame rawSsm) {

        try {
            if (!rawSsm.isValidationFailure()) {
                OdeMessageFrameData rawValue = rawSsm.getOdeMessageFrameData();
                OdeMessageFrameMetadata metadata = rawValue.getMetadata();
                OdeMessageFramePayload payload = rawValue.getPayload();
                SignalStatusMessageMessageFrame ssmMessageFrame = (SignalStatusMessageMessageFrame)payload.getData();
                JsonValidatorResult validationResults = rawSsm.getValidationResults();
                return createProcessedSsmList(ssmMessageFrame, metadata, validationResults);
            }
        } catch (Exception e) {
            log.error("Error converting Ssm to Processed Ssm", e);
        }
        return new ArrayList<>();
    }

    private List<KeyValue<RsuIntersectionKey, ProcessedSsm>> createProcessedSsmList(SignalStatusMessageMessageFrame ssm,
                 OdeMessageFrameMetadata metadata, JsonValidatorResult validationResults) {

        final ZonedDateTime odeReceivedAt = Instant.parse(metadata.getOdeReceivedAt()).atZone(ZoneId.of("UTC"));
        final int year = odeReceivedAt.getYear();

        // Process message frame
        var processedSsmList =  converter.processSsm(ssm, year);

        List<KeyValue<RsuIntersectionKey, ProcessedSsm>> keyValueList = new ArrayList<>();

        // Add metadata
        for (ProcessedSsm processedSsm : processedSsmList) {
            processedSsm.setOdeReceivedAt(odeReceivedAt);
            processedSsm.setAsn1(metadata.getAsn1());
            processedSsm.setOriginIp(metadata.getOriginIp());
            processedSsm.setSchemaVersion(ProcessedSchemaVersions.PROCESSED_SSM_SCHEMA_VERSION);

            var key = new RsuIntersectionKey();
            key.setRsuId(metadata.getOriginIp());
            key.setIntersectionId(processedSsm.getIntersectionId());
            key.setRegion(processedSsm.getRegion());

            keyValueList.add(new KeyValue<>(key, processedSsm));
        }

        return keyValueList;
    }
}
