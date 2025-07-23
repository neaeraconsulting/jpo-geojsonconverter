package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.BaseFeatureCollection;

@Slf4j
public class MapFeatureCollection<TGeometry> extends BaseFeatureCollection<MapFeature<TGeometry>> {
    @JsonCreator
    public MapFeatureCollection(@JsonProperty("features") MapFeature<TGeometry>[] features) {
        super(features);
    }
}
