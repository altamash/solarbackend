package com.solar.api.saas.repository;

import com.solar.api.saas.model.tenant.type.ETenantType;
import com.solar.api.saas.model.tenant.type.TenantType;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface TenantTypeRepository extends JpaRepository<TenantType, Long> {
    TenantType findByName(ETenantType name);
}
