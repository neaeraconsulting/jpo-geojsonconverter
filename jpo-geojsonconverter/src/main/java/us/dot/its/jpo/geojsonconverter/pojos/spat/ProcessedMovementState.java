package us.dot.its.jpo.geojsonconverter.pojos.spat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to convey various information about the current or future movement state of a designated collection of one or
 * more lanes of a common type.
 * <p>
 * movementName - Human readable name of the intersection movement state.
 * <p>
 * signalGroup - The signal group ID associated with the movement state. Used for mapping to lists of lanes.
 * <p>
 * stateTimeSpeed - A set of movement data with phase and timing information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class ProcessedMovementState {
    private static Logger logger = LoggerFactory.getLogger(ProcessedMovementState.class);

    private String movementName;
    private Integer signalGroup;
    private List<ProcessedMovementEvent> stateTimeSpeed = null;

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
