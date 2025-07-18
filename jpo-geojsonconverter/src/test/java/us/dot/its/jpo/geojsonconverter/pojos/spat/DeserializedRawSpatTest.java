package us.dot.its.jpo.geojsonconverter.pojos.spat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;

public class DeserializedRawSpatTest {
    @Test
    public void testGettersSetters() {
        DeserializedRawSpat object = new DeserializedRawSpat();

        OdeMessageFrameData spatData = new OdeMessageFrameData();
        object.setOdeSpatMessageFrameData(spatData);
        OdeMessageFrameData dataResponse = object.getOdeSpatMessageFrameData();
        assertEquals(dataResponse, spatData);

        JsonValidatorResult validation = new JsonValidatorResult();
        object.setValidatorResults(validation);
        JsonValidatorResult validationResponse = object.getValidatorResults();
        assertEquals(validationResponse, validation);
    }

    @Test
    public void testEquals() {
        DeserializedRawSpat object = new DeserializedRawSpat();
        DeserializedRawSpat otherObject = new DeserializedRawSpat();
        OdeMessageFrameData spatData = new OdeMessageFrameData();
        otherObject.setOdeSpatMessageFrameData(spatData);

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
        DeserializedRawSpat object = new DeserializedRawSpat();

        Integer hash = object.hashCode();
        assertNotNull(hash);
    }

    @Test
    public void testToString() {
        DeserializedRawSpat object = new DeserializedRawSpat();

        String string = object.toString();
        assertNotNull(string);
    }
}
