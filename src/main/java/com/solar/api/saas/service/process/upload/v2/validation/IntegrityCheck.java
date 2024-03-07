package com.solar.api.saas.service.process.upload.v2.validation;

import java.util.List;
import java.util.Map;

public interface IntegrityCheck {

    void validateIntegrity(List<Map> mappings, List<ValidationDTO> checks);
}
