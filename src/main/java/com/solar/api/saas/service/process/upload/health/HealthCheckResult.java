package com.solar.api.saas.service.process.upload.health;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthCheckResult {

    private Integer totalRows;
    private String correctRowIds;
    private List<HealthCheck> healthChecks;
    private Integer totalCorrectRows;

    //private String errorMessage;
}
