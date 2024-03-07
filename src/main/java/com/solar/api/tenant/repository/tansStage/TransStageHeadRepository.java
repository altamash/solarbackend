package com.solar.api.tenant.repository.tansStage;


import com.solar.api.tenant.model.billing.tansStage.TransStageHead;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TransStageHeadRepository extends JpaRepository<TransStageHead, Long> {

    @Query("Select max(tsh.stageId) from TransStageHead tsh")
    Long getLastStageId();

    @Query("select tsh from TransStageHead tsh where tsh.subsId=:subsId")
    TransStageHead findBySubsId(@Param("subsId") String subsId);
}
