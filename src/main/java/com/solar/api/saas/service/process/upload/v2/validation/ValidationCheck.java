package com.solar.api.saas.service.process.upload.v2.validation;

import java.util.List;
import java.util.Map;

public interface ValidationCheck {

    void validateRows(List<Map> mappings, List<ValidationDTO> checks, String uploadType, String customerType);
}
