package com.solar.api.saas.mapper.tenant;

import com.solar.api.saas.model.tenant.MasterTenant;

import java.util.List;
import java.util.stream.Collectors;

public class MasterTenantMapper {
    public static MasterTenant toMasterTenant(MasterTenantDTO masterTenantDTO) {
        if (masterTenantDTO == null) {
            return null;
        }
        return MasterTenant.builder()
                .id(masterTenantDTO.getId())
                .jwtToken(masterTenantDTO.getJwtToken())
                .dbName(masterTenantDTO.getDbName())
                .url(masterTenantDTO.getUrl())
                .userName(masterTenantDTO.getUserName())
                .email(masterTenantDTO.getEmail())
                .passCode(masterTenantDTO.getPassCode())
                .driverClass(masterTenantDTO.getDriverClass())
                .status(masterTenantDTO.getStatus())
                .companyCode(masterTenantDTO.getCompanyCode())
                .companyKey(masterTenantDTO.getCompanyKey())
                .companyName(masterTenantDTO.getCompanyName())
                .companyLogo(masterTenantDTO.getCompanyLogo())
                .tenantTier(masterTenantDTO.getTenantTier())
                .type(masterTenantDTO.getType())
                .roles(masterTenantDTO.getRoles())
                .build();
    }

    public static MasterTenantDTO toMasterTenantDTO(MasterTenant masterTenant) {
        if (masterTenant == null) {
            return null;
        }
        return MasterTenantDTO.builder()
                .id(masterTenant.getId())
                .jwtToken(masterTenant.getJwtToken())
                .dbName(masterTenant.getDbName())
                .url(masterTenant.getUrl())
                .userName(masterTenant.getUserName())
                .email(masterTenant.getEmail())
                .passCode(masterTenant.getPassCode())
                .driverClass(masterTenant.getDriverClass())
                .status(masterTenant.getStatus())
                .companyCode(masterTenant.getCompanyCode())
                .companyKey(masterTenant.getCompanyKey())
                .companyName(masterTenant.getCompanyName())
                .companyLogo(masterTenant.getCompanyLogo())
                .tenantTier(masterTenant.getTenantTier())
                .type(masterTenant.getType())
                .roles(masterTenant.getTenantRoles().stream().map(r -> r.getName().getName()).collect(Collectors.toSet()))
                .createdAt(masterTenant.getCreatedAt())
                .updatedAt(masterTenant.getUpdatedAt())
                .build();
    }

    public static MasterTenant toUpdatedMasterTenant(MasterTenant masterTenant, MasterTenant masterTenantUpdate) {
        masterTenant.setJwtToken(masterTenantUpdate.getJwtToken() == null ? masterTenant.getJwtToken() : masterTenantUpdate.getJwtToken());
        masterTenant.setDbName(masterTenantUpdate.getDbName() == null ? masterTenant.getDbName() : masterTenantUpdate.getDbName());
        masterTenant.setUrl(masterTenantUpdate.getUrl() == null ? masterTenant.getUrl() : masterTenantUpdate.getUrl());
        masterTenant.setUserName(masterTenantUpdate.getUserName() == null ? masterTenant.getUserName() : masterTenantUpdate.getUserName());
        masterTenant.setEmail(masterTenantUpdate.getEmail() == null ? masterTenant.getEmail() : masterTenantUpdate.getEmail());
        masterTenant.setPassCode(masterTenantUpdate.getPassCode() == null ? masterTenant.getPassCode() : masterTenantUpdate.getPassCode());
        masterTenant.setDriverClass(masterTenantUpdate.getDriverClass() == null ? masterTenant.getDriverClass() : masterTenantUpdate.getDriverClass());
        masterTenant.setStatus(masterTenantUpdate.getStatus() == null ? masterTenant.getStatus() : masterTenantUpdate.getStatus());
        masterTenant.setCompanyCode(masterTenantUpdate.getCompanyCode() == null ? masterTenant.getCompanyCode() : masterTenantUpdate.getCompanyCode());
        masterTenant.setCompanyKey(masterTenantUpdate.getCompanyKey() == null ? masterTenant.getCompanyKey() : masterTenantUpdate.getCompanyKey());
        masterTenant.setCompanyName(masterTenantUpdate.getCompanyName() == null ? masterTenant.getCompanyName() : masterTenantUpdate.getCompanyName());
        masterTenant.setCompanyLogo(masterTenantUpdate.getCompanyLogo() == null ? masterTenant.getCompanyLogo() : masterTenantUpdate.getCompanyLogo());
        masterTenant.setTenantTier(masterTenantUpdate.getTenantTier() == null ? masterTenant.getTenantTier() : masterTenantUpdate.getTenantTier());
        masterTenant.setType(masterTenantUpdate.getType() == null ? masterTenant.getType() : masterTenantUpdate.getType());
        masterTenant.setRoles(masterTenantUpdate.getRoles() == null ? masterTenant.getRoles() : masterTenantUpdate.getRoles());

        return masterTenant;
    }

    public static List<MasterTenant> toMasterTenants(List<MasterTenantDTO> masterTenantDTOs) {
        return masterTenantDTOs.stream().map(mt -> toMasterTenant(mt)).collect(Collectors.toList());
    }

    /**
     * @param masterTenant
     * @return
     */
    public static List<MasterTenantDTO> toMasterTenantDTOs(List<MasterTenant> masterTenant) {
        return masterTenant.stream().map(mt -> toMasterTenantDTO(mt)).collect(Collectors.toList());
    }
}
