package us.dot.its.jpo.geojsonconverter.pojos.geojson;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Point extends Geometry {
    private final Double[] coordinates;
    private final Double[] bbox;

    public Point(Double longitude, Double latitude) {
        super();
        this.coordinates = new Double[] {longitude, latitude};
        this.bbox = null;
    }

    @JsonCreator
    public Point(@JsonProperty("coordinates") Double[] coordinates) {
        super();
        this.coordinates = coordinates;
        this.bbox = null;
    }

    public Double[] getCoordinates() {
        return coordinates;
    }

    public Double[] getBbox() {
        return bbox;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Point)) {
            return false;
        }
        Point point = (Point) o;
        return Objects.equals(coordinates, point.coordinates) && Objects.equals(bbox, point.bbox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, bbox);
    }

    @Override
    public String toString() {
        return "{" + " coordinates='" + getCoordinates() + "'" + ", bbox='" + getBbox() + "'" + "}";
    }

}
