package com.solar.api.tenant.service.tansStage;


import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.repository.tansStage.TransStageTempRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransStageTempServiceImpl implements TransStageTempService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private TransStageTempRepository transStageTempRepository;


    @Override
    public TransStageTemp save(TransStageTemp transStageTemp) {
        return transStageTempRepository.save(transStageTemp);
    }

    @Override
    public List<TransStageTemp> saveAll(List<TransStageTemp> transStageTempList) {
        return transStageTempRepository.saveAll(transStageTempList);
    }

    @Override
    public void delete(TransStageTemp transStageTemp) {
        transStageTempRepository.delete(transStageTemp);
    }

    @Override
    public void deleteAll(List<TransStageTemp> transStageTempList) {
        transStageTempRepository.deleteAll(transStageTempList);
    }

    @Override
    public void deleteById(Long id) {
        transStageTempRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        transStageTempRepository.deleteAllById(ids);
    }

    @Override
    public List<TransStageTemp> findAllByTJobId(Long tJobId) {
        return transStageTempRepository.findAllByTJobId(tJobId);
    }

    @Override
    public void deleteAll() {
        transStageTempRepository.deleteAll();
    }

}
