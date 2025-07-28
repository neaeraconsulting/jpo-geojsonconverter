package us.dot.its.jpo.geojsonconverter.serialization.deserializers;

import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.asn.runtime.serialization.OdeCustomJsonMapper;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class OdeCustomJsonDeserializer<T> implements Deserializer<T> {
    private static Logger logger = LoggerFactory.getLogger(OdeCustomJsonDeserializer.class);

    private final OdeCustomJsonMapper mapper = DateJsonMapper.getOdeInstance();

    private Class<T> destinationClass;

    public OdeCustomJsonDeserializer(Class<T> destinationClass) {
        this.destinationClass = destinationClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            T returnData = mapper.readValue(data, destinationClass);
            return returnData;
        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }
}
