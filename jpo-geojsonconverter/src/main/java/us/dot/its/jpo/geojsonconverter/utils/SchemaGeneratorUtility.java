package us.dot.its.jpo.geojsonconverter.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.psm.ProcessedPsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.rtcm.ProcessedRTCM;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.ProcessedSrm;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SchemaGeneratorUtility {
    public static void main(String[] args) throws IOException {
        try {
            // Define the classes for which to generate schemas
            Class<?>[] targetClasses =
                    {ProcessedPsm.class, ProcessedBsm.class, ProcessedMap.class, ProcessedSpat.class, ProcessedRTCM.class,
                    ProcessedSrm.class, ProcessedSsm.class};

            ObjectMapper objectMapper = new ObjectMapper();

            SchemaGeneratorConfigBuilder configBuilder =
                    new SchemaGeneratorConfigBuilder(objectMapper, SchemaVersion.DRAFT_2020_12,
                            OptionPreset.PLAIN_JSON);

            // JacksonModule with INCLUDE_ONLY_JSONPROPERTY_ANNOTATED_METHODS:
            configBuilder.with(new JacksonModule(JacksonOption.INCLUDE_ONLY_JSONPROPERTY_ANNOTATED_METHODS));

            configBuilder.with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                    .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
                    .with(Option.GETTER_METHODS)                 // needed for JacksonModule to process annotated getters
                    .without(Option.FLATTENED_ENUMS_FROM_TOSTRING)
                    .without(Option.NONSTATIC_NONVOID_NONGETTER_METHODS)
                    .without(Option.PUBLIC_NONSTATIC_FIELDS)
                    .with(Option.NONPUBLIC_NONSTATIC_FIELDS_WITH_GETTERS)
                    .without(Option.NONPUBLIC_NONSTATIC_FIELDS_WITHOUT_GETTERS);

            SchemaGenerator generator = new SchemaGenerator(configBuilder.build());

            // Find the project root directory
            Path currentPath = Paths.get("").toAbsolutePath();

            File resourcesDir = null;

            if (args.length > 0 && args[0].equals("--output")) {
                resourcesDir = new File(args[1]);
            } else {
                resourcesDir = new File(currentPath.toString(), "src/main/resources/schemas");
            }

            System.out.println("Creating schemas directory at: " + resourcesDir.getAbsolutePath());
            resourcesDir.mkdirs();

            // Generate schemas and save to files
            for (Class<?> targetClass : targetClasses) {
                JsonNode schema = generator.generateSchema(targetClass);

                // Post-process schema to add virtual @JsonProperty annotated getter methods
                schema = addVirtualProperties(schema, targetClass, objectMapper);

                // Create the schema file in the resources/schemas directory
                // Add hyphen after "Processed"
                String fileName = targetClass.getSimpleName().replaceAll("(?<=Processed)(?=\\w)", "-").toLowerCase()
                        + ".schema.json";
                File outputFile = new File(resourcesDir, fileName);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, schema);

                System.out.println(
                        "Generated schema for: " + targetClass.getSimpleName() + " at " + outputFile.getAbsolutePath());
            }

            System.out.println("Schema generation completed successfully.");
        } catch (Exception e) {
            System.err.println("Error generating schemas: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Exit successfully
        System.exit(0);
    }

    /**
     * Post-processes the generated schema to add virtual properties from @JsonProperty annotated getter methods
     * that may not have been automatically included by the schema generator.
     *
     * This is particularly useful for properties like utcTimeStampTS that are computed from another field
     * through a getter method annotated with @JsonProperty.
     *
     * @param schema The generated schema
     * @param targetClass The class for which the schema was generated
     * @param objectMapper The ObjectMapper used for JSON manipulation
     * @return The modified schema with virtual properties included
     */
    private static JsonNode addVirtualProperties(JsonNode schema, Class<?> targetClass, ObjectMapper objectMapper) {
        try {
            // Get or create the properties object in the schema
            if (!schema.has("properties")) {
                return schema;  // No properties defined, skip
            }

            // Create a mutable copy of the schema
            var schemaMap = objectMapper.convertValue(schema, java.util.LinkedHashMap.class);
            var propertiesMap = (java.util.LinkedHashMap<String, Object>) schemaMap.get("properties");

             // Reflect on all methods in the target class to find @JsonProperty annotated getters
             for (java.lang.reflect.Method method : targetClass.getDeclaredMethods()) {
                 JsonProperty jsonProp =
                         method.getAnnotation(JsonProperty.class);

                // Check if this is a getter method with @JsonProperty annotation
                if (jsonProp != null &&
                        method.getName().startsWith("get") &&
                        method.getParameterCount() == 0 &&
                        !method.getReturnType().equals(Void.TYPE)) {

                    String propertyName = jsonProp.value();

                    // Only add if not already in schema (don't override existing properties)
                    if (!propertiesMap.containsKey(propertyName)) {
                        // Infer the schema for this property based on return type
                        Class<?> returnType = method.getReturnType();
                        java.util.LinkedHashMap<String, Object> propertySchema = inferPropertySchema(returnType);
                        propertiesMap.put(propertyName, propertySchema);
                        System.out.println("  Added virtual property '" + propertyName + "' to schema");
                    }
                }
            }

            // Convert back to JsonNode
            return objectMapper.valueToTree(schemaMap);
        } catch (Exception e) {
            System.err.println("Warning: Could not add virtual properties to schema for " + targetClass.getName() + ": " + e.getMessage());
            return schema;
        }
    }

    /**
     * Infers a JSON schema property definition based on a Java class type.
     *
     * @param returnType The Java class type
     * @return A map representing the JSON schema for this type
     */
    private static java.util.LinkedHashMap<String, Object> inferPropertySchema(Class<?> returnType) {
        var propertySchema = new java.util.LinkedHashMap<String, Object>();

        if (returnType == String.class) {
            propertySchema.put("type", "string");
        } else if (returnType == Integer.class || returnType == int.class) {
            propertySchema.put("type", "integer");
            propertySchema.put("format", "int32");
        } else if (returnType == Long.class || returnType == long.class) {
            propertySchema.put("type", "integer");
            propertySchema.put("format", "int64");
        } else if (returnType == Double.class || returnType == double.class ||
                   returnType == Float.class || returnType == float.class) {
            propertySchema.put("type", "number");
        } else if (returnType == Boolean.class || returnType == boolean.class) {
            propertySchema.put("type", "boolean");
        } else if (returnType.getName().equals("java.time.Instant") ||
                   returnType.getName().equals("java.time.LocalDateTime")) {
            propertySchema.put("type", "string");
            propertySchema.put("format", "date-time");
        } else if (returnType == java.util.List.class || returnType.isArray()) {
            propertySchema.put("type", "array");
            propertySchema.put("items", new java.util.LinkedHashMap<>());
        } else {
            // For unknown types, use a generic object definition
            propertySchema.put("type", "object");
        }

        return propertySchema;
     }
}
