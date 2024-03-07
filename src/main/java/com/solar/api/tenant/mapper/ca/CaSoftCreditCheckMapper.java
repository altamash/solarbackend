package com.solar.api.tenant.mapper.ca;

import com.solar.api.tenant.model.ca.CaSoftCreditCheck;

public class CaSoftCreditCheckMapper {

    public static CaSoftCreditCheck toCaSoftCreditCheck(CaSoftCreditCheckDTO caSoftCreditCheckDTO){
        return CaSoftCreditCheck.builder()
                .id(caSoftCreditCheckDTO.getId())
                .source(caSoftCreditCheckDTO.getSource())
                .entity(caSoftCreditCheckDTO.getCustomerNo())
                .sequenceNo(caSoftCreditCheckDTO.getSequenceNo())
                .checkedBy(caSoftCreditCheckDTO.getCheckedBy())
                .creditStatus(caSoftCreditCheckDTO.getCreditStatus())
                .isChecked(caSoftCreditCheckDTO.getIsCheckedLater())
                .referenceNo(caSoftCreditCheckDTO.getReferenceNo())
                .expiryDate(caSoftCreditCheckDTO.getExpiryDate())
                .dateTime(caSoftCreditCheckDTO.getDateTime()).build();
    }

    public static CaSoftCreditCheck toCaSoftCreditCheckV2(CaSoftCreditCheckDTO caSoftCreditCheckDTO){
        return CaSoftCreditCheck.builder()
                .id(caSoftCreditCheckDTO.getId())
                .source(caSoftCreditCheckDTO.getSource())
                .entity(caSoftCreditCheckDTO.getCustomerNo())
                .sequenceNo(caSoftCreditCheckDTO.getSequenceNo())
                .checkedBy(caSoftCreditCheckDTO.getCheckedBy())
                .creditStatus(caSoftCreditCheckDTO.getCreditStatus())
                .isChecked(caSoftCreditCheckDTO.getIsCheckedLater())
                .referenceNo(caSoftCreditCheckDTO.getReferenceNo())
                .expiryDate(caSoftCreditCheckDTO.getExpiryDate())
                .dateTime(caSoftCreditCheckDTO.getDateTime()).build();
    }


    public static CaSoftCreditCheckDTO toCaSoftCreditCheck(CaSoftCreditCheck caSoftCreditCheck){
        return CaSoftCreditCheckDTO.builder()
                .id(caSoftCreditCheck.getId())
                .source(caSoftCreditCheck.getSource())
                .sequenceNo(caSoftCreditCheck.getSequenceNo())
                .checkedBy(caSoftCreditCheck.getCheckedBy())
                .creditStatus(caSoftCreditCheck.getCreditStatus())
                .isCheckedLater(caSoftCreditCheck.getIsChecked())
                .referenceNo(caSoftCreditCheck.getReferenceNo())
                .expiryDate(caSoftCreditCheck.getExpiryDate())
                .dateTime(caSoftCreditCheck.getDateTime()).build();
    }
}
