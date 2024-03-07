package com.solar.api.tenant.service.process.rule;

import com.solar.api.tenant.model.process.rule.RuleExecutionLog;

import java.util.List;

public interface RuleExecutionLogService {

    RuleExecutionLog save(RuleExecutionLog ruleExecutionLog);

    RuleExecutionLog update(RuleExecutionLog ruleExecutionLog);

    List<RuleExecutionLog> save(List<RuleExecutionLog> ruleExecutionLogs);

    RuleExecutionLog findById(Long id);

    List<RuleExecutionLog> findByBillId(Long id);

    List<RuleExecutionLog> findBySubscriptionMatrixRef(String subscriptionMatrixRef);

    List<RuleExecutionLog> findAll();

    List<RuleExecutionLog> findAllByBillIdAndRateCodeOrderByIdAsc(Long billId, String rateCode);

    void delete(Long id);

    void deleteAll();

    List<RuleExecutionLog> findAllByBillIdAndRateCodeOrderByIdDesc(Long billId, String rateCode);
}
