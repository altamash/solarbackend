package com.solar.api.tenant.repository.tansStage;


import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TransStageTempRepository extends JpaRepository<TransStageTemp, Long> {
    @Query("select tst from TransStageTemp tst where tst.tJob_id=:tJobId")
    List<TransStageTemp> findAllByTJobId(@Param("tJobId") Long tJobId);
}
