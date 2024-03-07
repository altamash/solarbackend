package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasureBlockHeadRepository extends JpaRepository<MeasureBlockHead, Long> {

    List<MeasureBlockHead> findAllByRegModuleIdOrderByIdDesc(Long regModuleId);
    List<MeasureBlockHead> findAllByIdInOrderByIdAsc(List<Long> ids);

}
