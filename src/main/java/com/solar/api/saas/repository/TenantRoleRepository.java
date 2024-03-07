package com.solar.api.saas.repository;

import com.solar.api.saas.model.tenant.role.ETenantRole;
import com.solar.api.saas.model.tenant.role.TenantRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRoleRepository extends JpaRepository<TenantRole, Long> {
    TenantRole findByName(ETenantRole name);
}
