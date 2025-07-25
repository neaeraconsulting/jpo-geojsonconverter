package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import us.dot.its.jpo.asn.j2735.r2024.Common.IntersectionID;
import us.dot.its.jpo.asn.j2735.r2024.Common.IntersectionReferenceID;
import us.dot.its.jpo.asn.j2735.r2024.Common.RoadRegulatorID;

public class MapSharedPropertiesTest {
    @Test
    public void testSetIntersectionReferenceID_WithNullReferenceID() {
        MapSharedProperties mapSharedProperties = new MapSharedProperties();
        mapSharedProperties.setIntersectionReferenceID(null);
        assertNull(mapSharedProperties.getIntersectionId());
        assertNull(mapSharedProperties.getRegion());
    }

    @Test
    public void testSetIntersectionReferenceID_WithNullIntersectionID() {
        MapSharedProperties mapSharedProperties = new MapSharedProperties();
        IntersectionReferenceID referenceID = new IntersectionReferenceID();
        RoadRegulatorID regionID = new RoadRegulatorID(67890L);
        referenceID.setId(null);
        referenceID.setRegion(regionID);

        mapSharedProperties.setIntersectionReferenceID(referenceID);

        assertNull(mapSharedProperties.getIntersectionId());
        assertEquals(Integer.valueOf(67890), mapSharedProperties.getRegion());
    }

    @Test
    public void testSetIntersectionReferenceID_WithNullRegion() {
        MapSharedProperties mapSharedProperties = new MapSharedProperties();
        IntersectionReferenceID referenceID = new IntersectionReferenceID();
        IntersectionID intersectionID = new IntersectionID(12345L);
        referenceID.setId(intersectionID);
        referenceID.setRegion(null);

        mapSharedProperties.setIntersectionReferenceID(referenceID);

        assertEquals(Integer.valueOf(12345), mapSharedProperties.getIntersectionId());
        assertNull(mapSharedProperties.getRegion());
    }
}
