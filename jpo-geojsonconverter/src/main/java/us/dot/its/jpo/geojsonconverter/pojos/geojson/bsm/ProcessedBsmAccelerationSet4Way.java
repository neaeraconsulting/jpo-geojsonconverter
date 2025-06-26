package us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.psm.PsmProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"accelLat", "accelLong", "accelVert", "accelYaw"})
public class ProcessedBsmAccelerationSet4Way {
    private static Logger logger = LoggerFactory.getLogger(PsmProperties.class);

    // Acceleration latitude and longitude in m/s^2
    private Double accelLat;
    private Double accelLong;
    // Vertical acceleration in Gs
    private Double accelVert;
    // Yaw rate in degrees per second
    private Double accelYaw;

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return testReturn;
    }
}
