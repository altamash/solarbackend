package com.solar.api.tenant.mapper.billingCredits;

import com.solar.api.tenant.mapper.billing.billingHead.CsvHeaderDTO;
import com.solar.api.tenant.model.billingCredits.BillingCredits;

import java.util.List;
import java.util.stream.Collectors;

public class BillingCreditsMapper {

    public static BillingCredits toBillingCredit(BillingCreditsDTO billingCreditsDTO) {

        return BillingCredits.builder()
                .id(billingCreditsDTO.getId())
                .importType(billingCreditsDTO.getImportType())
                .jobId(billingCreditsDTO.getJobId())
                .creditCodeType(billingCreditsDTO.getCreditCodeType())
                .gardenId(billingCreditsDTO.getGardenId())
                .mpa(billingCreditsDTO.getMPA())
                .tariffRate(billingCreditsDTO.getTariffRate())
                .creditCodeVal(billingCreditsDTO.getCreditCodeVal())
                .creditValue(billingCreditsDTO.getCreditValue())
                .creditForDate(billingCreditsDTO.getCreditForDate())
                .subscriptionCode(billingCreditsDTO.getSubscriptionCode())
                .lineSeqNo(billingCreditsDTO.getLineSeqNo())
                .calendarMonth(billingCreditsDTO.getCalendarMonth())
                .build();
    }

    public static CsvHeaderDTO toCsvHeaderDTO(CsvHeaderDTO csvHeaderDTO) {

        return CsvHeaderDTO.builder()
                .headers(csvHeaderDTO.getHeaders())
                .values(csvHeaderDTO.getValues())
                .build();
    }

    public static BillingCreditsDTO toBillingCreditsDTO(BillingCredits billingCredits) {

        if (billingCredits == null) {
            return null;
        }
        return BillingCreditsDTO.builder()
                .id(billingCredits.getId())
                .importType(billingCredits.getImportType())
                .jobId(billingCredits.getJobId())
                .creditCodeType(billingCredits.getCreditCodeType())
                .gardenId(billingCredits.getGardenId())
                .mPA(billingCredits.getMpa())
                .tariffRate(billingCredits.getTariffRate())
                .creditCodeVal(billingCredits.getCreditCodeVal())
                .creditValue(billingCredits.getCreditValue())
                .creditForDate(billingCredits.getCreditForDate())
                .subscriptionCode(billingCredits.getSubscriptionCode())
                .lineSeqNo(billingCredits.getLineSeqNo())
                .calendarMonth(billingCredits.getCalendarMonth())
                .build();
    }

    public static List<BillingCredits> toBillingCredits(List<BillingCreditsDTO> billingCreditsDTOS) {
        return billingCreditsDTOS.stream().map(bc -> toBillingCredit(bc)).collect(Collectors.toList());
    }

    public static List<BillingCreditsDTO> toBillingCreditsDTOs(List<BillingCredits> billingCredits) {
        return billingCredits.stream().map(bc -> toBillingCreditsDTO(bc)).collect(Collectors.toList());
    }
}
