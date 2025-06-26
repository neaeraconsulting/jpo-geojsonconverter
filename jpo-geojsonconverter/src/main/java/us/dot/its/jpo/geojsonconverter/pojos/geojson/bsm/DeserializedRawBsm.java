package us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm;

import java.util.Objects;

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
    Boolean validationFailure = false;
    String failedMessage = null;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DeserializedRawBsm)) {
            return false;
        }
        DeserializedRawBsm deserializedRawBsm = (DeserializedRawBsm) o;
        return Objects.equals(odeBsmMessageFrameData, deserializedRawBsm.odeBsmMessageFrameData)
                && Objects.equals(validatorResults, deserializedRawBsm.validatorResults)
                && Objects.equals(validationFailure, deserializedRawBsm.validationFailure)
                && Objects.equals(failedMessage, deserializedRawBsm.failedMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(odeBsmMessageFrameData, validatorResults, validationFailure, failedMessage);
    }

    @Override
    public String toString() {
        return "{" + " odeBsmMessageFrameData='" + getOdeBsmMessageFrameData() + "'" + ", validatorResults='"
                + getValidatorResults() + "'" + ", validationFailure='" + getValidationFailure() + "'"
                + ", failedMessage='" + getFailedMessage() + "'" + "}";
    }
}
