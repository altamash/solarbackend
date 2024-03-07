package com.solar.api.tenant.mapper.billing.calculation;

import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;

public class ExtDataStageDefinitionBillingMapper {

    public static ExtDataStageDefinitionBilling toUpdatedExtDataStageDefinitionBilling(ExtDataStageDefinitionBilling extDataStageDefinitionBilling, ExtDataStageDefinitionBilling extDataStageDefinitionBillingUpdate) {
        extDataStageDefinitionBilling.setId(extDataStageDefinitionBillingUpdate.getId() == null ?
                extDataStageDefinitionBilling.getId() :
                extDataStageDefinitionBillingUpdate.getId());
        extDataStageDefinitionBilling.setUserId(extDataStageDefinitionBillingUpdate.getUserId() == null ?
                extDataStageDefinitionBilling.getUserId() :
                extDataStageDefinitionBillingUpdate.getUserId());
        extDataStageDefinitionBilling.setRefType(extDataStageDefinitionBillingUpdate.getRefType() == null ?
                extDataStageDefinitionBilling.getRefType() : extDataStageDefinitionBillingUpdate.getRefType());
        extDataStageDefinitionBilling.setRefId(extDataStageDefinitionBillingUpdate.getRefId() == null ?
                extDataStageDefinitionBilling.getRefId() : extDataStageDefinitionBillingUpdate.getRefId());
        extDataStageDefinitionBilling.setSubsId(extDataStageDefinitionBillingUpdate.getSubsId() == null ?
                extDataStageDefinitionBilling.getSubsId()  : extDataStageDefinitionBillingUpdate.getSubsId());
        extDataStageDefinitionBilling.setSubscriptionType(extDataStageDefinitionBillingUpdate.getSubscriptionType() == null ?
                extDataStageDefinitionBilling.getSubscriptionType() : extDataStageDefinitionBillingUpdate.getSubscriptionType());
        extDataStageDefinitionBilling.setVariantAlias(extDataStageDefinitionBillingUpdate.getVariantAlias() == null ?
                extDataStageDefinitionBilling.getVariantAlias() : extDataStageDefinitionBillingUpdate.getVariantAlias());
        extDataStageDefinitionBilling.setParserCode(extDataStageDefinitionBillingUpdate.getParserCode() == null ?
                extDataStageDefinitionBilling.getParserCode() : extDataStageDefinitionBillingUpdate.getParserCode());
        extDataStageDefinitionBilling.setPhysicalLocationId(extDataStageDefinitionBillingUpdate.getPhysicalLocationId() == null ?
                extDataStageDefinitionBilling.getPhysicalLocationId() : extDataStageDefinitionBillingUpdate.getPhysicalLocationId());
        extDataStageDefinitionBilling.setBillingJson(extDataStageDefinitionBillingUpdate.getBillingJson() == null ?
                extDataStageDefinitionBilling.getBillingJson() : extDataStageDefinitionBillingUpdate.getBillingJson());
        extDataStageDefinitionBilling.setStartDate(extDataStageDefinitionBillingUpdate.getStartDate() == null ?
                extDataStageDefinitionBilling.getStartDate() : extDataStageDefinitionBillingUpdate.getStartDate());
        extDataStageDefinitionBilling.setEndDate(extDataStageDefinitionBillingUpdate.getEndDate() == null ?
                extDataStageDefinitionBilling.getEndDate() : extDataStageDefinitionBillingUpdate.getEndDate());
        extDataStageDefinitionBilling.setSubsStatus(extDataStageDefinitionBillingUpdate.getSubsStatus() == null ?
                extDataStageDefinitionBilling.getSubsStatus() : extDataStageDefinitionBillingUpdate.getSubsStatus());
        extDataStageDefinitionBilling.setBillingCycle(extDataStageDefinitionBillingUpdate.getBillingCycle() == null ?
                extDataStageDefinitionBilling.getBillingCycle() : extDataStageDefinitionBillingUpdate.getBillingCycle());
        extDataStageDefinitionBilling.setBillingFrequency(extDataStageDefinitionBillingUpdate.getBillingFrequency() == null ?
                extDataStageDefinitionBilling.getBillingFrequency() : extDataStageDefinitionBillingUpdate.getBillingFrequency());
        extDataStageDefinitionBilling.setTerminationDate(extDataStageDefinitionBillingUpdate.getTerminationDate() == null ?
                extDataStageDefinitionBilling.getTerminationDate() : extDataStageDefinitionBillingUpdate.getTerminationDate());
        extDataStageDefinitionBilling.setClosedDate(extDataStageDefinitionBillingUpdate.getClosedDate() == null ?
                extDataStageDefinitionBilling.getClosedDate() : extDataStageDefinitionBillingUpdate.getClosedDate());
        extDataStageDefinitionBilling.setTerminationReason(extDataStageDefinitionBillingUpdate.getTerminationReason() == null ?
                extDataStageDefinitionBilling.getTerminationReason() : extDataStageDefinitionBillingUpdate.getTerminationReason());
        extDataStageDefinitionBilling.setPreGenerate(extDataStageDefinitionBillingUpdate.getPreGenerate() == null ?
                extDataStageDefinitionBilling.getPreGenerate() : extDataStageDefinitionBillingUpdate.getPreGenerate());
        extDataStageDefinitionBilling.setCategory(extDataStageDefinitionBillingUpdate.getCategory() == null ?
                extDataStageDefinitionBilling.getCategory() : extDataStageDefinitionBillingUpdate.getCategory());
        return extDataStageDefinitionBilling;
    }
}
