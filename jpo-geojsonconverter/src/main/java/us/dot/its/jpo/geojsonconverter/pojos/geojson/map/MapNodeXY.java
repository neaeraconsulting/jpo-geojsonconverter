package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/**
 * Nodes are described in terms of X and Y offsets in units of 1 cm. Each single selected node is computed as an X and Y
 * offset from the prior node point unless one of the entries reflecting a complete lat-long representation is selected.
 * <p>
 * x - X offset in units of 1 cm
 * <p>
 * y - Y offset in units of 1 cm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class MapNodeXY {
    private static Logger logger = LoggerFactory.getLogger(MapNodeXY.class);

    private Integer x;
    private Integer y;

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
