package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.extended.BatchDefinitionMapper;
import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.repository.BatchDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class BatchDefinitionServiceImpl implements BatchDefinitionService {

    @Autowired
    private BatchDefinitionRepository batchDefinitionRepository;

    @Override
    public BatchDefinition save(BatchDefinition batchDefinition) {
        if (batchDefinition.getId() != null) {
            BatchDefinition batchDefinitionData = findById(batchDefinition.getId());
            BatchDefinitionMapper.toUpdatedBatchDefinition(batchDefinitionData, batchDefinition);
            return batchDefinitionRepository.save(batchDefinitionData);
        }
        return batchDefinitionRepository.save(batchDefinition);
    }

    @Override
    public BatchDefinition update(BatchDefinition batchDefinition) {
        return batchDefinitionRepository.save(batchDefinition);
    }

    @Override
    public BatchDefinition findById(Long id) {
        return batchDefinitionRepository.findById(id).orElseThrow(()-> new NotFoundException(BatchDefinition.class, id));
    }

    @Override
    public BatchDefinition findByJobName(String jobName) {
        return batchDefinitionRepository.findByJobName(jobName) == null ? batchDefinitionRepository.findByJobName(jobName) : batchDefinitionRepository.findByJobName(jobName);
    }

    @Override
    public List<BatchDefinition> findAll() {
        return batchDefinitionRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        batchDefinitionRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        batchDefinitionRepository.deleteAll();
    }
}
