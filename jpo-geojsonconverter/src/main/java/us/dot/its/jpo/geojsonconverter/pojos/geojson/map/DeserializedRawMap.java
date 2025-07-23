package us.dot.its.jpo.geojsonconverter.pojos.geojson.map;

import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DeserializedRawMap {
    OdeMessageFrameData odeMapMessageFrameData;
    JsonValidatorResult validatorResults;
    boolean validationFailure = false;
    String failedMessage = null;
}
