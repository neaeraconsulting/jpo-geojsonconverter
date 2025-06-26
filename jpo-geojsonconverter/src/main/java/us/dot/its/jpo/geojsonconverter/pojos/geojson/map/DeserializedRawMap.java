package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import java.util.Objects;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;

public class DeserializedRawMap {

    OdeMessageFrameData odeMapMessageFrameData;
    JsonValidatorResult validatorResults;
    Boolean validationFailure = false;
    String failedMessage = null;

    public OdeMessageFrameData getOdeMapMessageFrameData() {
        return this.odeMapMessageFrameData;
    }

    public void setOdeMapMessageFrameData(OdeMessageFrameData odeMapMessageFrameData) {
        this.odeMapMessageFrameData = odeMapMessageFrameData;
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
        if (!(o instanceof DeserializedRawMap)) {
            return false;
        }
        DeserializedRawMap deserializedRawMap = (DeserializedRawMap) o;
        return Objects.equals(odeMapMessageFrameData, deserializedRawMap.odeMapMessageFrameData)
                && Objects.equals(validatorResults, deserializedRawMap.validatorResults)
                && Objects.equals(validationFailure, deserializedRawMap.validationFailure)
                && Objects.equals(failedMessage, deserializedRawMap.failedMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(odeMapMessageFrameData, validatorResults, validationFailure, failedMessage);
    }


    @Override
    public String toString() {
        return "{" + " odeMapOdeSpat='" + getOdeMapMessageFrameData() + "'" + ", validatorResults='"
                + getValidatorResults() + "'" + ", validationFailure='" + getValidationFailure() + "'"
                + ", failedMessage='" + getFailedMessage() + "'" + "}";
    }



}
