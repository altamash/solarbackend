package com.solar.api.saas.repository;

import com.solar.api.saas.model.process.rule.RuleHead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleHeadRepository extends JpaRepository<RuleHead, Long> {

    RuleHead findBySubscriptionCode(String subscriptionCode);

    RuleHead findByRuleId(String ruleId);

    RuleHead findByBillingCodeAndRuleDependency(String billingCode, String ruleDependency);
    RuleHead findByBillingCode(String billingCode);
}
