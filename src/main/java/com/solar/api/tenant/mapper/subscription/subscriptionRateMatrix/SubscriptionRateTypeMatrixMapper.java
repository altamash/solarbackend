package com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix;

import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionRateTypeMatrixMapper {

    // SubscriptionRateMatrixHead /////////////////////////////////////////////////
    public static SubscriptionRateMatrixHead toSubscriptionRateMatrixHead(SubscriptionRateMatrixHeadDTO subscriptionRateMatrixHeadDTO) {
        return SubscriptionRateMatrixHead.builder()
                .id(subscriptionRateMatrixHeadDTO.getId())
                .subscriptionCode(subscriptionRateMatrixHeadDTO.getSubscriptionCode())
                .active(subscriptionRateMatrixHeadDTO.getActive())
                .subscriptionTemplate(subscriptionRateMatrixHeadDTO.getSubscriptionTemplate())
                .subscriptionRateMatrixDetails(toSubscriptionRateMatrixDetails(subscriptionRateMatrixHeadDTO.getSubscriptionRateMatrixDetails()))
                .build();
    }

    public static SubscriptionRateMatrixHeadDTO toSubscriptionRateMatrixHeadDTO(SubscriptionRateMatrixHead subscriptionRateMatrixHead) {
        if (subscriptionRateMatrixHead == null) {
            return null;
        }
        return SubscriptionRateMatrixHeadDTO.builder()
                .id(subscriptionRateMatrixHead.getId())
                .subscriptionCode(subscriptionRateMatrixHead.getSubscriptionCode())
                .active(subscriptionRateMatrixHead.getActive())
                .subscriptionTemplate(subscriptionRateMatrixHead.getSubscriptionTemplate())
                .subscriptionRateMatrixDetails(toSubscriptionRateMatrixDetailDTOs(subscriptionRateMatrixHead.getSubscriptionRateMatrixDetails()))
                .build();
    }

    public static SubscriptionRateMatrixHeadDTO toSubscriptionRateMatrixHeadRawDTO(SubscriptionRateMatrixHead subscriptionRateMatrixHead) {
        if (subscriptionRateMatrixHead == null) {
            return null;
        }
        return SubscriptionRateMatrixHeadDTO.builder()
                .id(subscriptionRateMatrixHead.getId())
                .subscriptionCode(subscriptionRateMatrixHead.getSubscriptionCode())
                .active(subscriptionRateMatrixHead.getActive())
                .subscriptionTemplate(subscriptionRateMatrixHead.getSubscriptionTemplate())
                .build();
    }

    public static SubscriptionRateMatrixHead toUpdatedSubscriptionRateMatrix(SubscriptionRateMatrixHead subscriptionRateMatrixHead, SubscriptionRateMatrixHead subscriptionRateMatrixHeadUpdate) {
        subscriptionRateMatrixHead.setSubscriptionCode(subscriptionRateMatrixHeadUpdate.getSubscriptionCode() == null ? subscriptionRateMatrixHead.getSubscriptionCode() : subscriptionRateMatrixHeadUpdate.getSubscriptionCode());
        subscriptionRateMatrixHead.setActive(subscriptionRateMatrixHeadUpdate.getActive() == null ?
                subscriptionRateMatrixHead.getActive() : subscriptionRateMatrixHeadUpdate.getActive());
        subscriptionRateMatrixHead.setSubscriptionTemplate(subscriptionRateMatrixHeadUpdate.getSubscriptionTemplate() == null ? subscriptionRateMatrixHead.getSubscriptionTemplate() : subscriptionRateMatrixHeadUpdate.getSubscriptionTemplate());
        subscriptionRateMatrixHead.setSubscriptionRateMatrixDetails(subscriptionRateMatrixHeadUpdate.getSubscriptionRateMatrixDetails() == null ? subscriptionRateMatrixHead.getSubscriptionRateMatrixDetails() : subscriptionRateMatrixHeadUpdate.getSubscriptionRateMatrixDetails());
        return subscriptionRateMatrixHead;
    }

    public static List<SubscriptionRateMatrixHead> toSubscriptionRateMatrixHeads(List<SubscriptionRateMatrixHeadDTO> subscriptionRateMatrixHeadDTOS) {
        return subscriptionRateMatrixHeadDTOS.stream().map(rm -> toSubscriptionRateMatrixHead(rm)).collect(Collectors.toList());
    }

    public static List<SubscriptionRateMatrixHeadDTO> toSubscriptionRateMatrixHeadDTOs(List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads) {
        return subscriptionRateMatrixHeads.stream().map(rm -> toSubscriptionRateMatrixHeadDTO(rm)).collect(Collectors.toList());
    }

    // SubscriptionRateMatrixDetail ////////////////////////////////////////////////////////////
    public static SubscriptionRateMatrixDetail toSubscriptionRateMatrix(SubscriptionRateMatrixDetailDTO subscriptionRateMatrixDetailDTO) {
        if (subscriptionRateMatrixDetailDTO == null) {
            return null;
        }
        return SubscriptionRateMatrixDetail.builder()
                .id(subscriptionRateMatrixDetailDTO.getId())
                .subscriptionRateMatrixId(subscriptionRateMatrixDetailDTO.getSubscriptionRateMatrixId())
                .subscriptionCode(subscriptionRateMatrixDetailDTO.getSubscriptionCode())
                .rateCode(subscriptionRateMatrixDetailDTO.getRateCode())
                .defaultValue(subscriptionRateMatrixDetailDTO.getDefaultValue())
                .mandatory(subscriptionRateMatrixDetailDTO.getMandatory())
                .level(subscriptionRateMatrixDetailDTO.getLevel())
                .sequenceNumber(subscriptionRateMatrixDetailDTO.getSequenceNumber())
                .maintainBillHistory(subscriptionRateMatrixDetailDTO.getMaintainBillHistory())
                .flags(subscriptionRateMatrixDetailDTO.getFlags())
                .allowOthersToEdit(subscriptionRateMatrixDetailDTO.getAllowOthersToEdit())
                .varyByCustomer(subscriptionRateMatrixDetailDTO.getVaryByCustomer())
                .systemUsed(subscriptionRateMatrixDetailDTO.getSystemUsed())
                .build();
    }


    public static SubscriptionRateMatrixDetailDTO toSubscriptionRateMatrixDTO(SubscriptionRateMatrixDetail subscriptionRateMatrixDetail) {
        if (subscriptionRateMatrixDetail == null) {
            return null;
        }
        return SubscriptionRateMatrixDetailDTO.builder()
                .id(subscriptionRateMatrixDetail.getId())
                .subscriptionRateMatrixId(subscriptionRateMatrixDetail.getSubscriptionRateMatrixId())
                .subscriptionCode(subscriptionRateMatrixDetail.getSubscriptionCode())
                .rateCode(subscriptionRateMatrixDetail.getRateCode())
                .defaultValue(subscriptionRateMatrixDetail.getDefaultValue())
                .mandatory(subscriptionRateMatrixDetail.getMandatory())
                .level(subscriptionRateMatrixDetail.getLevel())
                .sequenceNumber(subscriptionRateMatrixDetail.getSequenceNumber())
                .maintainBillHistory(subscriptionRateMatrixDetail.getMaintainBillHistory())
                .flags(subscriptionRateMatrixDetail.getFlags())
                .allowOthersToEdit(subscriptionRateMatrixDetail.getAllowOthersToEdit())
                .measureDefinition(subscriptionRateMatrixDetail.getMeasureDefinition())
                .varyByCustomer(subscriptionRateMatrixDetail.getVaryByCustomer())
                .systemUsed(subscriptionRateMatrixDetail.getSystemUsed())
                .createdAt(subscriptionRateMatrixDetail.getCreatedAt())
                .updatedAt(subscriptionRateMatrixDetail.getUpdatedAt())
                .build();
    }

    public static SubscriptionRateMatrixDetail toUpdatedSubscriptionRateMatrix(SubscriptionRateMatrixDetail subscriptionRateMatrixDetail, SubscriptionRateMatrixDetail subscriptionRateMatrixDetailUpdate) {
        subscriptionRateMatrixDetail.setSubscriptionRateMatrixId(subscriptionRateMatrixDetailUpdate.getSubscriptionRateMatrixId() == null ?
                subscriptionRateMatrixDetail.getSubscriptionRateMatrixId() :
                subscriptionRateMatrixDetailUpdate.getSubscriptionRateMatrixId());
        subscriptionRateMatrixDetail.setSubscriptionCode(subscriptionRateMatrixDetailUpdate.getSubscriptionCode() == null ?
                subscriptionRateMatrixDetail.getSubscriptionCode() :
                subscriptionRateMatrixDetailUpdate.getSubscriptionCode());
        subscriptionRateMatrixDetail.setRateCode(subscriptionRateMatrixDetailUpdate.getRateCode() == null ?
                subscriptionRateMatrixDetail.getRateCode() : subscriptionRateMatrixDetailUpdate.getRateCode());
        subscriptionRateMatrixDetail.setDefaultValue(subscriptionRateMatrixDetailUpdate.getDefaultValue() == null ?
                subscriptionRateMatrixDetail.getDefaultValue() : subscriptionRateMatrixDetailUpdate.getDefaultValue());
        subscriptionRateMatrixDetail.setMandatory(subscriptionRateMatrixDetailUpdate.getMandatory() == null ?
                subscriptionRateMatrixDetail.getMandatory() : subscriptionRateMatrixDetailUpdate.getMandatory());
        subscriptionRateMatrixDetail.setLevel(subscriptionRateMatrixDetailUpdate.getLevel() == null ?
                subscriptionRateMatrixDetail.getLevel() : subscriptionRateMatrixDetailUpdate.getLevel());
        subscriptionRateMatrixDetail.setSequenceNumber(subscriptionRateMatrixDetailUpdate.getSequenceNumber() == null ? subscriptionRateMatrixDetail.getSequenceNumber() : subscriptionRateMatrixDetailUpdate.getSequenceNumber());
        subscriptionRateMatrixDetail.setMaintainBillHistory(subscriptionRateMatrixDetailUpdate.getMaintainBillHistory() == null ? subscriptionRateMatrixDetail.getMaintainBillHistory() : subscriptionRateMatrixDetailUpdate.getMaintainBillHistory());
        subscriptionRateMatrixDetail.setFlags(subscriptionRateMatrixDetailUpdate.getFlags() == null ?
                subscriptionRateMatrixDetail.getFlags() : subscriptionRateMatrixDetailUpdate.getFlags());
        subscriptionRateMatrixDetail.setAllowOthersToEdit(subscriptionRateMatrixDetailUpdate.getAllowOthersToEdit() == null ?
                subscriptionRateMatrixDetail.getAllowOthersToEdit() :
                subscriptionRateMatrixDetailUpdate.getAllowOthersToEdit());
        return subscriptionRateMatrixDetail;
    }

    public static List<SubscriptionRateMatrixDetail> toSubscriptionRateMatrixDetails(List<SubscriptionRateMatrixDetailDTO> subscriptionRateTypeMatrices) {
        if (subscriptionRateTypeMatrices == null) {
            return null;
        }
        return subscriptionRateTypeMatrices.stream().map(cr -> toSubscriptionRateMatrix(cr)).collect(Collectors.toList());
    }

    public static List<SubscriptionRateMatrixDetailDTO> toSubscriptionRateMatrixDetailDTOs(List<SubscriptionRateMatrixDetail> subscriptionRateTypeMatrices) {
        if (subscriptionRateTypeMatrices == null) {
            return null;
        }
        return subscriptionRateTypeMatrices.stream().map(cr -> toSubscriptionRateMatrixDTO(cr)).collect(Collectors.toList());
    }

    // SubscriptionRatesDerived //////////////////////////////////////////////
    public static SubscriptionRatesDerived toSubscriptionRatesDerived(SubscriptionRatesDerivedDTO subscriptionRatesDerivedDTO) {
        return SubscriptionRatesDerived.builder()
                .id(subscriptionRatesDerivedDTO.getId())
                .subscriptionRateMatrixId(subscriptionRatesDerivedDTO.getSubscriptionRateMatrixId())
                .subscriptionCode(subscriptionRatesDerivedDTO.getSubscriptionCode())
                .refType(subscriptionRatesDerivedDTO.getRefType())
                .calcGroup(subscriptionRatesDerivedDTO.getCalcGroup())
                .refCode(subscriptionRatesDerivedDTO.getRefCode())
                .value(subscriptionRatesDerivedDTO.getValue())
                .conditionExpr(subscriptionRatesDerivedDTO.getCondition())
                .notes(subscriptionRatesDerivedDTO.getNotes())
                .build();
    }

    public static SubscriptionRatesDerivedDTO toSubscriptionRatesDerivedDTO(SubscriptionRatesDerived subscriptionRatesDerived) {
        if (subscriptionRatesDerived == null) {
            return null;
        }
        return SubscriptionRatesDerivedDTO.builder()
                .id(subscriptionRatesDerived.getId())
                .subscriptionRateMatrixId(subscriptionRatesDerived.getSubscriptionRateMatrixId())
                .subscriptionCode(subscriptionRatesDerived.getSubscriptionCode())
                .refType(subscriptionRatesDerived.getRefType())
                .calcGroup(subscriptionRatesDerived.getCalcGroup())
                .refCode(subscriptionRatesDerived.getRefCode())
                .value(subscriptionRatesDerived.getValue())
                .condition(subscriptionRatesDerived.getConditionExpr())
                .notes(subscriptionRatesDerived.getNotes())
                .build();
    }

    public static SubscriptionRatesDerived toUpdatedSubscriptionRatesDerived(SubscriptionRatesDerived subscriptionRatesDerived, SubscriptionRatesDerived subscriptionRatesDerivedUpdate) {
        subscriptionRatesDerived.setSubscriptionRateMatrixId(subscriptionRatesDerivedUpdate.getSubscriptionRateMatrixId() == null ? subscriptionRatesDerived.getSubscriptionRateMatrixId() : subscriptionRatesDerivedUpdate.getSubscriptionRateMatrixId());
        subscriptionRatesDerived.setSubscriptionCode(subscriptionRatesDerivedUpdate.getSubscriptionCode() == null ?
                subscriptionRatesDerived.getSubscriptionCode() : subscriptionRatesDerivedUpdate.getSubscriptionCode());
        subscriptionRatesDerived.setRefType(subscriptionRatesDerivedUpdate.getRefType() == null ?
                subscriptionRatesDerived.getRefType() : subscriptionRatesDerivedUpdate.getRefType());
        subscriptionRatesDerived.setCalcGroup(subscriptionRatesDerivedUpdate.getCalcGroup() == null ?
                subscriptionRatesDerived.getCalcGroup() : subscriptionRatesDerivedUpdate.getCalcGroup());
        subscriptionRatesDerived.setRefCode(subscriptionRatesDerivedUpdate.getRefCode() == null ?
                subscriptionRatesDerived.getRefCode() : subscriptionRatesDerivedUpdate.getRefCode());
        subscriptionRatesDerived.setValue(subscriptionRatesDerivedUpdate.getValue() == null ?
                subscriptionRatesDerived.getValue() : subscriptionRatesDerivedUpdate.getValue());
        subscriptionRatesDerived.setConditionExpr(subscriptionRatesDerivedUpdate.getConditionExpr() == null ?
                subscriptionRatesDerived.getConditionExpr() : subscriptionRatesDerivedUpdate.getConditionExpr());
        subscriptionRatesDerived.setNotes(subscriptionRatesDerivedUpdate.getNotes() == null ?
                subscriptionRatesDerived.getNotes() : subscriptionRatesDerivedUpdate.getNotes());
        return subscriptionRatesDerived;
    }

    public static List<SubscriptionRatesDerived> toSubscriptionRatesDerived(List<SubscriptionRatesDerivedDTO> subscriptionRatesDerivedDTOs) {
        return subscriptionRatesDerivedDTOs.stream().map(cr -> toSubscriptionRatesDerived(cr)).collect(Collectors.toList());
    }

    public static List<SubscriptionRatesDerivedDTO> toSubscriptionRatesDerivedDTOs(List<SubscriptionRatesDerived> subscriptionRatesDerived) {
        return subscriptionRatesDerived.stream().map(cr -> toSubscriptionRatesDerivedDTO(cr)).collect(Collectors.toList());
    }
}
