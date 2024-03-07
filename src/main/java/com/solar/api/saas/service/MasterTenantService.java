package com.solar.api.saas.service;

import com.solar.api.saas.mapper.tenant.MasterTenantDTO;
import com.solar.api.saas.model.tenant.MasterTenant;

import java.util.List;

public interface MasterTenantService {

    MasterTenant save(MasterTenant masterTenant);

    MasterTenant update(MasterTenant masterTenant);

    MasterTenant findById(Long id);

    MasterTenant findByUserName(String userName);

    MasterTenant findByUserNameFetchTenantRoles(String userName);

    MasterTenant findByCompanyKey(Long companyKey);

    MasterTenant findByCompanyCode(String companyCode);

    MasterTenant findByDbName(String dbName);

    MasterTenant setCurrentDb(Long id);

    List<MasterTenant> findAll();

    List<MasterTenant> findAllFetchTenantRoles();

    void delete(Long tenantClientId);

    void deleteAll();

    List<MasterTenantDTO> findAllByCompanyNameLike(String dbName);
    MasterTenantDTO findByLoginUrlLike(String keyword, boolean isMobileLanding);
}