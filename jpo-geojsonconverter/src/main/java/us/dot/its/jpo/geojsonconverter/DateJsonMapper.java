package us.dot.its.jpo.geojsonconverter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import us.dot.its.jpo.asn.runtime.serialization.OdeCustomJsonMapper;

public class DateJsonMapper {
    final static ObjectMapper objectMapper;
    final static OdeCustomJsonMapper odeMapper;

    static {
        // Initialize the ObjectMapper for general use
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Initialize the OdeCustomJsonMapper for serialization
        odeMapper = new OdeCustomJsonMapper(true);
        odeMapper.registerModule(new JavaTimeModule());
        odeMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        odeMapper.setSerializationInclusion(Include.NON_NULL);
        odeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    public static OdeCustomJsonMapper getOdeInstance() {
        return odeMapper;
    }
}
