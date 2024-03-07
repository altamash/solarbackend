package com.solar.api.saas.module.com.solar.scheduler.repository;

import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobSchedulerRepository extends JpaRepository<JobScheduler, Long> {

    JobScheduler findByJobNameAndCronExpression(String jobName, String cronExpression);

    JobScheduler findByJobInstanceId(Long id);

    List<JobScheduler> findByStatus(String status);

    List<JobScheduler> findByState(String state);

    JobScheduler findByJobName(String jobName);

    @Query("Select j FROM JobScheduler j where j.status='ACTIVE'")
    List<JobScheduler> findAll();

    @Query("Select js FROM JobScheduler js WHERE js.id = (SELECT max(j.id) FROM JobScheduler j where j.jobName= :jobName and j.status='ACTIVE') ")
    JobScheduler findLatestJobByJobName(@Param("jobName") String jobName);
}
