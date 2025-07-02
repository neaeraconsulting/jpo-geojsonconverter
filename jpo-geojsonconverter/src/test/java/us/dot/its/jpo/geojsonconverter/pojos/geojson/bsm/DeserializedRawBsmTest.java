package us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;

public class DeserializedRawBsmTest {
    @Test
    public void testGettersSetters() {
        DeserializedRawBsm object = new DeserializedRawBsm();

        OdeMessageFrameData bsmData = new OdeMessageFrameData();
        object.setOdeBsmMessageFrameData(bsmData);
        OdeMessageFrameData dataResponse = object.getOdeBsmMessageFrameData();
        assertEquals(dataResponse, bsmData);

        JsonValidatorResult validation = new JsonValidatorResult();
        object.setValidatorResults(validation);
        JsonValidatorResult validationResponse = object.getValidatorResults();
        assertEquals(validationResponse, validation);
    }

    @Test
    public void testEquals() {
        DeserializedRawBsm object = new DeserializedRawBsm();
        DeserializedRawBsm otherObject = new DeserializedRawBsm();
        OdeMessageFrameData bsmData = new OdeMessageFrameData();
        object.setOdeBsmMessageFrameData(bsmData);

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
        DeserializedRawBsm object = new DeserializedRawBsm();

        Integer hash = object.hashCode();
        assertNotNull(hash);
    }

    @Test
    public void testToString() {
        DeserializedRawBsm object = new DeserializedRawBsm();

        String string = object.toString();
        assertNotNull(string);
    }
}
