package com.solar.api.tenant.mapper;

import com.solar.api.tenant.model.AnalyticalCalculation;
import com.solar.api.tenant.model.AnalyticalCalculationArchive;

import java.util.List;
import java.util.stream.Collectors;

public class AnalyticalCalculationMapper {

    public static AnalyticalCalculation toAnalyticalCalculation(AnalyticalCalculationDTO analyticalCalculationDTO) {
        if (analyticalCalculationDTO == null) {
            return null;
        }
        return AnalyticalCalculation.builder()
                .id(analyticalCalculationDTO.getId())
                .accountId(analyticalCalculationDTO.getAccountId())
                .subscriptionId(analyticalCalculationDTO.getSubscriptionId())
                .scope(analyticalCalculationDTO.getScope())
                .analysis(analyticalCalculationDTO.getAnalysis())
                .oldValue(analyticalCalculationDTO.getOldValue())
                .currentValue(analyticalCalculationDTO.getCurrentValue())
                .movingAverage(analyticalCalculationDTO.getMovingAverage())
                .lastUpdatedDatetime(analyticalCalculationDTO.getLastUpdatedDatetime())
                .build();
    }

    public static AnalyticalCalculationDTO toAnalyticalCalculationDTO(AnalyticalCalculation analyticalCalculation) {
        if (analyticalCalculation == null) {
            return null;
        }
        return AnalyticalCalculationDTO.builder()
                .id(analyticalCalculation.getId())
                .accountId(analyticalCalculation.getAccountId())
                .subscriptionId(analyticalCalculation.getSubscriptionId())
                .scope(analyticalCalculation.getScope())
                .analysis(analyticalCalculation.getAnalysis())
                .oldValue(analyticalCalculation.getOldValue())
                .currentValue(analyticalCalculation.getCurrentValue())
                .movingAverage(analyticalCalculation.getMovingAverage())
                .lastUpdatedDatetime(analyticalCalculation.getLastUpdatedDatetime())
                .build();
    }

    public static AnalyticalCalculation toUpdatedAnalyticalCalculation(AnalyticalCalculation analyticalCalculation, AnalyticalCalculation analyticalCalculationUpdate) {
        analyticalCalculation.setAccountId(analyticalCalculationUpdate.getAccountId() == null ? analyticalCalculation.getAccountId() : analyticalCalculationUpdate.getAccountId());
        analyticalCalculation.setSubscriptionId(analyticalCalculationUpdate.getSubscriptionId() == null ? analyticalCalculation.getSubscriptionId() : analyticalCalculationUpdate.getSubscriptionId());
        analyticalCalculation.setScope(analyticalCalculationUpdate.getScope() == null ? analyticalCalculation.getScope() : analyticalCalculationUpdate.getScope());
        analyticalCalculation.setAnalysis(analyticalCalculationUpdate.getAnalysis() == null ? analyticalCalculation.getAnalysis() : analyticalCalculationUpdate.getAnalysis());
        analyticalCalculation.setOldValue(analyticalCalculationUpdate.getOldValue() == null ? analyticalCalculation.getOldValue() : analyticalCalculationUpdate.getOldValue());
        analyticalCalculation.setCurrentValue(analyticalCalculationUpdate.getCurrentValue() == null ? analyticalCalculation.getCurrentValue() : analyticalCalculationUpdate.getCurrentValue());
        analyticalCalculation.setMovingAverage(analyticalCalculationUpdate.getMovingAverage() == null ? analyticalCalculation.getMovingAverage() : analyticalCalculationUpdate.getMovingAverage());
        analyticalCalculation.setLastUpdatedDatetime(analyticalCalculationUpdate.getLastUpdatedDatetime() == null ? analyticalCalculation.getLastUpdatedDatetime() : analyticalCalculationUpdate.getLastUpdatedDatetime());
        return analyticalCalculation;
    }

    public static AnalyticalCalculation toUpdatedAnalyticalCalculationTemp(AnalyticalCalculation analyticalCalculationOld, AnalyticalCalculationArchive analyticalCalculationArchiveUpdate) {
        AnalyticalCalculation analyticalCalculation = new AnalyticalCalculation();
        analyticalCalculation.setId(analyticalCalculationOld.getId());
        analyticalCalculation.setAccountId(analyticalCalculationArchiveUpdate.getAccountId());
        analyticalCalculation.setSubscriptionId(analyticalCalculationArchiveUpdate.getSubscriptionId());
        analyticalCalculation.setScope(analyticalCalculationArchiveUpdate.getScope());
        analyticalCalculation.setAnalysis(analyticalCalculationArchiveUpdate.getAnalysis());
        analyticalCalculation.setOldValue(analyticalCalculationArchiveUpdate.getOldValue());
        analyticalCalculation.setCurrentValue(analyticalCalculationArchiveUpdate.getCurrentValue());
        analyticalCalculation.setMovingAverage(analyticalCalculationArchiveUpdate.getMovingAverage());
        analyticalCalculation.setLastUpdatedDatetime(analyticalCalculationArchiveUpdate.getLastUpdatedDatetime());
        return analyticalCalculation;
    }

    public static AnalyticalCalculationArchive toAnalyticalCalculationArchive(AnalyticalCalculation analyticalCalculation) {
        if (analyticalCalculation == null) {
            return null;
        }
        return AnalyticalCalculationArchive.builder()
                .id(analyticalCalculation.getId())
                .accountId(analyticalCalculation.getAccountId())
                .subscriptionId(analyticalCalculation.getSubscriptionId())
                .scope(analyticalCalculation.getScope())
                .analysis(analyticalCalculation.getAnalysis())
                .oldValue(analyticalCalculation.getOldValue())
                .currentValue(analyticalCalculation.getCurrentValue())
                .movingAverage(analyticalCalculation.getMovingAverage())
                .lastUpdatedDatetime(analyticalCalculation.getLastUpdatedDatetime())
                .build();
    }

    public static List<AnalyticalCalculationArchive> toAnalyticalCalculationArchives(List<AnalyticalCalculation> analyticalCalculationList) {
        return analyticalCalculationList.stream().map(c -> toAnalyticalCalculationArchive(c)).collect(Collectors.toList());
    }

    public static List<AnalyticalCalculation> toAnalyticalCalculations(List<AnalyticalCalculationDTO> analyticalCalculationDTOS) {
        return analyticalCalculationDTOS.stream().map(c -> toAnalyticalCalculation(c)).collect(Collectors.toList());
    }

    public static List<AnalyticalCalculationDTO> toAnalyticalCalculationDTOs(List<AnalyticalCalculation> analyticalCalculationList) {
        return analyticalCalculationList.stream().map(c -> toAnalyticalCalculationDTO(c)).collect(Collectors.toList());
    }
}
