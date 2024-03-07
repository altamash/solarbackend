package com.solar.api.saas.service.process.upload.v2.validation;

import java.util.List;
import java.util.Map;

public abstract class AbstractIntegrityCheck {

//    abstract void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue);

    /*ValidationResult healthCheckResult(List<Map> mappings, List<ValidationDTO> checks) {
        Set<Integer> checksRows = checks.stream().map(ValidationDTO::getLine).collect(Collectors.toSet());
        List<Integer> checksRowsOriginal = checksRows.stream().map(r -> r - 2).collect(Collectors.toList());
        List<Integer> correctRowIds = IntStream.range(0, mappings.size())
                .boxed()
                .filter(i -> !checksRowsOriginal.contains(i))
                .collect(Collectors.toList());
        return ValidationResult.builder()
                .totalRows(mappings.size())
                .totalCorrectRows(correctRowIds.size())
                .correctRowIdsList(correctRowIds)
                .correctRowIds(Joiner.on(", ").join(correctRowIds))
                .validationDTOs(checks)
                .build();
    }*/

    public abstract int checkDuplication(List<ValidationDTO> checks, List<Map> mappings, String... fields);

}
