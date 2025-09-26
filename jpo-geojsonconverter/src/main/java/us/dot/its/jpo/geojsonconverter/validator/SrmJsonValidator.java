package us.dot.its.jpo.geojsonconverter.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * JSON validator for SRM messages.
 */
@Service
public class SrmJsonValidator extends AbstractJsonValidator {
    /**
     * @param jsonSchemaResource The json schema file in resources/schemas.
     *                           Injected by Spring DI.
     */
    public SrmJsonValidator(@Value("${schema.srm}") Resource jsonSchemaResource) {
        super(jsonSchemaResource);
    }
}
