package us.dot.its.jpo.geojsonconverter.converter.rtcm;

import us.dot.its.jpo.asn.j2735.r2024.Common.DDateTime;

public class RtcmFieldConversions {

    /**
     * <pre>
     * Longitude ::= INTEGER (-1799999999..1800000001)
     *  -- LSB = 1/10 microdegree
     *  -- Providing a range of plus-minus 180 degrees
     *  </pre>
     * @param j2735Long in 1/10 microdegrees
     * @return longitude in degrees
     */
    public static double convertLong(long j2735Long) {

        Double returnValue = null;
        if (j2735Long != 1800000001) {
            returnValue = j2735Long * 1e-7;
        }
        return returnValue;
    }

    /**
     * <pre>
     * Latitude ::= INTEGER (-900000000..900000001)
     *  -- LSB = 1/10 microdegree
     *  -- Providing a range of plus-minus 90 degrees
     *  </pre>
     * @param j2735Lat in 1/10 microdegrees
     * @return latitude in degrees
     */
    public static Double convertLat(long j2735Lat) {
        Double returnValue = null;
        if (j2735Lat != 900000001) {
            returnValue = j2735Lat * 1e-7;
        }
        return returnValue;
    }

    /**
     * <pre>
     * Elevation ::= INTEGER (-4096..61439)
     * -- In units of 10 cm steps above or below the reference ellipsoid
     * -- Providing a range of -409.5 to + 6143.9 meters
     * -- The value -4096 shall be used when Unknown is to be sent
     * </pre>
     * @param j2735Elevation elevation in 10cm
     * @return elevation in meters
     */
    public static Double convertElevation(long j2735Elevation) {
        return (j2735Elevation != -4096)
                ? j2735Elevation / 10d
                : null;
    }

    public static long convertDDateTime(DDateTime dDateTime) {
        return 0;
    }

}
