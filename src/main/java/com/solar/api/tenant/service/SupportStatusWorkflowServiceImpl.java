package com.solar.api.tenant.service;

import com.solar.api.tenant.model.support.SupportStatusWorkflow;
import com.solar.api.tenant.repository.SupportStatusWorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class SupportStatusWorkflowServiceImpl implements SupportStatusWorkflowService {

    @Autowired
    SupportStatusWorkflowRepository supportStatusWorkflowRepository;

    @Override
    public List<SupportStatusWorkflow> add(List<SupportStatusWorkflow> supportStatusWorkflow) {
        return supportStatusWorkflowRepository.saveAll(supportStatusWorkflow);
    }

    @Override
    public List<SupportStatusWorkflow> findAll() {
        return supportStatusWorkflowRepository.findAll();
    }
}
