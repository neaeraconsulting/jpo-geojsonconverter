package us.dot.its.jpo.geojsonconverter.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * JSON validator for SSM Messages.
 */
@Service
public class SsmJsonValidator extends AbstractJsonValidator {
    /**
     * @param jsonSchemaResource The json schema file in resources/schemas.
     */
    public SsmJsonValidator(@Value("${schema.ssm}") Resource jsonSchemaResource) {
        super(jsonSchemaResource);
    }
}
