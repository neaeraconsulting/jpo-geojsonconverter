package us.dot.its.jpo.geojsonconverter.pojos.spat;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ProcessedMovementStateTest {
    @Test
    public void testGettersSetters() {
        ProcessedMovementState object = new ProcessedMovementState();

        object.setMovementName("name");
        String nameResponse = object.getMovementName();
        assertEquals(nameResponse, "name");

        object.setSignalGroup(1);
        Integer sgResponse = object.getSignalGroup();
        assertEquals(sgResponse, 1);

        List<ProcessedMovementEvent> movementEvent = new ArrayList<ProcessedMovementEvent>();
        object.setStateTimeSpeed(movementEvent);
        List<ProcessedMovementEvent> movementResponse = object.getStateTimeSpeed();
        assertEquals(movementResponse, movementEvent);
    }

    @Test
    public void testEquals() {
        ProcessedMovementState object = new ProcessedMovementState();
        ProcessedMovementState otherObject = new ProcessedMovementState();
        otherObject.setMovementName("name");

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
        ProcessedMovementState object = new ProcessedMovementState();

        Integer hash = object.hashCode();
        assertNotNull(hash);
    }

    @Test
    public void testToString() {
        ProcessedMovementState object = new ProcessedMovementState();

        String string = object.toString();
        assertNotNull(string);
    }
}
