package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.model.extended.project.FinancialAccrual;

import java.util.List;
import java.util.stream.Collectors;

public class FinancialAccrualMapper {

    public static FinancialAccrual toFinancialAccrual(FinancialAccrualDTO financialAccrualDTO) {
        if (financialAccrualDTO == null) {
            return null;
        }
        return FinancialAccrual.builder()
                .id(financialAccrualDTO.getId())
                .category(financialAccrualDTO.getCategory())
                .refId(financialAccrualDTO.getRefId())
                .subRefId(financialAccrualDTO.getSubRefId())
                .accrualCategory(financialAccrualDTO.getAccrualCategory())
                .accrualCategoryId(financialAccrualDTO.getAccrualCategoryId())
                .type(financialAccrualDTO.getType())
                .accrualDatetime(financialAccrualDTO.getAccrualDatetime())
                .accrualAdjustment(financialAccrualDTO.getAccrualAdjustment())
                .accrualPeriod(financialAccrualDTO.getAccrualPeriod())
                .accruedAmount(financialAccrualDTO.getAccruedAmount())
                .rate(financialAccrualDTO.getRate())
                .postingDate(financialAccrualDTO.getPostingDate())
                .status(financialAccrualDTO.getStatus())
                .orgId(financialAccrualDTO.getOrgId())
                .build();
    }

    public static FinancialAccrualDTO toFinancialAccrualDTO(FinancialAccrual financialAccrual) {
        if (financialAccrual == null) {
            return null;
        }
        return FinancialAccrualDTO.builder()
                .id(financialAccrual.getId())
                .category(financialAccrual.getCategory())
                .refId(financialAccrual.getRefId())
                .subRefId(financialAccrual.getSubRefId())
                .accrualCategory(financialAccrual.getAccrualCategory())
                .accrualCategoryId(financialAccrual.getAccrualCategoryId())
                .type(financialAccrual.getType())
                .accrualDatetime(financialAccrual.getAccrualDatetime())
                .accrualAdjustment(financialAccrual.getAccrualAdjustment())
                .accrualPeriod(financialAccrual.getAccrualPeriod())
                .accruedAmount(financialAccrual.getAccruedAmount())
                .rate(financialAccrual.getRate())
                .postingDate(financialAccrual.getPostingDate())
                .status(financialAccrual.getStatus())
                .orgId(financialAccrual.getOrgId())
                .build();
    }

    public static FinancialAccrual toUpdatedFinancialAccrual(FinancialAccrual financialAccrual,FinancialAccrual financialAccrualUpdate) {
        financialAccrual.setCategory(financialAccrualUpdate.getCategory() == null ? financialAccrual.getAccrualCategory() : financialAccrualUpdate.getCategory());
        financialAccrual.setRefId(financialAccrualUpdate.getRefId() == null ? financialAccrual.getRefId() : financialAccrualUpdate.getRefId());
        financialAccrual.setSubRefId(financialAccrualUpdate.getSubRefId() == null ? financialAccrual.getSubRefId() : financialAccrualUpdate.getSubRefId());
        financialAccrual.setAccrualCategory(financialAccrualUpdate.getAccrualCategory() == null ? financialAccrual.getAccrualCategory() : financialAccrualUpdate.getAccrualCategory());
        financialAccrual.setAccrualCategoryId(financialAccrualUpdate.getAccrualCategoryId() == null ? financialAccrual.getAccrualCategoryId() : financialAccrualUpdate.getAccrualCategoryId());
        financialAccrual.setType(financialAccrualUpdate.getType() == null ? financialAccrual.getType() : financialAccrualUpdate.getType());
        financialAccrual.setAccrualDatetime(financialAccrualUpdate.getAccrualDatetime() == null ? financialAccrual.getAccrualDatetime() : financialAccrualUpdate.getAccrualDatetime());
        financialAccrual.setAccrualAdjustment(financialAccrualUpdate.getAccrualAdjustment() == null ? financialAccrual.getAccrualAdjustment() : financialAccrualUpdate.getAccrualAdjustment());
        financialAccrual.setAccrualPeriod(financialAccrualUpdate.getAccrualPeriod() == null ? financialAccrual.getAccrualPeriod() : financialAccrualUpdate.getAccrualPeriod());
//        financialAccrual.setAccruedAmount(financialAccrualUpdate.getAccruedAmount() == null ? financialAccrual.getAccruedAmount() : financialAccrualUpdate.getAccruedAmount());
        financialAccrual.setRate(financialAccrualUpdate.getRate() == null ? financialAccrual.getRate() : financialAccrualUpdate.getRate());
        financialAccrual.setPostingDate(financialAccrualUpdate.getPostingDate() == null ? financialAccrual.getPostingDate() : financialAccrualUpdate.getPostingDate());
        financialAccrual.setOrgId(financialAccrualUpdate.getOrgId() == null ? financialAccrual.getOrgId() : financialAccrualUpdate.getOrgId());
        return financialAccrual;
    }

    public static List<FinancialAccrual> toFinancialAccruals(List<FinancialAccrualDTO> financialAccrualDTOS) {
        return financialAccrualDTOS.stream().map(p -> toFinancialAccrual(p)).collect(Collectors.toList());
    }

    public static List<FinancialAccrualDTO> toFinancialAccrualDTOs(List<FinancialAccrual> financialAccruals) {
        return financialAccruals.stream().map(p -> toFinancialAccrualDTO(p)).collect(Collectors.toList());
    }
}
