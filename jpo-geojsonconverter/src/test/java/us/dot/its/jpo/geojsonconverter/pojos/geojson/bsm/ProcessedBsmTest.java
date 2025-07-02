package us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import us.dot.its.jpo.asn.j2735.r2024.Common.AntiLockBrakeStatus;
import us.dot.its.jpo.asn.j2735.r2024.Common.AuxiliaryBrakeStatus;
import us.dot.its.jpo.asn.j2735.r2024.Common.BrakeBoostApplied;
import us.dot.its.jpo.asn.j2735.r2024.Common.BrakeSystemStatus;
import us.dot.its.jpo.asn.j2735.r2024.Common.StabilityControlStatus;
import us.dot.its.jpo.asn.j2735.r2024.Common.TractionControlStatus;
import us.dot.its.jpo.asn.j2735.r2024.Common.TransmissionState;
import us.dot.its.jpo.asn.j2735.r2024.Common.VehicleLength;
import us.dot.its.jpo.asn.j2735.r2024.Common.VehicleSize;
import us.dot.its.jpo.asn.j2735.r2024.Common.VehicleWidth;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

public class ProcessedBsmTest {
    ProcessedBsm<Point> feature;

    @Before
    public void setup() {
        BsmProperties properties = new BsmProperties();
        ProcessedBsmAccelerationSet4Way accelSet = new ProcessedBsmAccelerationSet4Way();
        accelSet.setAccelLat(2001D);
        accelSet.setAccelLong(0D);
        accelSet.setAccelVert(null);
        accelSet.setAccelYaw(0D);

        ProcessedBsmPositionalAccuracy positionAcc = new ProcessedBsmPositionalAccuracy();
        positionAcc.setSemiMajor(5D);
        positionAcc.setSemiMinor(2D);
        positionAcc.setOrientation(0D);

        BrakeSystemStatus brakes = new BrakeSystemStatus();
        brakes.setAbs(AntiLockBrakeStatus.UNAVAILABLE);
        brakes.setAuxBrakes(AuxiliaryBrakeStatus.UNAVAILABLE);
        brakes.setBrakeBoost(BrakeBoostApplied.UNAVAILABLE);
        brakes.setScs(StabilityControlStatus.UNAVAILABLE);
        brakes.setTraction(TractionControlStatus.UNAVAILABLE);

        VehicleSize size = new VehicleSize();
        size.setLength(new VehicleLength(586L));
        size.setWidth(new VehicleWidth(208L));

        properties.setAccelSet(accelSet);
        properties.setAccuracy(positionAcc);
        properties.setAngle(10D);
        properties.setBrakes(brakes);
        properties.setHeading(10D);
        properties.setId("12A7A951");
        properties.setMsgCnt(20L);
        properties.setSecMark(280L);
        properties.setSize(size);
        properties.setSpeed(100D);
        properties.setTransmission(TransmissionState.FORWARDGEARS);

        Double[] coordinates = new Double[] {40.5671913, -105.0342901};
        Point geometry = new Point(coordinates);

        feature = new ProcessedBsm<Point>(null, geometry, properties);
    }

    @Test
    public void testEquals() {
        ProcessedBsm<Point> object = new ProcessedBsm<Point>(null, null, new BsmProperties());
        object.getProperties().setOriginIp("10.0.0.15");
        ProcessedBsm<Point> otherObject = new ProcessedBsm<Point>(null, null, new BsmProperties());

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
        ProcessedMap<LineString> ProcessedMapPojo = new ProcessedMap<LineString>();
        Integer hash = ProcessedMapPojo.hashCode();
        assertNotNull(hash);
    }

    @Test
    public void testToString() {
        ProcessedMap<LineString> ProcessedMapPojo = new ProcessedMap<LineString>();
        String string = ProcessedMapPojo.toString();
        assertNotNull(string);
    }
}
