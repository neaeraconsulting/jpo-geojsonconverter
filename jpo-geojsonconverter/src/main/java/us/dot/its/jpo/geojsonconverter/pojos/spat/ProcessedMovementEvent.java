
package us.dot.its.jpo.geojsonconverter.pojos.spat;

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
import us.dot.its.jpo.asn.j2735.r2024.SPAT.AdvisorySpeedList;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementPhaseState;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/**
 * Contains information regarding the dynamic flow of traffic in relation to a signal group.
 * <p>
 * eventState - The signal group's phase state.
 * <p>
 * timing - The collection of timing details for the signal group.
 * <p>
 * speeds - A list of advisory speeds for the signal group.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class ProcessedMovementEvent {
    private static Logger logger = LoggerFactory.getLogger(ProcessedMovementEvent.class);

    private MovementPhaseState eventState;
    private TimingChangeDetails timing;
    private AdvisorySpeedList speeds;

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
