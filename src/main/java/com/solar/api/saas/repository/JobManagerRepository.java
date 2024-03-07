package com.solar.api.saas.repository;

import com.solar.api.saas.model.JobManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobManagerRepository extends JpaRepository<JobManager, Long> {

    Page<JobManager> findAll(Pageable pageable);

    JobManager findByJobName(String jobName);
}
