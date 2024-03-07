package com.solar.api.tenant.mapper.contract;

import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.mongo.response.subscription.MongoSubscriptionPMDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataTempStage;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StageMapper {

    public static List<ExtDataTempStage> fromMongotoSolarDTOList(List<MongoSubscriptionPMDTO> mongoSubscriptionPMMasterDTO) {
        return mongoSubscriptionPMMasterDTO.stream().map(StageMapper::fromMongotoSolarDTO).collect(Collectors.toList());
    }
    public static ExtDataTempStage fromMongotoSolarDTO(MongoSubscriptionPMDTO mongoSubscriptions) {
        if (mongoSubscriptions == null) {
            return null;
        }

        return ExtDataTempStage.builder().subsId(mongoSubscriptions.getSubscriptionId()).groupId(mongoSubscriptions.getProductId())
                .refId(mongoSubscriptions.getVariantId()).refType(mongoSubscriptions.getVariantAlias()).brand("E-GAUGE").monPlatform("E-GAUGE")
                .subsStatus(mongoSubscriptions.getSubscriptionStatus().toUpperCase()).endDate(new Date())
                .custAdd(mongoSubscriptions.getCustAdd())
                .subscriptionName(mongoSubscriptions.getSubscriptionName())
                .systemSize(mongoSubscriptions.getSystemSize())
                .siteLocationId(mongoSubscriptions.getSiteLocationId())
                .mpJson(mongoSubscriptions.getFilteredMeasure().replaceAll("\\\\", ""))
                .productName(mongoSubscriptions.getProductName())
                .LastFetchDate(new Date())
                .extJson(mongoSubscriptions.getExtJson().replaceAll("\\\\", ""))
                .build();
    }

    public static List<ExtDataStageDefinition> toStageDefinitions(List<ExtDataTempStage> extDataTempStage) {
        return extDataTempStage.stream().map(StageMapper::toStageDefinition).collect(Collectors.toList());
    }
    public static ExtDataStageDefinition toStageDefinition(ExtDataTempStage extDataTempStage) {
        if (extDataTempStage == null) {
            return null;
        }
        return ExtDataStageDefinition.builder().subsId(extDataTempStage.getSubsId()).groupId(extDataTempStage.getGroupId())
                .refId(extDataTempStage.getRefId()).refType(extDataTempStage.getRefType()).brand(extDataTempStage.getBrand())
                .subsStatus(extDataTempStage.getSubsStatus().toUpperCase()).endDate(new Date()).LastFetchDate(new Date())
                .custAdd(extDataTempStage.getCustAdd())
                .subscriptionName(extDataTempStage.getSubscriptionName())
                .systemSize(extDataTempStage.getSystemSize())
                .siteLocationId(extDataTempStage.getSiteLocationId())
                .productName((extDataTempStage.getProductName()))
                .mpJson(extDataTempStage.getMpJson().replaceAll("\\\\", ""))
                .monPlatform(extDataTempStage.getMonPlatform())
                .extJson(extDataTempStage.getExtJson()!=null? extDataTempStage.getExtJson().replaceAll("\\\\", ""):null)
                .build();

    }

    public static List<ExtDataTempStage> mapMonitoringPlatform(List<ExtDataTempStage> extDataTempStage) {

         extDataTempStage.forEach(m -> {
           String MP = Utility.getMeasureAsJson(m.getMpJson(), Constants.RATE_CODES.MP);
           m.setBrand(MP != null ? MP.toUpperCase() : "");
           m.setMonPlatform(MP != null ? MP.toUpperCase() : "");
         });
         return extDataTempStage;
    }
}
