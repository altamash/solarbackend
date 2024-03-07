package com.solar.api.saas.mapper.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemConfigurationDTO {

    private Long id;
    private String baseCurrency;
    private String dateFormat;
    private String region;
    private Long orgId;
    private String tenantId;
    private String language;
    private String adminHashcode;
    private String admin;

}
