package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;

import java.util.List;

public interface ExtDataStageDefinitionBillingService {

    ExtDataStageDefinitionBilling findBySubsId(String subId);
    List<ExtDataStageDefinitionBilling> findAllBySubIds(List<String> subIds);

    List<String> findVariantIdsBySrcNo(List<String> srcNo);

    List<ExtDataStageDefinitionBilling> findBySubStatus(String status);

    Long findLocIdByVariantId(String variantId);

    String findVariantAliasByVariantId(String variantId);
    List<ExtDataStageDefinitionBilling> findAllByRefId(String refId);

    ExtDataStageDefinitionBilling addOrUpdate(ExtDataStageDefinitionBilling extDataStageDefinitionBilling) throws AlreadyExistsException;
}
