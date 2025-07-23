package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MapNode {
    private Integer[] delta;
    private Integer dWidth;
    private Integer dElevation;
    private Boolean stopLine = null;
}
