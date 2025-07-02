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
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.psm.PsmProperties;

/**
 * Represents a processed BSM (Basic Safety Message) J2735 AccelerationSet4Way.
 * <p>
 * accelLat - meters per second squared (m/s^2)
 * <p>
 * accelLong - meters per second squared (m/s^2)
 * <p>
 * accelVert - Gs (vertical acceleration)
 * <p>
 * accelYaw - degrees per second (yaw rate)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"accelLat", "accelLong", "accelVert", "accelYaw"})
@Slf4j
public class ProcessedBsmAccelerationSet4Way {
    private static Logger logger = LoggerFactory.getLogger(PsmProperties.class);

    private Double accelLat;
    private Double accelLong;
    private Double accelVert;
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
