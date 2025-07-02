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
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;

public class BsmFeatureTest {
    BsmProperties properties;
    Point geometry;

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
        this.properties = properties;

        Double[] coordinates = new Double[] {40.5671913, -105.0342901};
        this.geometry = new Point(coordinates);
    }

    @Test
    public void testBsmFeatureConstructor() {
        ProcessedBsm<Point> feature = new ProcessedBsm<Point>(null, geometry, properties);
        assertNotNull(feature);
    }

    @Test
    public void testType() {
        ProcessedBsm<Point> feature = new ProcessedBsm<Point>(null, geometry, properties);
        assertEquals("Feature", feature.getType());
    }

    @Test
    public void testGeoJsonToString() {
        String expectedString =
                """
                        {"type":"Feature","geometry":{"type":"Point","coordinates":[40.5671913,-105.0342901]},"properties":{"schemaVersion":-1,"messageType":"BSM","accelSet":{"accelLat":2001.0,"accelLong":0.0,"accelYaw":0.0},"accuracy":{"semiMajor":5.0,"semiMinor":2.0,"orientation":0.0},"angle":10.0,"brakes":{"traction":"unavailable","abs":"unavailable","scs":"unavailable","brakeBoost":"unavailable","auxBrakes":"unavailable"},"heading":10.0,"id":"12A7A951","msgCnt":20,"secMark":280,"size":{"width":208,"length":586},"speed":100.0,"transmission":"forwardGears"}}""";
        ProcessedBsm<Point> feature = new ProcessedBsm<Point>(null, geometry, properties);
        assertEquals(expectedString, feature.toString());
    }

    @Test
    public void testGetId() {
        ProcessedBsm<Point> feature = new ProcessedBsm<Point>(2, geometry, properties);
        assertEquals(2, feature.getId());
    }

    @Test
    public void testGeoJsonGetGeometry() {
        ProcessedBsm<Point> feature = new ProcessedBsm<Point>(null, geometry, properties);
        assertEquals(40.5671913, feature.getGeometry().getCoordinates()[0]);
    }

    @Test
    public void testGetProperties() {
        ProcessedBsm<Point> feature = new ProcessedBsm<Point>(null, geometry, properties);
        assertEquals("12A7A951", feature.getProperties().getId());
    }
}
