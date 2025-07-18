package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.asn.j2735.r2024.Common.IntersectionReferenceID;
import us.dot.its.jpo.asn.j2735.r2024.Common.SpeedLimitList;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.ode.model.OdeMessageFrameMetadata.Source;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class MapSharedProperties {
    private static Logger logger = LoggerFactory.getLogger(MapSharedProperties.class);

    // Default schemaVersion is -1 for older messages that lack a schemaVersion value
    private int schemaVersion = -1;
    private String messageType = "MAP";
    private ZonedDateTime odeReceivedAt;
    private String originIp;
    private String intersectionName;
    private Integer region;
    private Integer intersectionId;
    private Integer msgIssueRevision;
    private Integer revision;
    private MapRefPoint refPoint;
    private Boolean cti4501Conformant;
    private List<ProcessedValidationMessage> validationMessages;
    private Integer laneWidth;
    private SpeedLimitList speedLimits;
    private Source mapSource;
    private ZonedDateTime timeStamp;

    /**
     * Sets both intersection ID and region with null checks
     * 
     * @param referenceID IntersectionReferenceID
     */
    public void setIntersectionReferenceID(IntersectionReferenceID referenceID) {
        if (referenceID != null) {
            if (referenceID.getId() != null) {
                setIntersectionId((int) referenceID.getId().getValue());
            }
            if (referenceID.getRegion() != null) {
                setRegion((int) referenceID.getRegion().getValue());
            }
        }
    }

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return testReturn;
    }
}
