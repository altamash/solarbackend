package com.solar.api.tenant.service;

import com.solar.api.tenant.model.user.TempPass;
import com.solar.api.tenant.repository.TempPassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TempPassServiceImpl implements TempPassService {

    @Autowired
    TempPassRepository tempPassRepository;

    @Override
    public List<TempPass> getAllTempPass() {
        return tempPassRepository.findAll();
    }

    @Override
    public void deleteAll() {
        tempPassRepository.deleteAll();
    }

    @Override
    public List<TempPass> saveAll(List<TempPass> tempPasses) {
        return tempPassRepository.saveAll(tempPasses);
    }

    @Override
    public TempPass saveOrUpdate(TempPass tempPass) {
        return tempPassRepository.save(tempPass);
    }
}
