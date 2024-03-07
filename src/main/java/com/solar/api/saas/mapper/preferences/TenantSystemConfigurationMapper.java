package com.solar.api.saas.mapper.preferences;

import com.solar.api.saas.model.preferences.TenantSystemConfiguration;

public class TenantSystemConfigurationMapper {

    public static TenantSystemConfiguration toSystemConfiguration(TenantSystemConfigurationDTO tenantSystemConfigurationDTO) {
        return TenantSystemConfiguration.builder()
                .id(tenantSystemConfigurationDTO.getId())
                .orgId(tenantSystemConfigurationDTO.getOrgId())
                .orgName(tenantSystemConfigurationDTO.getOrgName())
                .regDate(tenantSystemConfigurationDTO.getRegDate())
                .tenancyState(tenantSystemConfigurationDTO.getTenancyState())
                .expiryDate(tenantSystemConfigurationDTO.getExpiryDate())
                .lastRenewalDate(tenantSystemConfigurationDTO.getLastRenewalDate())
                .operationYear(tenantSystemConfigurationDTO.getOperationYear())
                .region(tenantSystemConfigurationDTO.getRegion())
                .ccy(tenantSystemConfigurationDTO.getCcy())
                .language(tenantSystemConfigurationDTO.getLanguage())
                .targetSchema(tenantSystemConfigurationDTO.getTargetSchema())
                .build();
    }

    public static TenantSystemConfigurationDTO toSystemConfigurationDTO(TenantSystemConfiguration tenantSystemConfiguration) {
        if (tenantSystemConfiguration == null) {
            return null;
        }
        return TenantSystemConfigurationDTO.builder()
                .id(tenantSystemConfiguration.getId())
                .orgId(tenantSystemConfiguration.getOrgId())
                .orgName(tenantSystemConfiguration.getOrgName())
                .regDate(tenantSystemConfiguration.getRegDate())
                .tenancyState(tenantSystemConfiguration.getTenancyState())
                .expiryDate(tenantSystemConfiguration.getExpiryDate())
                .lastRenewalDate(tenantSystemConfiguration.getLastRenewalDate())
                .operationYear(tenantSystemConfiguration.getOperationYear())
                .region(tenantSystemConfiguration.getRegion())
                .ccy(tenantSystemConfiguration.getCcy())
                .language(tenantSystemConfiguration.getLanguage())
                .targetSchema(tenantSystemConfiguration.getTargetSchema())
                .build();
    }
}
