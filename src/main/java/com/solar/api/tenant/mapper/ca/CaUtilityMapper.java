package com.solar.api.tenant.mapper.ca;

import com.solar.api.saas.service.process.upload.mapper.CustomerUtility;
import com.solar.api.tenant.model.ca.CaUtility;

import java.util.List;
import java.util.stream.Collectors;

public class CaUtilityMapper {
    public static CaUtility toCaUtility(CaUtilityDTO caUtilityDTO){
        return CaUtility.builder()
                .id(caUtilityDTO.getId())
                .accountHolderName(caUtilityDTO.getAccountHolderName())
                .averageMonthlyBill(caUtilityDTO.getAverageMonthlyBill())
                .referenceId(caUtilityDTO.getReferenceId())
                .utilityProviderId(caUtilityDTO.getUtilityProviderId())
                .premise(caUtilityDTO.getPremise())
                .isChecked(caUtilityDTO.getIsChecked())
                .createdAt(caUtilityDTO.getCreatedAt())
                .isPrimary(caUtilityDTO.getIsPrimary())
                .utilityPortalUsername(caUtilityDTO.getUtilityPortalName())
                .passCode(caUtilityDTO.getPassCode())
                .portalAccessAllowed(caUtilityDTO.getPortalAccessAllowed())
                .build();
    }

    public static CaUtility customerUtilityToCaUtility(CustomerUtility customerUtility){
        return CaUtility.builder()
                .id(customerUtility.getUtilityId())
                .action(customerUtility.getAction())
                .entityId(customerUtility.getEntityId())
                .accountHolderName(customerUtility.getAccountHolderName())
                .averageMonthlyBill(customerUtility.getAverageMonthlyBill())
                .referenceId(customerUtility.getReferenceNum())
                .utilityProviderId(customerUtility.getUtilProviderId())
                .premise(customerUtility.getPremise())
                .isChecked(customerUtility.getIsChecked())
                .build();
    }

    public static CaUtilityDTO toCaUtilityDTO(CaUtility caUtility){
        if(caUtility == null){
            return null;
        }
        return CaUtilityDTO.builder()
                .id(caUtility.getId())
                .accountHolderName(caUtility.getAccountHolderName())
                .averageMonthlyBill(caUtility.getAverageMonthlyBill())
                //.entityId(caUtility.getEntity().getId()
                .referenceId(caUtility.getReferenceId())
                .utilityProviderId(caUtility.getUtilityProviderId())
                .isChecked(caUtility.getIsChecked())
                .premise(caUtility.getPremise())
                .createdAt(caUtility.getCreatedAt())
                .utilityPortalName(caUtility.getUtilityPortalUsername())
                .passCode(caUtility.getPassCode())
                .isPrimary(caUtility.getIsPrimary() != null ? caUtility.getIsPrimary() : false)
                .portalAccessAllowed(caUtility.getPortalAccessAllowed())
                .build();

    }

    public static CaUtility toUpdateCaUtility(CaUtility caUtilityData,CaUtility caUtility){
        caUtility.setEntity(caUtility.getEntity()!=null?caUtility.getEntity():caUtilityData.getEntity());
        caUtility.setUtilityProviderId(caUtility.getUtilityProviderId()!=null?caUtility.getUtilityProviderId():caUtilityData.getUtilityProviderId());
        caUtility.setCreatedAt(caUtility.getCreatedAt()!=null?caUtility.getCreatedAt():caUtilityData.getCreatedAt());
        caUtility.setPremise(caUtility.getPremise()!=null?caUtility.getPremise():caUtilityData.getPremise());
        caUtility.setAccountHolderName(caUtility.getAccountHolderName()!=null?caUtility.getAccountHolderName():caUtilityData.getAccountHolderName());
        caUtility.setAverageMonthlyBill(caUtility.getAverageMonthlyBill()!=null?caUtility.getAverageMonthlyBill():caUtilityData.getAverageMonthlyBill());
        return caUtility;

    }

    public static List<CaUtility> toCaUtilitys(List<CustomerUtility> customerUtilities) {
        return customerUtilities.stream().map(CaUtilityMapper::customerUtilityToCaUtility).collect(Collectors.toList());
    }

    public static List<CaUtility> toCaUtilities(List<CaUtilityDTO> caUtilityDTOS) {
        return caUtilityDTOS.stream().map(bh -> toCaUtility(bh)).collect(Collectors.toList());
    }

    public static List<CaUtilityDTO> toCaUtilityDTOs(List<CaUtility> caUtilitys) {
        return caUtilitys.stream().map(
                bh -> toCaUtilityDTO(bh)).collect(Collectors.toList());
    }

}
