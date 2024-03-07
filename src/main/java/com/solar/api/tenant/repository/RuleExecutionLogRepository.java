package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleExecutionLogRepository extends JpaRepository<RuleExecutionLog, Long> {
    List<RuleExecutionLog> findBySubscriptionMatrixRef(String subscriptionMatrixRef);

    List<RuleExecutionLog> findRuleExecutionLogByBillId(Long billId);

    List<RuleExecutionLog> findAllByBillIdAndRateCodeOrderByIdAsc(Long billId, String rateCode);
    List<RuleExecutionLog> findAllByBillIdAndRateCodeOrderByIdDesc(Long billId, String rateCode);
}
