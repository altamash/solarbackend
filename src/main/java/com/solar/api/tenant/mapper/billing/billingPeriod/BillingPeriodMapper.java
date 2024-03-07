package com.solar.api.tenant.mapper.billing.billingPeriod;

import com.solar.api.tenant.model.billing.billingPeriod.BillingPeriod;

import java.util.List;
import java.util.stream.Collectors;

public class BillingPeriodMapper {

    public static BillingPeriod toBillingPeriod(BillingPeriodDTO billingPeriodDTO) {

        return BillingPeriod.builder()
                .id(billingPeriodDTO.getId())
                .subscriptionCode(billingPeriodDTO.getSubscriptionCode())
                .periodType(billingPeriodDTO.getPeriodType())
                .periodGap(billingPeriodDTO.getPeriodGap())
                .billingFinancialYear(billingPeriodDTO.getBillingFinancialYear())
                .startDate(billingPeriodDTO.getStartDate())
                .endDate(billingPeriodDTO.getEndDate())
                .status(billingPeriodDTO.getStatus())
                .comments(billingPeriodDTO.getComments())
                .build();
    }

    public static BillingPeriodDTO toBillingPeriodDTO(BillingPeriod billingPeriod) {

        if (billingPeriod == null) {
            return null;
        }
        return BillingPeriodDTO.builder()
                .id(billingPeriod.getId())
                .subscriptionCode(billingPeriod.getSubscriptionCode())
                .periodType(billingPeriod.getPeriodType())
                .periodGap(billingPeriod.getPeriodGap())
                .billingFinancialYear(billingPeriod.getBillingFinancialYear())
                .startDate(billingPeriod.getStartDate())
                .endDate(billingPeriod.getEndDate())
                .status(billingPeriod.getStatus())
                .comments(billingPeriod.getComments())
                .build();
    }

    public static BillingPeriod toUpdatedBillingPeriod(BillingPeriod billingPeriod, BillingPeriod billingPeriodUpdate) {
        billingPeriod.setSubscriptionCode(billingPeriodUpdate.getSubscriptionCode() == null ?
                billingPeriod.getSubscriptionCode() : billingPeriodUpdate.getSubscriptionCode());
        billingPeriod.setPeriodType(billingPeriodUpdate.getPeriodType() == null ?
                billingPeriod.getPeriodType() : billingPeriodUpdate.getPeriodType());
        billingPeriod.setPeriodGap(billingPeriodUpdate.getPeriodGap() == null ?
                billingPeriod.getPeriodGap() : billingPeriodUpdate.getPeriodGap());
        billingPeriod.setBillingFinancialYear(billingPeriodUpdate.getBillingFinancialYear() == null ?
                billingPeriod.getBillingFinancialYear() : billingPeriodUpdate.getBillingFinancialYear());
        billingPeriod.setStartDate(billingPeriodUpdate.getStartDate() == null ? billingPeriod.getStartDate() :
                billingPeriodUpdate.getStartDate());
        billingPeriod.setEndDate(billingPeriodUpdate.getEndDate() == null ? billingPeriod.getStartDate() :
                billingPeriodUpdate.getEndDate());
        billingPeriod.setStatus(billingPeriodUpdate.getStatus() == null ? billingPeriod.getStatus() :
                billingPeriodUpdate.getStatus());
        billingPeriod.setComments(billingPeriodUpdate.getComments() == null ? billingPeriod.getComments() :
                billingPeriodUpdate.getComments());
        return billingPeriod;
    }

    public static List<BillingPeriod> toBillingPeriods(List<BillingPeriodDTO> billingPeriodDTOS) {
        return billingPeriodDTOS.stream().map(bp -> toBillingPeriod(bp)).collect(Collectors.toList());
    }

    public static List<BillingPeriodDTO> toBillingPeriodDTOs(List<BillingPeriod> billingPeriods) {
        return billingPeriods.stream().map(bp -> toBillingPeriodDTO(bp)).collect(Collectors.toList());
    }
}
