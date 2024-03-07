package com.solar.api.saas.mapper.preferences;

import com.solar.api.saas.model.preferences.SystemConfiguration;

public class SystemConfigurationMapper {

    public static SystemConfiguration toSystemConfiguration(SystemConfigurationDTO systemConfigurationDTO) {
        return SystemConfiguration.builder()
                .id(systemConfigurationDTO.getId())
                .baseCurrency(systemConfigurationDTO.getBaseCurrency())
                .dateFormat(systemConfigurationDTO.getDateFormat())
                .region(systemConfigurationDTO.getRegion())
                .orgId(systemConfigurationDTO.getOrgId())
                .tenantId(systemConfigurationDTO.getTenantId())
                .language(systemConfigurationDTO.getLanguage())
                .adminHashcode(systemConfigurationDTO.getAdminHashcode())
                .admin(systemConfigurationDTO.getAdmin())
                .build();
    }

    public static SystemConfigurationDTO toSystemConfigurationDTO(SystemConfiguration systemConfiguration) {
        if (systemConfiguration == null) {
            return null;
        }
        return SystemConfigurationDTO.builder()
                .id(systemConfiguration.getId())
                .baseCurrency(systemConfiguration.getBaseCurrency())
                .dateFormat(systemConfiguration.getDateFormat())
                .region(systemConfiguration.getRegion())
                .orgId(systemConfiguration.getOrgId())
                .tenantId(systemConfiguration.getTenantId())
                .language(systemConfiguration.getLanguage())
                .adminHashcode(systemConfiguration.getAdminHashcode())
                .admin(systemConfiguration.getAdmin())
                .build();
    }
}
