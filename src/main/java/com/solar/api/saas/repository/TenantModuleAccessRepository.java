package com.solar.api.saas.repository;

import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.model.tenant.TenantModuleAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantModuleAccessRepository extends JpaRepository<TenantModuleAccess, Long> {

    List<TenantModuleAccess> findByMasterTenant(MasterTenant masterTenant);
}
