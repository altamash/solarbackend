package com.solar.api.tenant.repository.service;

import com.solar.api.tenant.model.extended.service.ServiceResources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceResourcesRepository extends JpaRepository<ServiceResources, Long> {
}
