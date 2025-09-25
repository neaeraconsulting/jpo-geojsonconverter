package us.dot.its.jpo.geojsonconverter.converter.srm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.*;

@Slf4j
public class SrmTransformer
    implements KeyValueMapper<Void, DeserializedRawMessageFrame, KeyValue<RsuIntersectionKey, ProcessedSrm>> {

    private final SrmConverter converter;

    public SrmTransformer(SrmConverter converter) {
        this.converter = converter;
    }

    @Override
    public KeyValue<RsuIntersectionKey, ProcessedSrm> apply(Void rawKey, DeserializedRawMessageFrame rawSrm) {
        try {
            if (!rawSrm.isValidationFailure()) {
                OdeMessageFrameData rawValue = rawSrm.getOdeMessageFrameData();
                OdeMessageFrameMetadata metadata = rawValue.getMetadata();
                OdeMessageFramePayload payload = rawValue.getPayload();
                SignalRequestMessageMessageFrame ssmMessageFrame = (SignalRequestMessageMessageFrame)payload.getData();
                JsonValidatorResult validationResults = rawSrm.getValidationResults();
                return createProcessedSrm(ssmMessageFrame, metadata, validationResults);
            }
        } catch (Exception e) {
            log.error("Error converting SRM to Processed SRM", e);
        }
        return new ArrayList<>();
    }

    private KeyValue<RsuIntersectionKey, ProcessedSrm> createProcessedSrm(
            SignalRequestMessageMessageFrame srm, OdeMessageFrameMetadata metadata, JsonValidatorResult validationResults) {
        final ZonedDateTime odeReceivedAt = Instant.parse(metadata.getOdeReceivedAt()).atZone(ZoneId.of("UTC"));
        final int year = odeReceivedAt.getYear();

        // Process message frame
        ProcessedSrm processedSrm =  converter.processSrm(srm, year);

        List<KeyValue<RsuIntersectionKey, ProcessedSrm>> keyValueList = new ArrayList<>();

        // Add metadata
        SrmProperties properties = processedSrm.getProperties();
        properties.setOdeReceivedAt(odeReceivedAt);
        properties.setAsn1(metadata.getAsn1());
        final String originIp = metadata.getOriginIp();
        properties.setOriginIp(originIp);
        properties.setSchemaVersion(ProcessedSchemaVersions.PROCESSED_SSM_SCHEMA_VERSION);

        var requests = properties.getRequests();
        RsuIntersectionKey key = null;
        if (requests != null && !requests.isEmpty()) {
            SortedSet<RsuIntersectionKey> keySet = new TreeSet<>();
            requests.forEach(request ->
                    keySet.add(
                            new RsuIntersectionKey(
                                    originIp,
                                    request.getIntersectionId(),
                                    request.getRegion())));

            key = keySet.first();

            // Warn if the requests are for more than one intersection
            if (keySet.size() > 1) {
                log.warn("The ProcessedSrm contains requests for more than one intersection: {}.  " +
                        "Using {} as the topic key.  Topologies that consume this topic will need to repartition  to" +
                        "perform intersection-based joins.", StringUtils.join(keySet, ", "), key);
            }
        } else {
            key = new RsuIntersectionKey();
            key.setRsuId(originIp);
            log.warn("The ProcessedSrm has no requests {}", processedSrm);
        }

       return new KeyValue<>(key, processedSrm);
    }
}
