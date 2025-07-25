package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.Common.Position3D;
import us.dot.its.jpo.geojsonconverter.converter.FieldConversions;

@Data
@Generated
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class MapRefPoint {
        private Double latitude;
        private Double longitude;
        private Double elevation;

        /**
         * Sets the latitude, longitude, and elevation fields of this MapRefPoint from a J2735 Position3D object. Uses
         * MapFieldConversions to convert ASN.1 encoded values to standard double values.
         *
         * @param refPoint the Position3D object containing the reference point data
         */
        public void setFromPosition3D(Position3D refPoint) {
                this.latitude = refPoint.getLat() != null ? FieldConversions.convertLat(refPoint.getLat().getValue())
                                : null;
                this.longitude = refPoint.getLong_() != null
                                ? FieldConversions.convertLong(refPoint.getLong_().getValue())
                                : null;
                this.elevation = refPoint.getElevation() != null
                                ? FieldConversions.convertElevation(refPoint.getElevation().getValue())
                                : null;
        }
}
