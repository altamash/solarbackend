package com.solar.api.tenant.service.tansStage;


import com.solar.api.tenant.model.billing.tansStage.TransStageHead;
import com.solar.api.tenant.repository.tansStage.TransStageHeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransStageHeadServiceImpl implements TransStageHeadService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private TransStageHeadRepository transStageHeadRepository;
    @Override
    public TransStageHead findBySubsId(String subsId) {
        return transStageHeadRepository.findBySubsId(subsId);
    }
    @Override
    public TransStageHead save(TransStageHead transStageHead) {
        Long stageId = stageIdGenerator();
        transStageHead.setStageId(stageId);
        return transStageHeadRepository.save(transStageHead);
    }

    @Override
    public List<TransStageHead> saveAll(List<TransStageHead> transStageHeadList) {
        Long stageId = stageIdGenerator();
        transStageHeadList.stream().forEach(transStageHead -> transStageHead.setStageId(stageId));
        return transStageHeadRepository.saveAll(transStageHeadList);
    }

    @Override
    public Long stageIdGenerator() {

        return (transStageHeadRepository.getLastStageId()!=null?transStageHeadRepository.getLastStageId():0) + 1;
    }
}
