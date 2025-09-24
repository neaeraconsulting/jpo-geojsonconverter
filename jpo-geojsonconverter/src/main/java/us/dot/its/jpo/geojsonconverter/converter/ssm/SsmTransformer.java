package us.dot.its.jpo.geojsonconverter.converter.ssm;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.common.DeserializedRawMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;

/**
 * KeyValueMapper transforms one SSM to one or more ProcessedSsms, for use in flatMap in the Topology.
 * Implement this as a KeyValueMapper, because the Transformer interface is deprecated.
 * The transformation is not stateful, so this doesn't need to be a processor.
 */
public class SsmTransformer implements KeyValueMapper<Void, DeserializedRawMessageFrame, Iterable<KeyValue<RsuIntersectionKey, ProcessedSsm>>> {

    private final SsmConverter converter;
    private final int year;

    public SsmTransformer(SsmConverter converter, int year) {
        this.converter = converter;
        this.year = year;
    }

    @Override
    public Iterable<KeyValue<RsuIntersectionKey, ProcessedSsm>> apply(Void key, DeserializedRawMessageFrame value) {

        try {

        } catch (Exception e) {

        }
    }
}
