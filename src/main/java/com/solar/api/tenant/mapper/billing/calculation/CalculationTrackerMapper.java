package com.solar.api.tenant.mapper.billing.calculation;

import com.solar.api.tenant.model.billing.calculation.CalculationTracker;

import java.util.List;
import java.util.stream.Collectors;

public class CalculationTrackerMapper {

    public static CalculationTracker toCalculationTracker(CalculationTrackerDTO calculationTrackerDTO) {
        if (calculationTrackerDTO == null) {
            return null;
        }
        return CalculationTracker.builder()
                .id(calculationTrackerDTO.getId())
                .calcRefType(calculationTrackerDTO.getCalcRefType())
                .billingPeriod(calculationTrackerDTO.getBillingPeriod())
                .periodStartDate(calculationTrackerDTO.getPeriodStartDate())
                .periodEndDate(calculationTrackerDTO.getPeriodEndDate())
                .state(calculationTrackerDTO.getState())
                .refId(calculationTrackerDTO.getRefId())
                .build();
    }

    public static CalculationTrackerDTO toCalculationTrackerDTO(CalculationTracker calculationTracker) {
        if (calculationTracker == null) {
            return null;
        }
        return CalculationTrackerDTO.builder()
                .id(calculationTracker.getId())
                .calcRefType(calculationTracker.getCalcRefType())
                .billingPeriod(calculationTracker.getBillingPeriod())
                .periodStartDate(calculationTracker.getPeriodStartDate())
                .periodEndDate(calculationTracker.getPeriodEndDate())
                .state(calculationTracker.getState())
                .refId(calculationTracker.getRefId())
                .createdAt(calculationTracker.getCreatedAt())
                .updatedAt(calculationTracker.getUpdatedAt())
                .build();
    }

    public static CalculationTracker toUpdatedCalculationTracker(CalculationTracker calculationTracker, CalculationTracker calculationTrackerUpdate) {
        calculationTracker.setId(calculationTrackerUpdate.getId() == null ?
                calculationTracker.getId() :
                calculationTrackerUpdate.getId());
        calculationTracker.setCalcRefType(calculationTrackerUpdate.getCalcRefType() == null ?
                calculationTracker.getCalcRefType() :
                calculationTrackerUpdate.getCalcRefType());
        calculationTracker.setBillingPeriod(calculationTrackerUpdate.getBillingPeriod() == null ?
                calculationTracker.getBillingPeriod() : calculationTrackerUpdate.getBillingPeriod());
        calculationTracker.setPeriodStartDate(calculationTrackerUpdate.getPeriodStartDate() == null ?
                calculationTracker.getPeriodStartDate() : calculationTrackerUpdate.getPeriodStartDate());
        calculationTracker.setPeriodEndDate(calculationTrackerUpdate.getPeriodEndDate() == null ?
                calculationTracker.getPeriodEndDate() : calculationTrackerUpdate.getPeriodEndDate());
        calculationTracker.setRefId(calculationTrackerUpdate.getRefId() == null ?
                calculationTracker.getRefId() : calculationTrackerUpdate.getRefId());
        calculationTracker.setState(calculationTrackerUpdate.getState() == null ?
                calculationTracker.getState() : calculationTrackerUpdate.getState());
        return calculationTracker;
    }

    public static List<CalculationTracker> toCalculationTrackers(List<CalculationTrackerDTO> calculationTrackerDTOList) {
        if (calculationTrackerDTOList == null) {
            return null;
        }
        return calculationTrackerDTOList.stream().map(cr -> toCalculationTracker(cr)).collect(Collectors.toList());
    }

    public static List<CalculationTrackerDTO> toCalculationTrackerDTOs(List<CalculationTracker> calculationTrackerList) {
        if (calculationTrackerList == null) {
            return null;
        }
        return calculationTrackerList.stream().map(cr -> toCalculationTrackerDTO(cr)).collect(Collectors.toList());
    }

}
