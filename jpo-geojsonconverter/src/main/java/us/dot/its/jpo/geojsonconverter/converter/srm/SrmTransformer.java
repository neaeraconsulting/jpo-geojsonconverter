package us.dot.its.jpo.geojsonconverter.converter.srm;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.SignalRequestMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.common.DeserializedRawMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.ProcessedSrm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.SrmProperties;
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

@Slf4j
public class SrmTransformer
    implements KeyValueMapper<Void, DeserializedRawMessageFrame, Iterable<KeyValue<RsuIntersectionKey, ProcessedSrm>>> {

    private final SrmConverter converter;

    public SrmTransformer(SrmConverter converter) {
        this.converter = converter;
    }

    @Override
    public Iterable<KeyValue<RsuIntersectionKey, ProcessedSrm>> apply(Void rawKey, DeserializedRawMessageFrame rawSrm) {
        try {
            if (!rawSrm.isValidationFailure()) {
                OdeMessageFrameData rawValue = rawSrm.getOdeMessageFrameData();
                OdeMessageFrameMetadata metadata = rawValue.getMetadata();
                OdeMessageFramePayload payload = rawValue.getPayload();
                SignalRequestMessageMessageFrame ssmMessageFrame = (SignalRequestMessageMessageFrame)payload.getData();
                JsonValidatorResult validationResults = rawSrm.getValidationResults();
                return createProcessedSrmList(ssmMessageFrame, metadata, validationResults);
            }
        } catch (Exception e) {
            log.error("Error converting SRM to Processed SRM", e);
        }
        return new ArrayList<>();
    }

    private List<KeyValue<RsuIntersectionKey, ProcessedSrm>> createProcessedSrmList(
            SignalRequestMessageMessageFrame srm, OdeMessageFrameMetadata metadata, JsonValidatorResult validationResults) {
        final ZonedDateTime odeReceivedAt = Instant.parse(metadata.getOdeReceivedAt()).atZone(ZoneId.of("UTC"));
        final int year = odeReceivedAt.getYear();

        // Process message frame
        var processedSrmList =  converter.processSrm(srm, year);

        List<KeyValue<RsuIntersectionKey, ProcessedSrm>> keyValueList = new ArrayList<>();

        // Add metadata
        for (ProcessedSrm processedSrm : processedSrmList) {
            SrmProperties properties = processedSrm.getProperties();
            properties.setOdeReceivedAt(odeReceivedAt);
            properties.setAsn1(metadata.getAsn1());
            properties.setOriginIp(metadata.getOriginIp());
            properties.setSchemaVersion(ProcessedSchemaVersions.PROCESSED_SSM_SCHEMA_VERSION);

            var key = new RsuIntersectionKey();
            key.setRsuId(metadata.getOriginIp());
            key.setIntersectionId(properties.getIntersectionId());
            key.setRegion(properties.getRegion());

            keyValueList.add(new KeyValue<>(key, processedSrm));
        }

        return keyValueList;
    }
}
