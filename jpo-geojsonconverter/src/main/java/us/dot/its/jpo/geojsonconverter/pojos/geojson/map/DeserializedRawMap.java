package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeserializedRawMap {

    OdeMessageFrameData odeMapMessageFrameData;
    JsonValidatorResult validatorResults;
    boolean validationFailure = false;
    String failedMessage = null;

    @Override
    public String toString() {
        return "{" + " odeMapOdeSpat='" + getOdeMapMessageFrameData() + "'" + ", validatorResults='"
                + getValidatorResults() + "'" + ", validationFailure='" + isValidationFailure() + "'"
                + ", failedMessage='" + getFailedMessage() + "'" + "}";
    }



}
