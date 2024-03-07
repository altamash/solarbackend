package com.solar.api.tenant.mapper.extended.resources;

import com.solar.api.tenant.model.extended.resources.HRDetail;
import com.solar.api.tenant.model.extended.resources.HRHead;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HRMapper {

    public static HRHead toHRHead(HRHeadDTO hrHeadDTO) {
        if (hrHeadDTO == null) {
            return null;
        }
        return HRHead.builder()
                .id(hrHeadDTO.getId())
                .name(hrHeadDTO.getName())
                .title(hrHeadDTO.getTitle())
                .firstName(hrHeadDTO.getFirstName())
                .middleName(hrHeadDTO.getMiddleName()!=null ? hrHeadDTO.getMiddleName() : "")
                .lastName(hrHeadDTO.getLastName())
                .registerId(hrHeadDTO.getRegisterId())
                .contractId(hrHeadDTO.getContractId())
                .reportsTo(hrHeadDTO.getReportsTo())
                .employmentType(hrHeadDTO.getEmploymentType())
                .referenceSource(hrHeadDTO.getReferenceSource())
                .externalReferenceId(hrHeadDTO.getExternalReferenceId())
                .dateEntered(hrHeadDTO.getDateEntered())
                .empStatus(hrHeadDTO.getEmpStatus())
                .loginUser(hrHeadDTO.getLoginUser())
                .enteredBy(hrHeadDTO.getEnteredBy())
                .designation(hrHeadDTO.getDesignation())
                .type(hrHeadDTO.getType())
                .startDate(hrHeadDTO.getStartDate())
                .endDate(hrHeadDTO.getEndDate())
                .photoId(hrHeadDTO.getPhotoId())
                .photoIdType(hrHeadDTO.getPhotoIdType())
                .externalReferenceId(hrHeadDTO.getExternalReferenceId())
                .encodedId(hrHeadDTO.getEncodedId())
                .hrDetails(hrHeadDTO.getHrDetails() != null ? toHRDetails(hrHeadDTO.getHrDetails()) :
                        Collections.emptyList())
                .build();
    }

    public static HRHeadDTO toHRHeadDTO(HRHead hrHead) {
        if (hrHead == null) {
            return null;
        }
        return HRHeadDTO.builder()
                .id(hrHead.getId())
                .name(hrHead.getName())
                .title(hrHead.getTitle())
                .firstName(hrHead.getFirstName())
                .middleName(hrHead.getMiddleName()!=null ? hrHead.getMiddleName() : "")
                .lastName(hrHead.getLastName())
                .registerId(hrHead.getRegisterId())
                .contractId(hrHead.getContractId())
                .employmentType(hrHead.getEmploymentType())
                .referenceSource(hrHead.getReferenceSource())
                .externalReferenceId(hrHead.getExternalReferenceId())
                .reportsTo(hrHead.getReportsTo())
                .dateEntered(hrHead.getDateEntered())
                .empStatus(hrHead.getEmpStatus())
                .loginUser(hrHead.getLoginUser())
                .enteredBy(hrHead.getEnteredBy())
                .createdAt(hrHead.getCreatedAt())
                .updatedAt(hrHead.getUpdatedAt())
                .designation(hrHead.getDesignation())
                .type(hrHead.getType())
                .startDate(hrHead.getStartDate())
                .endDate(hrHead.getEndDate())
                .photoId(hrHead.getPhotoId())
                .photoIdType(hrHead.getPhotoIdType())
                .externalReferenceId(hrHead.getExternalReferenceId())
                .encodedId(hrHead.getEncodedId())
                .hrDetails(hrHead.getHrDetails() != null ? toHRDetailDTOs(hrHead.getHrDetails()) :
                        Collections.emptyList())
                .build();
    }

    public static HRHead toUpdatedHRHead(HRHead hrHead, HRHead hrHeadUpdate) {
        hrHead.setName(hrHeadUpdate.getName() == null ? hrHead.getName() : hrHeadUpdate.getName());
        hrHead.setFirstName(hrHeadUpdate.getFirstName() == null ? hrHead.getFirstName() : hrHeadUpdate.getFirstName());
        hrHead.setMiddleName(hrHeadUpdate.getMiddleName() == null ? hrHead.getMiddleName() : hrHeadUpdate.getMiddleName());
        hrHead.setEmploymentType(hrHeadUpdate.getEmploymentType() == null ? hrHead.getEmploymentType() : hrHeadUpdate.getEmploymentType());
        hrHead.setReferenceSource(hrHeadUpdate.getReferenceSource() == null ? hrHead.getReferenceSource() : hrHeadUpdate.getReferenceSource());
        hrHead.setExternalReferenceId(hrHeadUpdate.getExternalReferenceId() == null ? hrHead.getExternalReferenceId() : hrHeadUpdate.getExternalReferenceId());
        hrHead.setLastName(hrHeadUpdate.getLastName() == null ? hrHead.getLastName() : hrHeadUpdate.getLastName());
        hrHead.setRegisterId(hrHeadUpdate.getRegisterId() == null ? hrHead.getRegisterId() :
                hrHeadUpdate.getRegisterId());
        hrHead.setContractId(hrHeadUpdate.getContractId() == null ? hrHead.getContractId() :
                hrHeadUpdate.getContractId());
        hrHead.setReportsTo(hrHeadUpdate.getReportsTo() == null ? hrHead.getReportsTo() : hrHeadUpdate.getReportsTo());
        hrHead.setDateEntered(hrHeadUpdate.getDateEntered() == null ? hrHead.getDateEntered() :
                hrHeadUpdate.getDateEntered());
        hrHead.setEmpStatus(hrHeadUpdate.getEmpStatus() == null ? hrHead.getEmpStatus() : hrHeadUpdate.getEmpStatus());
        hrHead.setLoginUser(hrHeadUpdate.getLoginUser() == null ? hrHead.getLoginUser() : hrHeadUpdate.getLoginUser());
        hrHead.setEnteredBy(hrHeadUpdate.getEnteredBy() == null ? hrHead.getEnteredBy() : hrHeadUpdate.getEnteredBy());
        hrHead.setDesignation(hrHeadUpdate.getDesignation() == null ? hrHead.getDesignation() : hrHeadUpdate.getDesignation());
        hrHead.setType(hrHeadUpdate.getType() == null ? hrHead.getType() : hrHeadUpdate.getType());
        hrHead.setStartDate(hrHeadUpdate.getStartDate() == null ? hrHead.getStartDate() : hrHeadUpdate.getStartDate());
        hrHead.setEndDate(hrHeadUpdate.getEndDate() == null ? hrHead.getEndDate() : hrHeadUpdate.getEndDate());
        hrHead.setPhotoId(hrHeadUpdate.getPhotoId() == null ? hrHead.getPhotoId() : hrHeadUpdate.getPhotoId());
        hrHead.setPhotoIdType(hrHeadUpdate.getPhotoIdType() == null ? hrHead.getPhotoIdType() : hrHeadUpdate.getPhotoIdType());
        hrHead.setEncodedId(hrHeadUpdate.getEncodedId() == null ? hrHead.getEncodedId() : hrHeadUpdate.getEncodedId());
        //hrHead.setHrDetails(hrHeadUpdate.getHrDetails() == null ? hrHead.getHrDetails() : hrHeadUpdate.getHrDetails
        // ());
        return hrHead;
    }

    public static List<HRHead> toHRHeads(List<HRHeadDTO> hrHeadDTOS) {
        return hrHeadDTOS.stream().map(h -> toHRHead(h)).collect(Collectors.toList());
    }

    public static List<HRHeadDTO> toHRHeadDTOs(List<HRHead> hrHeads) {
        return hrHeads.stream().map(h -> toHRHeadDTO(h)).collect(Collectors.toList());
    }

    //TODO:head detail
    public static HRDetail toHRDetail(HRDetailDTO hrDetailDTO) {
        if (hrDetailDTO == null) {
            return null;
        }
        return HRDetail.builder()
                .id(hrDetailDTO.getId())
                //.hrHeadId(hrDetailDTO.getHrHeadId())
                .measureCodeId(hrDetailDTO.getMeasureCodeId())
                .measure(hrDetailDTO.getMeasure())
                .measureDefinitionTenant(hrDetailDTO.getMeasureDefinitionTenant())
                .value(hrDetailDTO.getValue())
                .filterByInd(hrDetailDTO.getFilterByInd())
                .lastUpdateOn(hrDetailDTO.getLastUpdateOn())
                .lastUpdateBy(hrDetailDTO.getLastUpdateBy())
                .validationRule(hrDetailDTO.getValidationRule())
                .validationParams(hrDetailDTO.getValidationParams())
                .createdAt(hrDetailDTO.getCreatedAt())
                .updatedAt(hrDetailDTO.getUpdatedAt())
                .build();
    }

    public static HRDetailDTO toHRDetailDTO(HRDetail hrDetail) {
        if (hrDetail == null) {
            return null;
        }
        return HRDetailDTO.builder()
                .id(hrDetail.getId())
                .hrHeadId(hrDetail.getHrHeadId())
                .value(hrDetail.getValue())
                .measureCodeId(hrDetail.getMeasureCodeId())
                .measure(hrDetail.getMeasure())
                .measureDefinitionTenant(hrDetail.getMeasureDefinitionTenant())
                .createdAt(hrDetail.getCreatedAt())
                .updatedAt(hrDetail.getUpdatedAt())
                .filterByInd(hrDetail.getFilterByInd())
                .lastUpdateOn(hrDetail.getLastUpdateOn())
                .lastUpdateBy(hrDetail.getLastUpdateBy())
                .validationRule(hrDetail.getValidationRule())
                .validationParams(hrDetail.getValidationParams())
                .createdAt(hrDetail.getCreatedAt())
                .updatedAt(hrDetail.getUpdatedAt())
                .build();
    }

    public static List<HRDetailDTO> toHRDetailDTOs(List<HRDetail> hrDetail) {
        return hrDetail.stream().map(r -> toHRDetailDTO(r)).collect(Collectors.toList());
    }

    public static List<HRDetail> toHRDetails(List<HRDetailDTO> hrDetailDTOS) {
        return hrDetailDTOS.stream().map(r -> toHRDetail(r)).collect(Collectors.toList());
    }

    public static HRDetail toUpdatedHRDetail(HRDetail hrDetail, HRDetail hrDetailUpdate) {
        hrDetail.setMeasureCodeId(hrDetailUpdate.getMeasureCodeId() == null ? hrDetail.getMeasureCodeId() :
                hrDetailUpdate.getMeasureCodeId());
        hrDetail.setMeasure(hrDetailUpdate.getMeasure() == null ? hrDetail.getMeasure() :
                hrDetailUpdate.getMeasure());
        hrDetail.setMeasureDefinitionTenant(hrDetailUpdate.getMeasureDefinitionTenant() == null ?
                hrDetail.getMeasureDefinitionTenant() : hrDetailUpdate.getMeasureDefinitionTenant());
        hrDetail.setValue(hrDetailUpdate.getValue() == null ? hrDetail.getValue() : hrDetailUpdate.getValue());
        hrDetail.setFilterByInd(hrDetailUpdate.getFilterByInd() == null ? hrDetail.getFilterByInd() :
                hrDetailUpdate.getFilterByInd());
        hrDetail.setLastUpdateOn(hrDetailUpdate.getLastUpdateOn() == null ? hrDetail.getLastUpdateOn() :
                hrDetailUpdate.getLastUpdateOn());
        hrDetail.setLastUpdateBy(hrDetailUpdate.getLastUpdateBy() == null ? hrDetail.getLastUpdateBy() :
                hrDetailUpdate.getLastUpdateBy());
        hrDetail.setValidationRule(hrDetailUpdate.getValidationRule() == null ? hrDetail.getValidationRule() :
                hrDetailUpdate.getValidationRule());
        hrDetail.setValidationParams(hrDetailUpdate.getValidationParams() == null ? hrDetail.getValidationParams() :
                hrDetailUpdate.getValidationParams());
        return hrDetail;
    }

}
