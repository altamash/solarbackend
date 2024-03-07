package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.resources.HRHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HRHeadRepository extends JpaRepository<HRHead, Long> {

    List<HRHead> findAllByRegisterId(Long registerId);

    HRHead findByExternalReferenceId(String externalReferenceId);

    HRHead findByLoginUser(Long loginUser);

    List<HRHead> findAllByIdIn(List<Long> ids);

}
