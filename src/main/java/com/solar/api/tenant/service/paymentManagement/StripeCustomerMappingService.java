package com.solar.api.tenant.service.paymentManagement;


import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.paymentManagement.StripeCustomerMapping;

public interface StripeCustomerMappingService {

    StripeCustomerMapping findByEmail(String email);
    StripeCustomerMapping save(StripeCustomerMapping stripeCustomerMapping);
    StripeCustomerMapping create(Entity entity, UserLevelPrivilege userLevelPrivilege, String stripeCustomerId);



}
