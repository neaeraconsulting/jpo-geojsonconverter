package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class MapSharedPropertiesTest {
    @Test
    public void testToString() {
        MapSharedProperties mapSharedProperties = new MapSharedProperties();
        String string = mapSharedProperties.toString();
        assertNotNull(string);
    }
}
