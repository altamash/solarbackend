package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.saas.model.extended.BatchDefinition;

import java.util.List;

public interface BatchDefinitionService {

    BatchDefinition save(BatchDefinition batchDefinition);

    BatchDefinition update(BatchDefinition batchDefinition);

    BatchDefinition findById(Long id);

    BatchDefinition findByJobName(String jobName);

    List<BatchDefinition> findAll();

    void delete(Long id);

    void deleteAll();
}
