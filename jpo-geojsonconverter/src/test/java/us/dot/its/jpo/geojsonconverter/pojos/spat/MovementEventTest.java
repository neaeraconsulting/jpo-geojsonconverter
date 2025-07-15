package us.dot.its.jpo.geojsonconverter.pojos.spat;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class MovementEventTest {
    @Test
    public void testToString() {
        ProcessedMovementEvent object = new ProcessedMovementEvent();

        String string = object.toString();
        assertNotNull(string);
    }
}
