package com.solar.api.tenant.mapper.billingCredits;

import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import com.solar.api.tenant.model.billingCredits.BillingCreditsTempStage;

import java.util.List;
import java.util.stream.Collectors;

public class BillingCreditsTempMapper {

    public static BillingCredits toBillingCredit(BillingCreditsCsv billingCreditsCsv) {
        return BillingCredits.builder()
                .id(billingCreditsCsv.getId())
                .importType(null)
                .jobId(null)
                .creditCodeType(billingCreditsCsv.getPaymentType())
                .gardenId(billingCreditsCsv.getGardenID())
                .mpa(Double.valueOf(billingCreditsCsv.getMonthlyProductionAllocationinkWh()))
                .tariffRate(Double.valueOf(billingCreditsCsv.getTariffRate()))
                .creditCodeVal(billingCreditsCsv.getPremiseNumber())
                .creditValue(Double.valueOf(billingCreditsCsv.getBillCredit()))
                .creditForDate(null)
                .subscriptionCode(null)
                .lineSeqNo(null)
                .calendarMonth(billingCreditsCsv.getCalendarMonth())
                .build();
    }

    public static BillingCreditsDTO toBillingCreditsDTO(BillingCreditsCsv billingCreditsCsv) {

        if (billingCreditsCsv == null) {
            return null;
        }

        return BillingCreditsDTO.builder()
                .id(billingCreditsCsv.getId())
                .importType(null)
                .jobId(null)
                .creditCodeType(billingCreditsCsv.getPaymentType())
                .gardenId(billingCreditsCsv.getGardenID())
                .mPA(Double.valueOf(billingCreditsCsv.getMonthlyProductionAllocationinkWh()))
                .tariffRate(Double.valueOf(billingCreditsCsv.getTariffRate()))
                .creditCodeVal(billingCreditsCsv.getPremiseNumber())
                .creditValue(Double.valueOf(billingCreditsCsv.getBillCredit()))
                .creditForDate(null)
                .subscriptionCode(null)
                .lineSeqNo(null)
                .calendarMonth(billingCreditsCsv.getCalendarMonth())
                .build();
    }
    public static BillingCredits toBillingCredit(BillingCreditsTempStage billingCreditsTempStage) {
        return BillingCredits.builder()
                .id(billingCreditsTempStage.getId())
                .importType(billingCreditsTempStage.getImportType())
                .jobId(billingCreditsTempStage.getJobId())
                .creditCodeType(billingCreditsTempStage.getCreditCodeType())
                .gardenId(billingCreditsTempStage.getGardenId())
                .mpa(billingCreditsTempStage.getMpa())
                .tariffRate(billingCreditsTempStage.getTariffRate())
                .creditCodeVal(billingCreditsTempStage.getCreditCodeVal())
                .creditValue(billingCreditsTempStage.getCreditValue())
                .creditForDate(billingCreditsTempStage.getCreditForDate())
                .subscriptionCode(billingCreditsTempStage.getSubscriptionCode())
                .lineSeqNo(billingCreditsTempStage.getLineSeqNo())
                .calendarMonth(billingCreditsTempStage.getCalendarMonth())
                .imported(billingCreditsTempStage.getImported())
                .build();
    }

    public static BillingCreditsTempStage toBillingCreditsTempStage(BillingCredits billingCredits) {

        if (billingCredits == null) {
            return null;
        }

        return BillingCreditsTempStage.builder()
                .id(billingCredits.getId())
                .importType(billingCredits.getImportType())
                .jobId(billingCredits.getJobId())
                .creditCodeType(billingCredits.getCreditCodeType())
                .gardenId(billingCredits.getGardenId())
                .mpa(billingCredits.getMpa())
                .tariffRate(billingCredits.getTariffRate())
                .creditCodeVal(billingCredits.getCreditCodeVal())
                .creditValue(billingCredits.getCreditValue())
                .creditForDate(billingCredits.getCreditForDate())
                .subscriptionCode(billingCredits.getSubscriptionCode())
                .lineSeqNo(billingCredits.getLineSeqNo())
                .calendarMonth(billingCredits.getCalendarMonth())
                .imported(billingCredits.getImported())
                .build();
    }

    public static BillingCreditsTempStage toBillingCreditTempStage(BillingCreditsCsv billingCreditsCsv) {
        return BillingCreditsTempStage.builder()
                .id(billingCreditsCsv.getId())
                .importType(null)
                .jobId(null)
                .creditCodeType(billingCreditsCsv.getPaymentType())
                .gardenId(billingCreditsCsv.getGardenID())
                .mpa(Double.valueOf(billingCreditsCsv.getMonthlyProductionAllocationinkWh()))
                .tariffRate(Double.valueOf(billingCreditsCsv.getTariffRate()))
                .creditCodeVal(billingCreditsCsv.getPremiseNumber())
                .creditValue(Double.valueOf(billingCreditsCsv.getBillCredit()))
                .creditForDate(null)
                .subscriptionCode(null)
                .lineSeqNo(null)
                .calendarMonth(billingCreditsCsv.getCalendarMonth())
                .build();
    }
    public static List<BillingCredits> toBillingCredits(List<BillingCreditsCsv> billingCreditsCsvs) {
        return billingCreditsCsvs.stream().map(cd -> toBillingCredit(cd)).collect(Collectors.toList());
    }

    public static List<BillingCreditsDTO> toBillingCreditsDTOs(List<BillingCreditsCsv> billingCreditsCsvs) {
        return billingCreditsCsvs.stream().map(cd -> toBillingCreditsDTO(cd)).collect(Collectors.toList());
    }

    public static List<BillingCredits> toBillingCreditsFromBillingCreditsTempStage(List<BillingCreditsTempStage> billingCreditsTempStageList) {
        return billingCreditsTempStageList.stream().map(cd -> toBillingCredit(cd)).collect(Collectors.toList());
    }

    public static List<BillingCreditsTempStage> toBillingCreditsTempStageFromBillingCredits(List<BillingCredits> billingCredits) {
        return billingCredits.stream().map(cd -> toBillingCreditsTempStage(cd)).collect(Collectors.toList());
    }
    public static List<BillingCreditsTempStage> toBillingCreditsTempStageFromBillingCreditsCsv(List<BillingCreditsCsv> billingCreditsCsvs) {
        return billingCreditsCsvs.stream().map(cd -> toBillingCreditTempStage(cd)).collect(Collectors.toList());
    }
}
