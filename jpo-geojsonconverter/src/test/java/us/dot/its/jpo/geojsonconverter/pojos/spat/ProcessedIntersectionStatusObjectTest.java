package us.dot.its.jpo.geojsonconverter.pojos.spat;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import us.dot.its.jpo.asn.j2735.r2024.SPAT.IntersectionStatusObject;

public class ProcessedIntersectionStatusObjectTest {
    @Test
    public void testSetStatus() {
        IntersectionStatusObject status = new IntersectionStatusObject();
        status.setManualControlIsEnabled(true);
        status.setStopTimeIsActivated(true);
        status.setFailureFlash(true);
        status.setPreemptIsActive(true);
        status.setSignalPriorityIsActive(true);
        status.setFixedTimeOperation(true);
        status.setTrafficDependentOperation(true);
        status.setStandbyOperation(true);
        status.setFailureMode(true);
        status.setOff(true);
        status.setRecentMAPmessageUpdate(true);
        status.setRecentChangeInMAPassignedLanesIDsUsed(true);
        status.setNoValidMAPisAvailableAtThisTime(true);
        status.setNoValidSPATisAvailableAtThisTime(true);

        ProcessedIntersectionStatusObject object = new ProcessedIntersectionStatusObject();
        object.setStatus(status);

        assertEquals(object.getManualControlIsEnabled(), true);
        assertEquals(object.getStopTimeIsActivated(), true);
        assertEquals(object.getFailureFlash(), true);
        assertEquals(object.getPreemptIsActive(), true);
        assertEquals(object.getSignalPriorityIsActive(), true);
        assertEquals(object.getFixedTimeOperation(), true);
        assertEquals(object.getTrafficDependentOperation(), true);
        assertEquals(object.getStandbyOperation(), true);
        assertEquals(object.getFailureMode(), true);
        assertEquals(object.getOff(), true);
        assertEquals(object.getRecentMAPmessageUpdate(), true);
        assertEquals(object.getRecentChangeInMAPassignedLanesIDsUsed(), true);
        assertEquals(object.getNoValidMAPisAvailableAtThisTime(), true);
        assertEquals(object.getNoValidSPATisAvailableAtThisTime(), true);
    }

    @Test
    public void testToString() {
        IntersectionStatusObject object = new IntersectionStatusObject();

        String string = object.toString();
        assertNotNull(string);
    }
}
