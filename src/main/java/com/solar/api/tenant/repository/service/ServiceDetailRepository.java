package com.solar.api.tenant.repository.service;

import com.solar.api.tenant.model.extended.service.ServiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, Long> {
}
