package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class MapPropertiesTest {

    @Test
    public void testToString() {
        MapProperties mapProperties = new MapProperties();
        String string = mapProperties.toString();
        assertNotNull(string);
    }
}
