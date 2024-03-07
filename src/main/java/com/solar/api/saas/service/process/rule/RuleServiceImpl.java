package com.solar.api.saas.service.process.rule;

import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.tenant.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@Transactional("masterTransactionManager")
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private RulesFactory rulesFactory;

    @Override
    public RuleHead addOrUpdate(RuleHead ruleHead) {
        return ruleHeadRepository.save(ruleHead);
    }

    @Override
    public RuleHead findBySubscriptionCode(String subscriptionCode) {
        return ruleHeadRepository.findBySubscriptionCode(subscriptionCode);
    }
}
