package com.solar.api.tenant.mapper.extended.partner;

import com.solar.api.tenant.model.extended.partner.PartnerDetail;
import com.solar.api.tenant.model.extended.partner.PartnerHead;

import java.util.List;
import java.util.stream.Collectors;

public class PartnerMapper {

    // PartnerHead ////////////////////////////////////////////////
    public static PartnerHead toPartnerHead(PartnerHeadDTO partnerHeadDTO) {
        if (partnerHeadDTO == null) {
            return null;
        }
        return PartnerHead.builder()
                .id(partnerHeadDTO.getPartnerId())
                .registerId(partnerHeadDTO.getRegisterId())
                .description(partnerHeadDTO.getDescription())
                .refName(partnerHeadDTO.getRefName())
                .recordLevelInd(partnerHeadDTO.getRecordLevelInd())
                .registrationDate(partnerHeadDTO.getRegistrationDate())
                .status(partnerHeadDTO.getStatus())
                .type(partnerHeadDTO.getType())
                .startDate(partnerHeadDTO.getStartDate())
                .endDate(partnerHeadDTO.getEndDate())
                .engagement(partnerHeadDTO.getEngagement())
                .partnerDetails(partnerHeadDTO.getPartnerDetails() != null ?
                        toPartnerDetails(partnerHeadDTO.getPartnerDetails()) : null)
                .build();
    }

    public static PartnerHeadDTO toPartnerHeadDTO(PartnerHead partnerHead) {
        if (partnerHead == null) {
            return null;
        }
        return PartnerHeadDTO.builder()
                .partnerId(partnerHead.getId())
                .registerId(partnerHead.getRegisterId())
                .refName(partnerHead.getRefName())
                .recordLevelInd(partnerHead.getRecordLevelInd())
                .description(partnerHead.getDescription())
                .registrationDate(partnerHead.getRegistrationDate())
                .status(partnerHead.getStatus())
                .createdAt(partnerHead.getCreatedAt())
                .updatedAt(partnerHead.getUpdatedAt())
                .type(partnerHead.getType())
                .startDate(partnerHead.getStartDate())
                .endDate(partnerHead.getEndDate())
                .engagement(partnerHead.getEngagement())
                .partnerDetails(partnerHead.getPartnerDetails() != null ?
                        toPartnerDetailDTOs(partnerHead.getPartnerDetails()) : null)
                .build();
    }

    public static PartnerHead toUpdatedPartnerHead(PartnerHead partnerHead, PartnerHead partnerHeadUpdate) {
        partnerHead.setRegisterId(partnerHeadUpdate.getRegisterId() == null ? partnerHead.getRegisterId() :
                partnerHeadUpdate.getRegisterId());
        partnerHead.setRefName(partnerHeadUpdate.getRefName() == null ? partnerHead.getRefName() :
                partnerHeadUpdate.getRefName());
        partnerHead.setRecordLevelInd(partnerHeadUpdate.getRecordLevelInd() == null ? partnerHead.getRecordLevelInd() :
                partnerHeadUpdate.getRecordLevelInd());
        partnerHead.setDescription(partnerHeadUpdate.getDescription() == null ? partnerHead.getDescription() :
                partnerHeadUpdate.getDescription());
        partnerHead.setRegistrationDate(partnerHeadUpdate.getRegistrationDate() == null ?
                partnerHead.getRegistrationDate() : partnerHeadUpdate.getRegistrationDate());
        partnerHead.setStatus(partnerHeadUpdate.getStatus() == null ? partnerHead.getStatus() :
                partnerHeadUpdate.getStatus());
        partnerHead.setType(partnerHeadUpdate.getType() == null ? partnerHead.getType() :
                partnerHeadUpdate.getType());
        partnerHead.setStartDate(partnerHeadUpdate.getStartDate() == null ? partnerHead.getStartDate() :
                partnerHeadUpdate.getStartDate());
        partnerHead.setEndDate(partnerHeadUpdate.getEndDate() == null ? partnerHead.getEndDate() :
                partnerHeadUpdate.getEndDate());
        return partnerHead;
    }

