package com.solar.api.helper.Acquisition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AcquisitionUtils {
    public static JsonNode getMeasuresArray(String jsonObject) {
        String jsonString = jsonObject;

        // Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse the JSON string into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract the "sections" array
            JsonNode sectionsNode = rootNode.get("sections");

            if (sectionsNode != null && sectionsNode.isArray()) {
                for (JsonNode sectionNode : sectionsNode) {
                    // Extract the "content" object within each section
                    JsonNode contentNode = sectionNode.get("content");

                    if (contentNode != null) {
                        // Extract the "measures" array within the content object
                        JsonNode measuresNode = contentNode.get("measures");
                        return measuresNode;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getSubCategory(String jsonObject) {
        String jsonString = jsonObject;

// Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode subCategoryNode = rootNode.get("sub_category");
            return subCategoryNode.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String addEntityIdInTemplate(String template, Long entityId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(template);

            // Add a new key-value pair
            if (jsonNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.put("entity_id", entityId);
            }

            // Convert the JSON object back to a JSON string
            String updatedJsonString = objectMapper.writeValueAsString(jsonNode);
            return updatedJsonString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
