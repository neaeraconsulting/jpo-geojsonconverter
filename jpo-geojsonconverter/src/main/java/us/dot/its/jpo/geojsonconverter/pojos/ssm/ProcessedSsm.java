package us.dot.its.jpo.geojsonconverter.pojos.ssm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Generated;

/**
 * A processed J2735 SignalStatusMessageFrame.
 * <p>Similar to a SPAT, the SSM is associated with an intersection, but does not contain geographic information
 * itself.</p>
 */
@Generated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessedSsm {

}
