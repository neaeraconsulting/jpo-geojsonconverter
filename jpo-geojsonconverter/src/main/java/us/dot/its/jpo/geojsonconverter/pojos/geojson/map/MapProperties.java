package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.asn.j2735.r2024.MapData.AllowedManeuvers;
import us.dot.its.jpo.asn.j2735.r2024.MapData.ConnectsToList;
import us.dot.its.jpo.asn.j2735.r2024.MapData.LaneSharing;
import us.dot.its.jpo.asn.j2735.r2024.MapData.LaneTypeAttributes;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class MapProperties {
    private static Logger logger = LoggerFactory.getLogger(MapProperties.class);

    private List<MapNode> nodes;
    private Integer laneId;
    private String laneName;
    private LaneTypeAttributes laneType;
    private LaneSharing sharedWith; // enum is of type J2735LaneSharing
    private Integer egressApproach;
    private Integer ingressApproach;
    private Boolean ingressPath;
    private Boolean egressPath;
    private AllowedManeuvers maneuvers; // enum is of type J2735AllowedManeuvers
    private ConnectsToList connectsTo;

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
