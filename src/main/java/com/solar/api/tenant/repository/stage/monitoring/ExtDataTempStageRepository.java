package com.solar.api.tenant.repository.stage.monitoring;

import com.solar.api.tenant.model.stage.monitoring.ExtDataTempStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExtDataTempStageRepository extends JpaRepository<ExtDataTempStage, Long> {
    boolean existsBySubsId(String subsId);
    ExtDataTempStage findBySubsId(String subsId);

    @Query("SELECT ext FROM ExtDataTempStage ext WHERE ext.subsId = :subscriptionIds ")
    ExtDataTempStage findBySubscriptionIds(@Param("subscriptionIds") String subscriptionIds);
}
