package com.solar.api.saas.service.process.upload.v2.validation;

import com.google.common.base.Joiner;
import com.solar.api.tenant.model.CustomerStageTable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Validation {

    ValidationResult validate(List<?> stagedCustomers, String uploadType, String customerType);

    default ValidationResult getValidationSummary(List<Map> mappings, List<ValidationDTO> checks) {
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
    }
}
