package us.dot.its.jpo.geojsonconverter.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SchemaGeneratorUtilityTest {

    private JsonNode invokeEnsureUtcTimeStampTsProperty(JsonNode schema) throws Exception {
        Method method = SchemaGeneratorUtility.class
                .getDeclaredMethod("ensureUtcTimeStampTsProperty", JsonNode.class);
        method.setAccessible(true);
        return (JsonNode) method.invoke(null, schema);
    }

    @Test
    public void ensureUtcTimeStampTsProperty_addsPropertyWhenMissing() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode properties = schema.putObject("properties");
        properties.putObject("utcTimeStamp").put("type", "string");

        JsonNode result = invokeEnsureUtcTimeStampTsProperty(schema);

        JsonNode utcTimeStampTs = result.path("properties").path("utcTimeStampTS");
        assertTrue(utcTimeStampTs.isObject());
        assertEquals("string", utcTimeStampTs.path("type").asText());
        assertEquals("date-time", utcTimeStampTs.path("format").asText());
    }

    @Test
    public void ensureUtcTimeStampTsProperty_keepsExistingProperty() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schema = mapper.createObjectNode();
        ObjectNode properties = schema.putObject("properties");
        ObjectNode existing = properties.putObject("utcTimeStampTS");
        existing.put("type", "string");
        existing.put("format", "custom-format");

        JsonNode result = invokeEnsureUtcTimeStampTsProperty(schema);

        JsonNode utcTimeStampTs = result.path("properties").path("utcTimeStampTS");
        assertEquals("custom-format", utcTimeStampTs.path("format").asText());
    }

    @Test
    public void ensureUtcTimeStampTsProperty_returnsSameNodeWhenPropertiesMissing() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");

        JsonNode result = invokeEnsureUtcTimeStampTsProperty(schema);

        assertSame(schema, result);
        assertTrue(!result.has("utcTimeStampTS"));
    }
}

