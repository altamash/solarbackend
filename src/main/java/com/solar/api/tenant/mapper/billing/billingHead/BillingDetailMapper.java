package com.solar.api.tenant.mapper.billing.billingHead;

import com.solar.api.tenant.model.billing.billingHead.BillingDetail;

import java.util.List;
import java.util.stream.Collectors;

public class BillingDetailMapper {

    public static BillingDetail toBillingDetail(BillingDetailDTO billingDetailDTO) {

        return BillingDetail.builder()
                .id(billingDetailDTO.getId())
                .rateCode(billingDetailDTO.getRateCode())
                .value(billingDetailDTO.getValue())
                .lineSeqNo(billingDetailDTO.getLineSeqNo())
                .billingCode(billingDetailDTO.getBillingCode())
                .date(billingDetailDTO.getDate())
                .addToBillAmount(billingDetailDTO.getAddToBillAmount())
                .notes(billingDetailDTO.getNotes())
                .build();
    }

    public static BillingDetailDTO toBillingDetailDTO(BillingDetail billingDetail) {

        if (billingDetail == null) {
            return null;
        }
        return BillingDetailDTO.builder()
                .id(billingDetail.getId())
                .rateCode(billingDetail.getRateCode())
                .value(billingDetail.getValue())
                .lineSeqNo(billingDetail.getLineSeqNo())
                .billingCode(billingDetail.getBillingCode())
                .date(billingDetail.getDate())
                .addToBillAmount(billingDetail.getAddToBillAmount())
                .notes(billingDetail.getNotes())
                .build();
    }

    public static BillingDetail toUpdatedBillingDetail(BillingDetail billingDetail, BillingDetail billingDetailUpdate) {
        billingDetail.setRateCode(billingDetailUpdate.getRateCode() == null ? billingDetail.getRateCode() :
                billingDetailUpdate.getRateCode());
        billingDetail.setValue(billingDetailUpdate.getValue() == null ? billingDetail.getValue() :
                billingDetailUpdate.getValue());
        billingDetail.setLineSeqNo(billingDetailUpdate.getLineSeqNo() == null ? billingDetail.getLineSeqNo() :
                billingDetailUpdate.getLineSeqNo());
        billingDetail.setBillingCode(billingDetailUpdate.getBillingCode() == null ? billingDetail.getBillingCode() :
                billingDetailUpdate.getBillingCode());
        billingDetail.setDate(billingDetailUpdate.getDate() == null ? billingDetail.getDate() :
                billingDetailUpdate.getDate());
        billingDetail.setAddToBillAmount(billingDetailUpdate.getAddToBillAmount() == null ?
                billingDetail.getAddToBillAmount() : billingDetailUpdate.getAddToBillAmount());
        billingDetail.setNotes(billingDetail.getNotes() == null ? billingDetail.getNotes() :
                billingDetailUpdate.getNotes());
        return billingDetail;
    }

    public static List<BillingDetail> toBillingDetails(List<BillingDetailDTO> billingDetailDTOS) {
        return billingDetailDTOS.stream().map(bd -> toBillingDetail(bd)).collect(Collectors.toList());
    }

    public static List<BillingDetailDTO> toBillingDetailDTOs(List<BillingDetail> billingDetails) {
        return billingDetails.stream().map(bd -> toBillingDetailDTO(bd)).collect(Collectors.toList());
    }
}
