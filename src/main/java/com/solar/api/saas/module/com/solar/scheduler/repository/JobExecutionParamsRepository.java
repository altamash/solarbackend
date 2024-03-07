package com.solar.api.saas.module.com.solar.scheduler.repository;

import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobExecutionParamsRepository extends JpaRepository<JobExecutionParams, Long> {

    List<JobExecutionParams> findByScheduledJobId(Long id);

    @Query("Select jep FROM JobExecutionParams jep WHERE jep.scheduledJobId =:jobId and jep.keyString= :keyString")
    JobExecutionParams getByScheduleJobIdAndKeyString(@Param("jobId") Long jobId, @Param("keyString") String keyString);

}
