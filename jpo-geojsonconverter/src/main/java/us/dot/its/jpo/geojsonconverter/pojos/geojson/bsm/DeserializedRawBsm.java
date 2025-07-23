package us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeserializedRawBsm {

    OdeMessageFrameData odeBsmMessageFrameData;
    JsonValidatorResult validatorResults;
    boolean validationFailure = false;
    String failedMessage = null;

    @Override
    public String toString() {
        return "{" + " odeBsmMessageFrameData='" + getOdeBsmMessageFrameData() + "'" + ", validatorResults='"
                + getValidatorResults() + "'" + ", validationFailure='" + isValidationFailure() + "'"
                + ", failedMessage='" + getFailedMessage() + "'" + "}";
    }
}
