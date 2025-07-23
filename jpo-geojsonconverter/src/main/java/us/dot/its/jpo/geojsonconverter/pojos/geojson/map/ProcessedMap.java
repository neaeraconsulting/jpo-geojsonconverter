package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.connectinglanes.ConnectingLanesFeatureCollection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"mapFeatureCollection", "connectingLanesFeatureCollection", "properties"})
@Slf4j
public class ProcessedMap<TGeometry> {
    MapFeatureCollection<TGeometry> mapFeatureCollection;
    ConnectingLanesFeatureCollection<TGeometry> connectingLanesFeatureCollection;
    MapSharedProperties properties;
}
