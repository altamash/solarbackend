package com.solar.api.saas.mapper.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantSystemConfigurationDTO {

    private Long id;
    private Long orgId;
    private String orgName;
    private String regDate;
    private String tenancyState;
    private String expiryDate;
    private String lastRenewalDate;
    private String operationYear;
    private String region;
    private String ccy;
    private String language;
    private String targetSchema;
}
