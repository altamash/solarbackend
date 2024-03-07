package com.solar.api.tenant.mapper.preferences;

import com.solar.api.tenant.model.TenantConfig;

import java.util.List;
import java.util.stream.Collectors;

public class TenantConfigMapper {
    public static TenantConfig toTenantConfig(TenantConfigDTO tenantConfigDTO) {
        return TenantConfig.builder()
                .id(tenantConfigDTO.getId())
                .parameter(tenantConfigDTO.getParameter())
                .description(tenantConfigDTO.getDescription())
                .varType(tenantConfigDTO.getVarType())
                .number(tenantConfigDTO.getNumber())
                .text(tenantConfigDTO.getText())
                .dateTime(tenantConfigDTO.getDateTime())
                .prefix(tenantConfigDTO.getPrefix())
                .postfix(tenantConfigDTO.getPostfix())
                .format(tenantConfigDTO.getFormat())
                .category(tenantConfigDTO.getCategory())
                .locked(tenantConfigDTO.getLocked())
                .allowedRegex(tenantConfigDTO.getAllowedRegex())
                .masked(tenantConfigDTO.getMasked())
                .orgID(tenantConfigDTO.getOrgID())
                .alias(tenantConfigDTO.getAlias())
                .createdAt(tenantConfigDTO.getCreatedAt())
                .updatedAt(tenantConfigDTO.getUpdatedAt())
                .build();
    }

    public static TenantConfigDTO toTenantConfigDTO(TenantConfig tenantConfig) {
        if (tenantConfig == null) {
            return null;
        }

        return TenantConfigDTO.builder()
                .id(tenantConfig.getId())
                .parameter(tenantConfig.getParameter())
                .description(tenantConfig.getDescription())
                .varType(tenantConfig.getVarType())
                .number(tenantConfig.getNumber())
                .text(tenantConfig.getText())
                .dateTime(tenantConfig.getDateTime())
                .prefix(tenantConfig.getPrefix())
                .postfix(tenantConfig.getPostfix())
                .format(tenantConfig.getFormat())
                .category(tenantConfig.getCategory())
                .locked(tenantConfig.getLocked())
                .allowedRegex(tenantConfig.getAllowedRegex())
                .masked(tenantConfig.getMasked())
                .orgID(tenantConfig.getOrgID())
                .alias(tenantConfig.getAlias())
                .createdAt(tenantConfig.getCreatedAt())
                .updatedAt(tenantConfig.getUpdatedAt())
                .build();
    }

    public static TenantConfig toUpdateTenantConfig(TenantConfig tenantConfig, TenantConfig tenantConfigUpdate) {
        tenantConfig.setParameter(tenantConfigUpdate.getParameter() == null ? tenantConfig.getParameter() :
                tenantConfigUpdate.getParameter());
        tenantConfig.setDescription(tenantConfigUpdate.getDescription() == null ? tenantConfig.getDescription() :
                tenantConfigUpdate.getDescription());
        tenantConfig.setVarType(tenantConfigUpdate.getVarType() == null ? tenantConfig.getVarType() :
                tenantConfigUpdate.getVarType());
        tenantConfig.setNumber(tenantConfigUpdate.getNumber() == null ? tenantConfig.getNumber() :
                tenantConfigUpdate.getNumber());
        tenantConfig.setText(tenantConfigUpdate.getText() == null ? tenantConfig.getText() :
                tenantConfigUpdate.getText());
        tenantConfig.setDateTime(tenantConfigUpdate.getDateTime() == null ? tenantConfig.getDateTime() :
                tenantConfigUpdate.getDateTime());
        tenantConfig.setPrefix(tenantConfigUpdate.getPrefix() == null ? tenantConfig.getPrefix() :
                tenantConfigUpdate.getPrefix());
        tenantConfig.setPostfix(tenantConfigUpdate.getPostfix() == null ? tenantConfig.getPostfix() :
                tenantConfigUpdate.getPostfix());
        tenantConfig.setFormat(tenantConfigUpdate.getFormat() == null ? tenantConfig.getFormat() :
                tenantConfigUpdate.getFormat());
        tenantConfig.setCategory(tenantConfigUpdate.getCategory() == null ? tenantConfig.getCategory() :
                tenantConfigUpdate.getCategory());
        tenantConfig.setLocked(tenantConfigUpdate.getLocked() == null ? tenantConfig.getLocked() :
                tenantConfigUpdate.getLocked());
        tenantConfig.setAllowedRegex(tenantConfigUpdate.getAllowedRegex() == null ? tenantConfig.getAllowedRegex() :
                tenantConfigUpdate.getAllowedRegex());
        tenantConfig.setMasked(tenantConfigUpdate.getMasked() == null ? tenantConfig.getMasked() :
                tenantConfigUpdate.getMasked());
        tenantConfig.setOrgID(tenantConfigUpdate.getOrgID() == null ? tenantConfig.getOrgID() :
                tenantConfigUpdate.getOrgID());
        tenantConfig.setAlias(tenantConfigUpdate.getAlias() == null ? tenantConfig.getAlias() :
                tenantConfigUpdate.getAlias());
        return tenantConfig;
    }

    public static List<TenantConfig> toTenantConfigList(List<TenantConfigDTO> tenantConfigDTOList) {
        return tenantConfigDTOList.stream().map(TenantConfigMapper::toTenantConfig).collect(Collectors.toList());
    }

    public static List<TenantConfigDTO> toTenantConfigDTOList(List<TenantConfig> tenantConfigList) {
        return tenantConfigList.stream().map(TenantConfigMapper::toTenantConfigDTO).collect(Collectors.toList());
    }
}
