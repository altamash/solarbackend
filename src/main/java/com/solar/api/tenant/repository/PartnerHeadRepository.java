package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.partner.PartnerHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartnerHeadRepository extends JpaRepository<PartnerHead, Long> {

    List<PartnerHead> findAllByRegisterId(Long registerId);
}
