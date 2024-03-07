package com.solar.api.saas.repository;

import com.solar.api.saas.model.extended.BatchDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchDefinitionRepository extends JpaRepository<BatchDefinition, Long> {

    BatchDefinition findByJobName(String jobName);
}
