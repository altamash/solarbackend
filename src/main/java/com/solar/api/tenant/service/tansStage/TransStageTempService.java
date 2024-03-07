package com.solar.api.tenant.service.tansStage;


import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;

import java.util.List;

public interface TransStageTempService {
    TransStageTemp save(TransStageTemp transStageTemp);

    List<TransStageTemp> saveAll(List<TransStageTemp> transStageTempList);

    void delete(TransStageTemp transStageTemp);

    void deleteAll(List<TransStageTemp> transStageTempList);
    void deleteAll();

    void deleteById(Long id);

    void deleteAllById(List<Long> ids);

    List<TransStageTemp> findAllByTJobId(Long tJobId);
}
