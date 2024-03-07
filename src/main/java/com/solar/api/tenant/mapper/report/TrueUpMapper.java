package com.solar.api.tenant.mapper.report;

import com.solar.api.tenant.model.report.TrueUp;

import java.util.List;
import java.util.stream.Collectors;

public class TrueUpMapper {

    public static TrueUp toTrueUp(TrueUpDTO trueUpDTO) {
        if (trueUpDTO == null) {
            return null;
        }
        return TrueUp.builder()
                .id(trueUpDTO.getId())
                .acctId(trueUpDTO.getAcctId())
                .subscriptionId(trueUpDTO.getSubscriptionId())
                .startDate(trueUpDTO.getStartDate())
                .endDate(trueUpDTO.getEndDate())
                .period(trueUpDTO.getPeriod())
                .subscriptionType(trueUpDTO.getSubscriptionType())
                .reportUrl(trueUpDTO.getReportUrl())
                .build();
    }

    public static TrueUpDTO toReportTemplateDTO(TrueUp trueUp) {
        if (trueUp == null) {
            return null;
        }
        return TrueUpDTO.builder()
                .id(trueUp.getId())
                .acctId(trueUp.getAcctId())
                .subscriptionId(trueUp.getSubscriptionId())
                .startDate(trueUp.getStartDate())
                .endDate(trueUp.getEndDate())
                .period(trueUp.getPeriod())
                .subscriptionType(trueUp.getSubscriptionType())
                .reportUrl(trueUp.getReportUrl())
                .build();
    }

    public static TrueUp toUpdatedTrueUp(TrueUp trueUp,
                                         TrueUp trueUpUpdate) {
        trueUp.setAcctId(trueUpUpdate.getAcctId() == null ? trueUp.getAcctId() : trueUpUpdate.getAcctId());
        trueUp.setSubscriptionId(trueUpUpdate.getSubscriptionId() == null ? trueUp.getSubscriptionId() :
                trueUpUpdate.getSubscriptionId());
        trueUp.setStartDate(trueUpUpdate.getStartDate() == null ? trueUp.getStartDate() : trueUpUpdate.getStartDate());
        trueUp.setEndDate(trueUpUpdate.getEndDate() == null ? trueUp.getEndDate() : trueUpUpdate.getEndDate());
        trueUp.setPeriod(trueUpUpdate.getPeriod() == null ? trueUp.getPeriod() : trueUpUpdate.getPeriod());
        trueUp.setSubscriptionType(trueUpUpdate.getSubscriptionType() == null ? trueUp.getSubscriptionType() :
                trueUpUpdate.getSubscriptionType());
        trueUp.setReportUrl(trueUpUpdate.getReportUrl() == null ? trueUp.getReportUrl() : trueUpUpdate.getReportUrl());
        return trueUp;
    }

    public static List<TrueUp> toTrueUps(List<TrueUpDTO> TrueUpDTOs) {
        return TrueUpDTOs.stream().map(r -> toTrueUp(r)).collect(Collectors.toList());
    }

    public static List<TrueUpDTO> toTrueUpDTOs(List<TrueUp> TrueUps) {
        return TrueUps.stream().map(r -> toReportTemplateDTO(r)).collect(Collectors.toList());
    }
}
