package us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;

public class BsmPropertiesTest {
    @Test
    public void testEquals() {
        BsmProperties object = new BsmProperties();
        BsmProperties otherObject = new BsmProperties();
        otherObject.setId("test");

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
        BsmProperties bsmProperties = new BsmProperties();
        Integer hash = bsmProperties.hashCode();
        assertNotNull(hash);
    }

    @Test
    public void testToString() {
        BsmProperties bsmProperties = new BsmProperties();
        String string = bsmProperties.toString();
        assertNotNull(string);
    }
}
