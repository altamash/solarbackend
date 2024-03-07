package com.solar.api.tenant.mapper.process.rule;

import com.solar.api.tenant.model.process.rule.RuleExecutionLog;

import java.util.List;
import java.util.stream.Collectors;

public class RuleExecutionLogMapper {

    public static RuleExecutionLog toRuleExecutionLog(RuleExecutionLogDTO ruleExecutionLogDTO) {
        return RuleExecutionLog.builder()
                .id(ruleExecutionLogDTO.getId())
                .billId(ruleExecutionLogDTO.getBillId())
                .jobId(ruleExecutionLogDTO.getJobId())
                .rateCode(ruleExecutionLogDTO.getRateCode())
                .returnedValue(ruleExecutionLogDTO.getReturnedValue())
                .subscriptionMatrixRef(ruleExecutionLogDTO.getSubscriptionMatrixRef())
                .jobExecutionDatetime(ruleExecutionLogDTO.getJobExecutionDatetime())
                .exception(ruleExecutionLogDTO.getException())
                .exceptionLog(ruleExecutionLogDTO.getExceptionLog())
//                .ruleExecuted(ruleExecutionLogDTO.getRuleExecuted())
                .build();
    }

    public static RuleExecutionLogDTO toRuleExecutionLogDTO(RuleExecutionLog ruleExecutionLog) {
        if (ruleExecutionLog == null) {
            return null;
        }
        return RuleExecutionLogDTO.builder()
                .id(ruleExecutionLog.getId())
                .billId(ruleExecutionLog.getBillId())
                .jobId(ruleExecutionLog.getJobId())
                .rateCode(ruleExecutionLog.getRateCode())
                .returnedValue(ruleExecutionLog.getReturnedValue())
                .subscriptionMatrixRef(ruleExecutionLog.getSubscriptionMatrixRef())
                .jobExecutionDatetime(ruleExecutionLog.getJobExecutionDatetime())
                .exception(ruleExecutionLog.getException())
                .exceptionLog(ruleExecutionLog.getExceptionLog())
//                .ruleExecuted(ruleExecutionLog.getRuleExecuted())
                .build();
    }

    public static RuleExecutionLog toUpdatedRuleExecutionLog(RuleExecutionLog ruleExecutionLog,
                                                             RuleExecutionLog ruleExecutionLogUpdate) {
        ruleExecutionLog.setBillId(ruleExecutionLogUpdate.getBillId() == null ? ruleExecutionLog.getBillId() :
                ruleExecutionLogUpdate.getBillId());
        ruleExecutionLog.setJobId(ruleExecutionLogUpdate.getJobId() == null ? ruleExecutionLog.getJobId() :
                ruleExecutionLogUpdate.getJobId());
        ruleExecutionLog.setRateCode(ruleExecutionLogUpdate.getRateCode() == null ? ruleExecutionLog.getRateCode() :
                ruleExecutionLogUpdate.getRateCode());
        ruleExecutionLog.setReturnedValue(ruleExecutionLogUpdate.getReturnedValue() == null ?
                ruleExecutionLog.getReturnedValue() : ruleExecutionLogUpdate.getReturnedValue());
        ruleExecutionLog.setSubscriptionMatrixRef(ruleExecutionLogUpdate.getSubscriptionMatrixRef() == null ?
                ruleExecutionLog.getSubscriptionMatrixRef() : ruleExecutionLogUpdate.getSubscriptionMatrixRef());
        ruleExecutionLog.setJobExecutionDatetime(ruleExecutionLogUpdate.getJobExecutionDatetime() == null ?
                ruleExecutionLog.getJobExecutionDatetime() : ruleExecutionLogUpdate.getJobExecutionDatetime());
        ruleExecutionLog.setException(ruleExecutionLogUpdate.getException() == null ?
                ruleExecutionLog.getException() : ruleExecutionLogUpdate.getException());
        ruleExecutionLog.setExceptionLog(ruleExecutionLogUpdate.getExceptionLog() == null ?
                ruleExecutionLog.getExceptionLog() : ruleExecutionLogUpdate.getExceptionLog());
//        ruleExecutionLog.setRuleExecuted(ruleExecutionLogUpdate.getRuleExecuted() == null ? ruleExecutionLog
//        .getRuleExecuted() : ruleExecutionLogUpdate.getRuleExecuted());
        return ruleExecutionLog;
    }

    public static List<RuleExecutionLog> toRuleExecutionLogs(List<RuleExecutionLogDTO> ruleExecutionLogDTOS) {
        return ruleExecutionLogDTOS.stream().map(r -> toRuleExecutionLog(r)).collect(Collectors.toList());
    }

    public static List<RuleExecutionLogDTO> toRuleExecutionLogDTOs(List<RuleExecutionLog> ruleExecutionLogs) {
        return ruleExecutionLogs.stream().map(r -> toRuleExecutionLogDTO(r)).collect(Collectors.toList());
    }
}
