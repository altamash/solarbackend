package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegisterDetailRepository extends JpaRepository<RegisterDetail, Long> {

    RegisterDetail findByMeasureCode(String measureCode);

    @Query("SELECT rd.measureCode from RegisterDetail rd WHERE variableByDetail = :variableByDetail")
    List<String> findMeasureCodesByVariableByDetail(String variableByDetail);

    List<RegisterDetail> findByRegisterHead(RegisterHead registerHead);

    @Query("SELECT COUNT(rd.id) from RegisterDetail rd WHERE rd.measureCodeId=:measureCodeId")
    Long getCountByMeasureCodeId(Long measureCodeId);

    List<RegisterDetail> findAllByRegisterHeadAndMeasureBlockId(RegisterHead register, Long blockId);

    RegisterDetail findByRegisterHeadAndMeasureCodeId(RegisterHead register, Long measureCodeId);

    @Query("SELECT rd from RegisterDetail rd WHERE registerHead.id=:registerId and measureBlockId is not null order by measureCodeId asc")
    List<RegisterDetail> findAllByRegisterIdAndBlockIdNotNull(Long registerId);

}
