package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.asn.runtime.serialization.OdeCustomJsonMapper;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.connectinglanes.ConnectingLanesFeatureCollection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"mapFeatureCollection", "connectingLanesFeatureCollection", "properties"})
@Slf4j
public class ProcessedMap<TGeometry> {
    private static Logger logger = LoggerFactory.getLogger(ProcessedMap.class);

    MapFeatureCollection<TGeometry> mapFeatureCollection;
    ConnectingLanesFeatureCollection<TGeometry> connectingLanesFeatureCollection;
    MapSharedProperties properties;

    @Override
    public String toString() {
        OdeCustomJsonMapper mapper = DateJsonMapper.getOdeInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return testReturn;
    }
}
