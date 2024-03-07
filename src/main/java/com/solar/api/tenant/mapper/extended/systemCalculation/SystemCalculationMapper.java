package com.solar.api.tenant.mapper.extended.systemCalculation;

import com.solar.api.tenant.model.extended.SystemCalculation;

import java.util.List;
import java.util.stream.Collectors;

public class SystemCalculationMapper {

    public static SystemCalculation toSystemCalculation(SystemCalculationDTO systemCalculationDTO) {
        if (systemCalculationDTO == null) {
            return null;
        }
        return SystemCalculation.builder()
                .id(systemCalculationDTO.getId())
                .acctId(systemCalculationDTO.getAcctId())
                .refType(systemCalculationDTO.getRefType())
                .refId(systemCalculationDTO.getRefId())
                .siteId(systemCalculationDTO.getSiteId())
                .calcType(systemCalculationDTO.getCalcType())
                .calcValue(systemCalculationDTO.getCalcValue())
                .date(systemCalculationDTO.getDate())
                .ext1(systemCalculationDTO.getExt1())
                .ext2(systemCalculationDTO.getExt2())
                .ext3(systemCalculationDTO.getExt3())
                .build();
    }

    public static SystemCalculationDTO toSystemCalculationDTO(SystemCalculation systemCalculation) {
        if (systemCalculation == null) {
            return null;
        }
        return SystemCalculationDTO.builder()
                .id(systemCalculation.getId())
                .acctId(systemCalculation.getAcctId())
                .refType(systemCalculation.getRefType())
                .refId(systemCalculation.getRefId())
                .siteId(systemCalculation.getSiteId())
                .calcType(systemCalculation.getCalcType())
                .calcValue(systemCalculation.getCalcValue())
                .date(systemCalculation.getDate())
                .ext1(systemCalculation.getExt1())
                .ext2(systemCalculation.getExt2())
                .ext3(systemCalculation.getExt3())
                .build();
    }

    public static SystemCalculation toUpdatedSystemCalculation(SystemCalculation systemCalculation,
                                                               SystemCalculation systemCalculationUpdate) {
        systemCalculation.setAcctId(systemCalculation.getAcctId() == null ? systemCalculation.getAcctId() :
                systemCalculationUpdate.getAcctId());
        systemCalculation.setRefType(systemCalculation.getRefType() == null ? systemCalculation.getRefType() :
                systemCalculationUpdate.getRefType());
        systemCalculation.setRefId(systemCalculation.getRefId() == null ? systemCalculation.getRefId() :
                systemCalculationUpdate.getRefId());
        systemCalculation.setSiteId(systemCalculation.getSiteId() == null ? systemCalculation.getSiteId() :
                systemCalculationUpdate.getSiteId());
        systemCalculation.setCalcType(systemCalculation.getCalcType() == null ? systemCalculation.getCalcType() :
                systemCalculationUpdate.getCalcType());
        systemCalculation.setCalcValue(systemCalculation.getCalcValue() == null ? systemCalculation.getCalcValue() :
                systemCalculationUpdate.getCalcValue());
        systemCalculation.setDate(systemCalculation.getDate() == null ? systemCalculation.getDate() :
                systemCalculationUpdate.getDate());
        systemCalculation.setExt1(systemCalculation.getExt1() == null ? systemCalculation.getExt1() :
                systemCalculationUpdate.getExt1());
        systemCalculation.setExt2(systemCalculation.getExt2() == null ? systemCalculation.getExt2() :
                systemCalculationUpdate.getExt2());
        systemCalculation.setExt3(systemCalculation.getExt3() == null ? systemCalculation.getExt3() :
                systemCalculationUpdate.getExt3());
        return systemCalculation;
    }

    public static List<SystemCalculation> toSystemCalculations(List<SystemCalculationDTO> systemCalculationDTOS) {
        return systemCalculationDTOS.stream().map(a -> toSystemCalculation(a)).collect(Collectors.toList());
    }

    public static List<SystemCalculationDTO> toSystemCalculationDTOs(List<SystemCalculation> systemCalculations) {
        return systemCalculations.stream().map(a -> toSystemCalculationDTO(a)).collect(Collectors.toList());
    }
}
