package us.dot.its.jpo.geojsonconverter.converter.bsm;

public class BsmFieldConversions {
    public static Double convertLong(long j2735Long) {
        // Longitude ::= INTEGER (-1799999999..1800000001)
        // -- LSB = 1/10 microdegree
        // -- Providing a range of plus-minus 180 degrees
        Double returnValue = null;
        if (j2735Long != 1800000001) {
            returnValue = j2735Long * 1e-7;
        }
        return returnValue;
    }

    public static Double convertLat(long j2735Lat) {
        // Latitude ::= INTEGER (-900000000..900000001)
        // -- LSB = 1/10 microdegree
        // -- Providing a range of plus-minus 90 degrees
        Double returnValue = null;
        if (j2735Lat != 900000001) {
            returnValue = j2735Lat * 1e-7;
        }
        return returnValue;
    }

    public static Double convertAccelLatLong(long accelLatLong) {
        // Acceleration ::= INTEGER (-2000..2001)
        // -- LSB units are 0.01 m/s^2
        // -- the value 2000 shall be used for values greater than 2000
        // -- the value -2000 shall be used for values less than -2000
        // -- a value of 2001 means the value is unavailable
        Double returnValue = null;
        if (accelLatLong < -2000) {
            returnValue = -20.0;
        } else if (accelLatLong > 2001) {
            returnValue = 20.0;
        } else if (accelLatLong != 2001) {
            returnValue = accelLatLong * 0.01;
        }
        return returnValue;
    }

    public static Double convertAccelVert(long accelVert) {
        // VerticalAcceleration ::= INTEGER (-127..127)
        // -- LSB units of 0.02 G steps over -2.52 to +2.54 G
        // -- The value +127 shall be used for ranges >= 2.54 G
        // -- The value -126 shall be used for ranges <= -2.52 G
        // -- The value -127 shall be used for unavailable
        Double returnValue = null;
        if (accelVert != -127) {
            returnValue = accelVert * 0.02;
        }
        return returnValue;
    }

    public static Double convertAccelYaw(long accelYaw) {
        // YawRate ::= INTEGER (-32767..32767)
        // -- LSB units of 0.01 degrees per second (signed)
        Double returnValue = null;
        if (accelYaw >= -32767 && accelYaw <= 32767) {
            returnValue = accelYaw * 0.01;
        }
        return returnValue;
    }

    public static Double convertSemiMajor(long semiMajor) {
        // SemiMajorAxisAccuracy ::= INTEGER (0..255)
        // -- semi-major axis accuracy at one standard dev
        // -- range 0-12.7 meter, LSB = .05m
        // -- 254 = any value equal or greater than 12.70 meter
        // -- 255 = unavailable semi-major axis value
        Double returnValue = null;
        if (semiMajor >= 0 && semiMajor < 255) {
            returnValue = semiMajor * 0.05;
        }
        return returnValue;
    }

    public static Double convertSemiMinor(long semiMinor) {
        // SemiMinorAxisAccuracy ::= INTEGER (0..255)
        // -- semi-minor axis accuracy at one standard dev
        // -- range 0-12.7 meter, LSB = .05m
        // -- 254 = any value equal or greater than 12.70 meter
        // -- 255 = unavailable semi-minor axis val
        Double returnValue = null;
        if (semiMinor >= 0 && semiMinor < 255) {
            returnValue = semiMinor * 0.05;
        }
        return returnValue;
    }

    public static Double convertOrientation(long orientation) {
        // SemiMajorAxisOrientation ::= INTEGER (0..65535)
        // -- orientation of semi-major axis
        // -- relative to true north (0~359.9945078786 degrees)
        // -- LSB units of 360/65535 deg = 0.0054932479
        // -- a value of 0 shall be 0 degrees
        // -- a value of 1 shall be 0.0054932479 degrees
        // -- a value of 65534 shall be 359.9945078786 deg
        // -- a value of 65535 shall be used for orientation unavailable
        Double returnValue = null;
        if (orientation >= 0 && orientation < 65535) {
            returnValue = 0.0054932479 * orientation;
        }
        return returnValue;
    }

    public static Double convertAngle(long angle) {
        // SteeringWheelAngle ::= INTEGER (-126..127)
        // -- LSB units of 1.5 degrees, a range of -189 to +189 degrees
        // -- +001 = +1.5 deg
        // -- -126 = -189 deg and beyond
        // -- +126 = +189 deg and beyond
        // -- +127 to be used for unavai
        Double returnValue = null;
        if (angle >= -126 && angle < 127) {
            returnValue = angle * 1.5;
        }
        return returnValue;
    }

    public static Double convertHeading(long angle) {
        // Heading ::= INTEGER (0..28800)
        // -- LSB of 0.0125 degrees
        // -- A range of 0 to 359.9875 degrees
        Double returnValue = null;
        if (angle >= 0 && angle <= 28800) {
            returnValue = angle * 0.0125;
        }
        return returnValue;
    }

    public static Double convertSpeed(long speed) {
        // Speed ::= INTEGER (0..8191) -- Units of 0.02 m/s
        // -- The value 8191 indicates that
        // -- speed is unavailable
        Double returnValue = null;
        if (speed >= 0 && speed < 8191) {
            returnValue = speed * 0.02;
        }
        return returnValue;
    }
}
