package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.process.JobManagerTenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobManagerTenantRepository extends JpaRepository<JobManagerTenant, Long> {

    Page<JobManagerTenant> findAll(Pageable pageable);

    JobManagerTenant findByJobName(String jobName);

    List<JobManagerTenant> findByBatchId(Long id);

    JobManagerTenant findByJobNameOrderByIdDesc(String jobName);

    @Query("SELECT max(jm.id) FROM JobManagerTenant jm where jm.jobName=:jobName and jm.status =:status")
    Long findIdOfLastJobByJobNameAndStatus(String jobName, String status);

    List<JobManagerTenant> findByJobNameAndStatus(String jobName, String status);
}
