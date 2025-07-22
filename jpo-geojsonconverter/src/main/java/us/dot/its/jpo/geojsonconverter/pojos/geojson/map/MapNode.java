package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapNode {
    private Integer[] delta;
    private Integer dWidth;
    private Integer dElevation;
    private Boolean stopLine = null;

    @Override
    public String toString() {
        return "{" + " delta='" + getDelta() + "'" + ", dWidth='" + getDWidth() + "'" + ", dElevation='"
                + getDElevation() + "'" + ", stopLine='" + getStopLine() + "'" + "}";
    }
}
