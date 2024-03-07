package com.solar.api.saas.service;

import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcedureServiceImpl implements ProcedureService {

    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;

    @Override
    public void callAgingReport() {
        customerSubscriptionRepository.callAgingReport();
    }
}
