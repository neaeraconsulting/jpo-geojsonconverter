package us.dot.its.jpo.geojsonconverter.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

/**
 * JSON validator for RTCM messasges.
 * Validates J2735 requirements and ODE metadata.
 */
public class RTCMJsonValidator extends AbstractJsonValidator {

    public RTCMJsonValidator(@Value("${schema.rtcm}") Resource jsonSchemaResource) {
        super(jsonSchemaResource);
    }
}
