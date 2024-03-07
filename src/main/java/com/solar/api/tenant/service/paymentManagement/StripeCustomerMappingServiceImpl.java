package com.solar.api.tenant.service.paymentManagement;


import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.paymentManagement.StripeCustomerMapping;
import com.solar.api.tenant.repository.paymentManagement.StripeCustomerMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StripeCustomerMappingServiceImpl implements StripeCustomerMappingService {
    @Autowired
    private StripeCustomerMappingRepository stripeCustomerMappingRepository;

    @Override
    public StripeCustomerMapping findByEmail(String email) {
        return stripeCustomerMappingRepository.findByEmail(email);
    }

    @Override
    public StripeCustomerMapping save(StripeCustomerMapping stripeCustomerMapping) {
        return stripeCustomerMappingRepository.save(stripeCustomerMapping);
    }

    @Override
    public StripeCustomerMapping create(Entity entity, UserLevelPrivilege userLevelPrivilege, String stripeCustomerId) {
        StripeCustomerMapping stripeCustomerMapping = StripeCustomerMapping.builder()
                .accountId(userLevelPrivilege.getUser().getAcctId())
                .entityId(entity.getId()).referenceId(stripeCustomerId).build();
        return save(stripeCustomerMapping);
    }
}
