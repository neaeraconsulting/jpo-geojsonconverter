package us.dot.its.jpo.geojsonconverter.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

/**
 * JSON validator for SSM Messages.
 */
public class SsmJsonValidator extends AbstractJsonValidator {
    /**
     * @param jsonSchemaResource The json schema file in resources/schemas.
     */
    protected SsmJsonValidator(@Value("${schema.ssm}") Resource jsonSchemaResource) {
        super(jsonSchemaResource);
    }
}
