package us.dot.its.jpo.geojsonconverter.pojos.geojson.srm;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Generated;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.BaseFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;

/**
 * A point feature representing a processed J2735 SignalRequestMessageFrame.
 * <p>The geometry is the point location of the vehicle that broadcast the SRM</p>
 */
@Generated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessedSrm extends BaseFeature<Void, Point, SrmProperties> {
    @JsonCreator
    public ProcessedSrm(@JsonProperty("geometry") Point geometry,
                        @JsonProperty("properties") SrmProperties srmProperties) {
        super(null, geometry, srmProperties);
    }
}
