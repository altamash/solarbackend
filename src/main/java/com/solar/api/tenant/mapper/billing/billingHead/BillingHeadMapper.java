package com.solar.api.tenant.mapper.billing.billingHead;

import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;

import java.text.DateFormatSymbols;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BillingHeadMapper {

    public static BillingHead toBillingHead(BillingHeadDTO billingHeadDTO) {

        return BillingHead.builder()
                .id(billingHeadDTO.getId())
                .invoiceId(billingHeadDTO.getInvoiceId())
                .userAccountId(billingHeadDTO.getUserAccountId())
                .subscriptionId(billingHeadDTO.getSubscriptionId())
                .custProdId(billingHeadDTO.getCustProdId())
                .billType(billingHeadDTO.getBillType())
                .amount(billingHeadDTO.getAmount())
                .generatedOn(billingHeadDTO.getGeneratedOn())
                .billingMonthYear(billingHeadDTO.getBillingMonthYear())
                .billStatus(billingHeadDTO.getBillStatus())
                .invoiceDate(billingHeadDTO.getInvoiceDate())
                .dueDate(billingHeadDTO.getDueDate())
                .defermentDate(billingHeadDTO.getDefermentDate())
                .billSkip(billingHeadDTO.getBillSkip())
                .build();
    }

    public static BillingHeadDTO toBillingHeadDTO(BillingHead billingHead, boolean formatDate,
                                                  String invoiceUrlBaseUrl) {

        if (billingHead == null) {
            return null;
        }
        return BillingHeadDTO.builder()
                .id(billingHead.getId())
                .invoiceId(billingHead.getInvoice() != null ? billingHead.getInvoice().getId() : null)
                .invoiceUrl(billingHead.getInvoice() != null && billingHead.getInvoice().getInvoiceUrl() != null ?
                        invoiceUrlBaseUrl + billingHead.getInvoice().getInvoiceUrl() : null)
                .userAccountId(billingHead.getUserAccountId())
                .subscriptionId(billingHead.getSubscriptionId())
                .custProdId(billingHead.getCustProdId())
                .billType(billingHead.getBillType())
                .amount(billingHead.getAmount())
                .generatedOn(billingHead.getGeneratedOn())
                .billingMonthYear(formatDate ?
                        new DateFormatSymbols().getShortMonths()[Integer.parseInt(billingHead.getBillingMonthYear().split("-")[0]) - 1] +
                                " " + billingHead.getBillingMonthYear().split("-")[1] :
                        billingHead.getBillingMonthYear())
                .billStatus(billingHead.getBillStatus())
                .invoiceDate(billingHead.getInvoiceDate())
                .dueDate(billingHead.getDueDate())
                .defermentDate(billingHead.getDefermentDate())
                .billingDetailIds(billingHead.getBillingDetails() != null ?
                        billingHead.getBillingDetails().stream().map(bd -> bd.getId()).collect(Collectors.toList()) :
                        null)
                .billSkip(billingHead.getBillSkip())
                .build();
    }

    public static BillingHead toUpdatedBillingHead(BillingHead billingHead, BillingHead billingHeadUpdate) {
        billingHead.setInvoice(billingHeadUpdate.getInvoice() == null ? billingHead.getInvoice() :
                billingHeadUpdate.getInvoice());
        billingHead.setAmount(billingHeadUpdate.getAmount() == null ? billingHead.getAmount() :
                billingHeadUpdate.getAmount());
        billingHead.setBillType(billingHeadUpdate.getBillType() == null ? billingHead.getBillType() :
                billingHeadUpdate.getBillType());
        billingHead.setBillStatus(billingHeadUpdate.getBillStatus() == null ? billingHead.getBillStatus() :
                billingHeadUpdate.getBillStatus());
        billingHead.setInvoiceDate(billingHeadUpdate.getInvoiceDate() == null ? billingHead.getInvoiceDate() :
                billingHeadUpdate.getInvoiceDate());
        billingHead.setDueDate(billingHeadUpdate.getDueDate() == null ? billingHead.getDueDate() :
                billingHeadUpdate.getDueDate());
        billingHead.setDefermentDate(billingHeadUpdate.getDefermentDate() == null ? billingHead.getDefermentDate() :
                billingHeadUpdate.getDefermentDate());
        billingHead.setBillingMonthYear(billingHeadUpdate.getBillingMonthYear() == null ?
                billingHead.getBillingMonthYear() : billingHeadUpdate.getBillingMonthYear());
        billingHead.setBillSkip(billingHeadUpdate.getBillSkip() == null ?
                billingHead.getBillSkip() : billingHeadUpdate.getBillSkip());
        return billingHead;
    }

    public static List<BillingHead> toBillingHeads(List<BillingHeadDTO> billingHeadDTOS) {
        return billingHeadDTOS.stream().map(bh -> toBillingHead(bh)).collect(Collectors.toList());
    }

    public static List<BillingHeadDTO> toBillingHeadDTOs(List<BillingHead> billingHeads, boolean formatDate,
                                                         String invoiceUrlBaseUrl) {
        List<BillingHeadDTO> billingHeadDTOs = billingHeads.stream().map(bh -> toBillingHeadDTO(bh, formatDate,
                invoiceUrlBaseUrl)).collect(Collectors.toList());

        //marking bill status to invalid if status is GENERATED or
        boolean[] invalidFlagFound = {Boolean.FALSE};

        int generatedIndex = IntStream.range(0, billingHeadDTOs.size())
                .filter(i -> billingHeadDTOs.get(i).getBillStatus().equals(EBillStatus.GENERATED.getStatus())
                        || billingHeadDTOs.get(i).getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()))
                .findFirst()
                .orElse(-1);

        int invoicedIndex = IntStream.range(0, billingHeadDTOs.size())
                .filter(i -> billingHeadDTOs.get(i).getBillStatus().equals(EBillStatus.INVOICED.getStatus())
                        || billingHeadDTOs.get(i).getBillStatus().equals(EBillStatus.PAID.getStatus()))
                .findFirst()
                .orElse(-1);

        if (invoicedIndex > generatedIndex && generatedIndex != -1) {
            billingHeadDTOs.stream().filter(bh -> (bh.getBillStatus().equals(EBillStatus.GENERATED.getStatus()) ||
                    bh.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus())
            )).forEach(value -> {
                if (!invalidFlagFound[0]) {
                    value.setInvalidationAllowed(Boolean.TRUE);
                    invalidFlagFound[0] = Boolean.TRUE;
                }
            });

        } else if (invoicedIndex == -1) {
            billingHeadDTOs.stream().filter(bh -> (bh.getBillStatus().equals(EBillStatus.GENERATED.getStatus()) ||
                    bh.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus())
            )).forEach(value -> {
                if (!invalidFlagFound[0]) {
                    value.setInvalidationAllowed(Boolean.TRUE);
                    invalidFlagFound[0] = Boolean.TRUE;
                }
            });
        }
        return billingHeadDTOs;
    }




}
