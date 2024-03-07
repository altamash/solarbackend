package com.solar.api.tenant.mapper.billing.billingHead;

import com.solar.api.tenant.model.billing.billingHead.BillSaving;

import java.util.List;
import java.util.stream.Collectors;

public class BillSavingMapper {

    public static BillSaving toBillSaving(BillSavingDTO billSavingDTO) {

        return BillSaving.builder()
                .id(billSavingDTO.getId())
                .billId(billSavingDTO.getBillId())
                .savingCode(billSavingDTO.getSavingCode())
                .value(billSavingDTO.getValue())
                .date(billSavingDTO.getDate())
                .build();
    }

    public static BillSavingDTO toBillSavingDTO(BillSaving billSaving) {

        if (billSaving == null) {
            return null;
        }
        return BillSavingDTO.builder()
                .id(billSaving.getId())
                .billId(billSaving.getBillId())
                .savingCode(billSaving.getSavingCode())
                .value(billSaving.getValue())
                .date(billSaving.getDate())
                .build();
    }

    public static BillSaving toUpdatedBillSaving(BillSaving billSaving, BillSaving billSavingUpdate) {
        billSaving.setSavingCode(billSavingUpdate.getSavingCode() == null ? billSaving.getSavingCode() :
                billSavingUpdate.getSavingCode());
        billSaving.setValue(billSavingUpdate.getValue() == null ? billSaving.getValue() : billSavingUpdate.getValue());
        billSaving.setDate(billSavingUpdate.getDate() == null ? billSaving.getDate() : billSavingUpdate.getDate());
        return billSaving;
    }

    public static List<BillSaving> toBillSaving(List<BillSavingDTO> billSavingDTOS) {
        return billSavingDTOS.stream().map(bs -> toBillSaving(bs)).collect(Collectors.toList());
    }

    public static List<BillSavingDTO> toBillSavingDTOs(List<BillSaving> billSavings) {
        return billSavings.stream().map(bs -> toBillSavingDTO(bs)).collect(Collectors.toList());
    }
}
