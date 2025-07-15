package us.dot.its.jpo.geojsonconverter.pojos.spat;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class TimingChangeDetails {
    private static Logger logger = LoggerFactory.getLogger(TimingChangeDetails.class);

    private ZonedDateTime startTime;
    private ZonedDateTime minEndTime;
    private ZonedDateTime maxEndTime;
    private ZonedDateTime likelyTime;
    private Integer confidence;
    private ZonedDateTime nextTime;

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        mapper.registerModule(new JavaTimeModule());
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return testReturn;
    }
}
