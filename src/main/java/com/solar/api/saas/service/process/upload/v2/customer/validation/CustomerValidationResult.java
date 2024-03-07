package com.solar.api.saas.service.process.upload.v2.customer.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.service.process.upload.v2.validation.ValidationResult;
import com.solar.api.tenant.model.CustomerStageTable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerValidationResult extends ValidationResult {

    private String correctStagedIds;
    private String stagedCustomersJson;
    private List<CustomerStageTable> stagedCustomers;

    void setValidationResult(ValidationResult healthCheckResult) {
        this.setTotalRows(healthCheckResult.getTotalRows());
        this.setCorrectRowIdsList(healthCheckResult.getCorrectRowIdsList());
        this.setCorrectRowIds(healthCheckResult.getCorrectRowIds());
        this.setValidationDTOs(healthCheckResult.getValidationDTOs());
        this.setTotalCorrectRows(healthCheckResult.getTotalCorrectRows());
        this.setUploadId(healthCheckResult.getUploadId());
    }
}
