package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;

public class DeserializedRawMapTest {
    @Test
    public void testGettersSetters() {
        DeserializedRawMap object = new DeserializedRawMap();

        OdeMessageFrameData mapData = new OdeMessageFrameData();
        object.setOdeMapMessageFrameData(mapData);
        OdeMessageFrameData dataResponse = object.getOdeMapMessageFrameData();
        assertEquals(dataResponse, mapData);

        JsonValidatorResult validation = new JsonValidatorResult();
        object.setValidatorResults(validation);
        JsonValidatorResult validationResponse = object.getValidatorResults();
        assertEquals(validationResponse, validation);
    }

    @Test
    public void testEquals() {
        DeserializedRawMap object = new DeserializedRawMap();
        DeserializedRawMap otherObject = new DeserializedRawMap();
        OdeMessageFrameData mapData = new OdeMessageFrameData();
        otherObject.setOdeMapMessageFrameData(mapData);

        boolean equals = object.equals(object);
        assertEquals(true, equals);

        boolean otherEquals = object.equals(otherObject);
        assertEquals(false, otherEquals);

        String string = "string";
        boolean notEquals = otherObject.equals(string);
        assertEquals(false, notEquals);
    }

    @Test
    public void testHashCode() {
        DeserializedRawMap object = new DeserializedRawMap();

        Integer hash = object.hashCode();
        assertNotNull(hash);
    }

    @Test
    public void testToString() {
        DeserializedRawMap object = new DeserializedRawMap();

        String string = object.toString();
        assertNotNull(string);
    }
}
