package com.solar.api.tenant.service;

import com.solar.api.tenant.model.support.SupportStatusWorkflow;

import java.util.List;

public interface SupportStatusWorkflowService {

    List<SupportStatusWorkflow> add(List<SupportStatusWorkflow> supportStatusWorkflow);

    List<SupportStatusWorkflow> findAll();
}
