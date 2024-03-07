package com.solar.api.tenant.service;

import com.solar.api.tenant.model.user.TempPass;

import java.util.List;

public interface TempPassService {

    List<TempPass> getAllTempPass();

    void deleteAll();

    List<TempPass> saveAll(List<TempPass> tempPasses);

    public TempPass saveOrUpdate(TempPass tempPass);
}
