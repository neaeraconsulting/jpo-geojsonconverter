package us.dot.its.jpo.geojsonconverter.converter.rtcm;


import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuStationIdKey;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.DeserializedRawRTCM;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.ProcessedRTCM;

/**
 * Streams transformer. Converts {@link DeserializedRawRTCM}s to {@link ProcessedRTCM}s
 */
public class RTCMTransformer
    implements Transformer<Void, DeserializedRawRTCM, KeyValue<RsuStationIdKey, ProcessedRTCM>> {

    @Override
    public void init(ProcessorContext context) {
        // Nothing to initialize
    }

    @Override
    public KeyValue<RsuStationIdKey, ProcessedRTCM> transform(Void key, DeserializedRawRTCM value) {
        return null;
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
