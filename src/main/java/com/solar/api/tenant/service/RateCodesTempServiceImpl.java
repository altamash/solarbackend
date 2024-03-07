package com.solar.api.tenant.service;

import com.solar.api.tenant.model.subscription.RateCodesTemp;
import com.solar.api.tenant.repository.RateCodesTempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateCodesTempServiceImpl implements RateCodesTempService {

    @Autowired
    RateCodesTempRepository rateCodesTempRepository;

    @Override
    public RateCodesTemp save(RateCodesTemp rateCodesTemp) {
        return rateCodesTempRepository.save(rateCodesTemp);
    }

    @Override
    public List<RateCodesTemp> saveAll(List<RateCodesTemp> rateCodesTempList) {
        return rateCodesTempRepository.saveAll(rateCodesTempList);
    }

    @Override
    public List<RateCodesTemp> getAll() {
        return rateCodesTempRepository.findAll();
    }
}
