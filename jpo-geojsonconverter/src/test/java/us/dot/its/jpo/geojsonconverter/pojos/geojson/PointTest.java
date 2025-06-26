package us.dot.its.jpo.geojsonconverter.pojos.geojson;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.Test;


public class PointTest {
    @Test
    public void testNonListConstructor() {
        Point geometry = new Point(39.7392, 104.9903);
        assertNotNull(geometry);
    }

    @Test
    public void testListConstructor() {
        Double[] coordinates = new Double[] {39.7392, 104.9903};
        Point geometry = new Point(coordinates);
        assertNotNull(geometry);
    }

    @Test
    public void testCoordinates() {
        Double[] coordinates = new Double[] {39.7392, 104.9903};
        Point geometry = new Point(coordinates);
        assertEquals(39.7392, geometry.getCoordinates()[0]);
    }

    @Test
    public void testBbox() {
        Double[] coordinates = new Double[] {39.7392, 104.9903};
        Point geometry = new Point(coordinates);
        assertNull(geometry.getBbox());
    }

    @Test
    public void testGeoJSONType() {
        Double[] coordinates = new Double[] {39.7392, 104.9903};
        Point geometry = new Point(coordinates);
        assertEquals("Point", geometry.getGeoJSONType());
    }

    @Test
    public void testHashCode() {
        Double[] coordinates = new Double[] {39.7392, 104.9903};
        Point geometry = new Point(coordinates);

        Integer hash = geometry.hashCode();
        assertNotNull(hash);
    }

    @Test
    public void testEquals() {
        Double[] coordinates = new Double[] {39.7392, 104.9903};
        Double[] otherCoordinates = new Double[] {1.0, 1.0};

        Point object = new Point(coordinates);
        Point otherObject = new Point(otherCoordinates);

        boolean equals = object.equals(object);
        assertEquals(true, equals);

        boolean otherEquals = object.equals(otherObject);
        assertEquals(false, otherEquals);

        String string = "string";
        boolean notEquals = otherObject.equals(string);
        assertEquals(false, notEquals);
    }

    @Test
    public void testToString() {
        Double[] coordinates = new Double[] {39.7392, 104.9903};
        Point geometry = new Point(coordinates);

        String string = geometry.toString();
        assertNotNull(string);
    }
}
