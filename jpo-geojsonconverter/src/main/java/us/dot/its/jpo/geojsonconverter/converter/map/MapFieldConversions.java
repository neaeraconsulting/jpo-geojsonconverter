package us.dot.its.jpo.geojsonconverter.converter.map;

public class MapFieldConversions {
    /**
     * Converts a J2735 longitude value to degrees. Providing a range of plus-minus 180 degrees. LSB = 1/10 microdegree
     * 
     * @param j2735Long J2735 longitude value.
     * @return Longitude in degrees, or null if unavailable.
     */
    public static Double convertLong(long j2735Long) {
        Double returnValue = null;
        if (j2735Long != 1800000001) {
            returnValue = j2735Long * 1e-7;
        }
        return returnValue;
    }

    /**
     * Converts a J2735 latitude value to degrees. Providing a range of plus-minus 90 degrees. LSB = 1/10 microdegree
     * 
     * @param j2735Lat J2735 latitude value.
     * @return Latitude in degrees, or null if unavailable.
     */
    public static Double convertLat(long j2735Lat) {
        Double returnValue = null;
        if (j2735Lat != 900000001) {
            returnValue = j2735Lat * 1e-7;
        }
        return returnValue;
    }

    /**
     * Converts a J2735 elevation value to meters. Providing a range of -409.5 to + 6143.9 meters. The value -4096 shall
     * be used when Unknown is to be sent.
     *
     * @param j2735Elev J2735 elevation value.
     * @return Elevation in meters, or null if unavailable.
     */
    public static Double convertElevation(long j2735Elev) {
        Double returnValue = null;
        if (j2735Elev != -4096) {
            returnValue = j2735Elev * 1e-1;
        }
        return returnValue;
    }
}