    public static List<PartnerHead> toPartnerHeads(List<PartnerHeadDTO> partnerHeadDTOS) {
        return partnerHeadDTOS.stream().map(p -> toPartnerHead(p)).collect(Collectors.toList());
    }

    public static List<PartnerHeadDTO> toPartnerHeadDTOs(List<PartnerHead> partnerHeads) {
        return partnerHeads.stream().map(p -> toPartnerHeadDTO(p)).collect(Collectors.toList());
    }

    // PartnerDetail ////////////////////////////////////////////////
    public static PartnerDetail toPartnerDetail(PartnerDetailDTO partnerDetailDTO) {
        if (partnerDetailDTO == null) {
            return null;
        }
        return PartnerDetail.builder()
                .id(partnerDetailDTO.getId())
                .partnerHeadId(partnerDetailDTO.getPartnerId())
                .measureCodeId(partnerDetailDTO.getMeasureCodeId())
                .measure(partnerDetailDTO.getMeasure())
                .measureDefinitionTenant(partnerDetailDTO.getMeasureDefinition())
                .value(partnerDetailDTO.getValue())
                .build();
    }

    public static PartnerDetailDTO toPartnerDetailDTO(PartnerDetail partnerDetail) {
        if (partnerDetail == null) {
            return null;
        }
        return PartnerDetailDTO.builder()
                .id(partnerDetail.getId())
                .partnerId(partnerDetail.getPartnerHeadId())
                .measureCodeId(partnerDetail.getMeasureCodeId())
                .measure(partnerDetail.getMeasure())
                .measureDefinition(partnerDetail.getMeasureDefinitionTenant())
                .value(partnerDetail.getValue())
                .createdAt(partnerDetail.getCreatedAt())
                .updatedAt(partnerDetail.getUpdatedAt())
                .build();
    }

    public static PartnerDetail toUpdatedPartnerDetail(PartnerDetail partnerDetail, PartnerDetail partnerDetailUpdate) {
        partnerDetail.setPartnerHeadId(partnerDetailUpdate.getPartnerHeadId() == null ?
                partnerDetail.getPartnerHeadId() : partnerDetailUpdate.getPartnerHeadId());
        //partnerDetail.setMeasure(partnerDetailUpdate.getMeasure() == null ? partnerDetail.getMeasure() :
        // partnerDetailUpdate.getMeasure());
        partnerDetail.setMeasureCodeId(partnerDetailUpdate.getMeasureCodeId() == null ?
                partnerDetail.getMeasureCodeId() : partnerDetailUpdate.getMeasureCodeId());
        partnerDetail.setMeasure(partnerDetailUpdate.getMeasure() == null ?
                partnerDetail.getMeasure() : partnerDetailUpdate.getMeasure());
        partnerDetail.setValue(partnerDetailUpdate.getValue() == null ? partnerDetail.getValue() :
                partnerDetailUpdate.getValue());
        partnerDetail.setMeasureDefinitionTenant(partnerDetailUpdate.getMeasureDefinitionTenant() == null ?
                partnerDetail.getMeasureDefinitionTenant() : partnerDetailUpdate.getMeasureDefinitionTenant());
        return partnerDetail;
    }

    public static List<PartnerDetail> toPartnerDetails(List<PartnerDetailDTO> partnerDetailDTOS) {
        return partnerDetailDTOS.stream().map(p -> toPartnerDetail(p)).collect(Collectors.toList());
    }

    public static List<PartnerDetailDTO> toPartnerDetailDTOs(List<PartnerDetail> partnerDetails) {
        return partnerDetails.stream().map(p -> toPartnerDetailDTO(p)).collect(Collectors.toList());
    }
}
