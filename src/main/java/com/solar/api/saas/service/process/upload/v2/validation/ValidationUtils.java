package com.solar.api.saas.service.process.upload.v2.validation;

import java.util.List;
import java.util.Map;

public class ValidationUtils {

    public static void addParseError(List<ValidationDTO> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(ValidationDTO.builder()
                .line(line + 2)
//                .entityId(mapping.get("entity_id") == null ? "" : (String) mapping.get("entity_id"))
//                .action(mapping.get("action") == null ? "" : (String) mapping.get("action"))
                .issue(issue)
                .build());
    }
}
