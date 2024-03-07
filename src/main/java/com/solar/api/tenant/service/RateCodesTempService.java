package com.solar.api.tenant.service;

import com.solar.api.tenant.model.subscription.RateCodesTemp;

import java.util.List;

public interface RateCodesTempService {

    RateCodesTemp save(RateCodesTemp rateCodesTemp);

    List<RateCodesTemp> saveAll(List<RateCodesTemp> rateCodesTempList);

    List<RateCodesTemp> getAll();
}
