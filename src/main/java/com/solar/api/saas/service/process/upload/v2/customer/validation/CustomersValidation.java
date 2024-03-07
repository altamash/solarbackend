package com.solar.api.saas.service.process.upload.v2.customer.validation;

import com.solar.api.saas.service.process.upload.v2.validation.Validation;
import com.solar.api.saas.service.process.upload.v2.validation.ValidationDTO;

import java.util.List;
import java.util.Map;

public interface CustomersValidation extends Validation {
    void validateRows(List<Map> mappings, List<ValidationDTO> checks, String uploadType, String customerType);
}
