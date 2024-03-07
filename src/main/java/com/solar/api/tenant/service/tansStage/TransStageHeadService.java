package com.solar.api.tenant.service.tansStage;


import com.solar.api.tenant.model.billing.tansStage.TransStageHead;

import java.util.List;

public interface TransStageHeadService {
    TransStageHead findBySubsId (String subsId);
    TransStageHead save (TransStageHead transStageHead);
    List<TransStageHead> saveAll(List<TransStageHead> transStageHeadList);

    Long stageIdGenerator();

}
