package us.dot.its.jpo.geojsonconverter.pojos.common;

import java.util.LinkedHashMap;

/**
 * Base class for processed BITSTRINGs.
 * <p>Serializes a verbose map of name: boolean pairs</p>
 */
public abstract class ProcessedBitstring extends LinkedHashMap<String, Boolean> {

    protected ProcessedBitstring(String... keys) {
        // Initialize with insertion-order constructor with capacity
        super(keys.length);
        initialize(keys);
    }

    protected void initialize(String... keys) {
        for (String key : keys) {
            put(key, false);
        }
    }

}
