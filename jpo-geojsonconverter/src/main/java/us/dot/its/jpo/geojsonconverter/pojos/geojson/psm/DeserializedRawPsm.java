package us.dot.its.jpo.geojsonconverter.pojos.geojson.psm;

import java.util.Objects;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;

public class DeserializedRawPsm {

    OdeMessageFrameData odePsmMessageFrameData;
    JsonValidatorResult validatorResults;
    Boolean validationFailure = false;
    String failedMessage = null;

    public OdeMessageFrameData getOdePsmMessageFrameData() {
        return this.odePsmMessageFrameData;
    }

    public void setOdePsmMessageFrameData(OdeMessageFrameData odePsmMessageFrameData) {
        this.odePsmMessageFrameData = odePsmMessageFrameData;
    }

    public JsonValidatorResult getValidatorResults() {
        return this.validatorResults;
    }

    public void setValidatorResults(JsonValidatorResult validatorResults) {
        this.validatorResults = validatorResults;
    }

    public Boolean getValidationFailure() {
        return this.validationFailure;
    }

    public void setValidationFailure(Boolean validationFailure) {
        this.validationFailure = validationFailure;
    }

    public String getFailedMessage() {
        return this.failedMessage;
    }

    public void setFailedMessage(String failedMessage) {
        this.failedMessage = failedMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DeserializedRawPsm)) {
            return false;
        }
        DeserializedRawPsm deserializedRawPsm = (DeserializedRawPsm) o;
        return Objects.equals(odePsmMessageFrameData, deserializedRawPsm.odePsmMessageFrameData)
                && Objects.equals(validatorResults, deserializedRawPsm.validatorResults)
                && Objects.equals(validationFailure, deserializedRawPsm.validationFailure)
                && Objects.equals(failedMessage, deserializedRawPsm.failedMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(odePsmMessageFrameData, validatorResults, validationFailure, failedMessage);
    }

    @Override
    public String toString() {
        return "{" + " odeSpatOdeSpat='" + getOdePsmMessageFrameData() + "'" + ", validatorResults='"
                + getValidatorResults() + "'" + ", validationFailure='" + getValidationFailure() + "'"
                + ", failedMessage='" + getFailedMessage() + "'" + "}";
    }
}
