package com.solar.api.saas.service.process.rule;

import com.solar.api.saas.model.process.rule.RuleHead;

public interface RuleService {

    RuleHead addOrUpdate(RuleHead ruleHead);

    RuleHead findBySubscriptionCode(String subscriptionCode);
}
