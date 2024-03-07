package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.partner.PartnerDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerDetailRepository extends JpaRepository<PartnerDetail, Long> {
}
