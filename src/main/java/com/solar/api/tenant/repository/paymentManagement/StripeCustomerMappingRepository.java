package com.solar.api.tenant.repository.paymentManagement;


import com.solar.api.tenant.model.paymentManagement.StripeCustomerMapping;

import com.solar.api.tenant.model.process.JobManagerTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface StripeCustomerMappingRepository extends JpaRepository<StripeCustomerMapping, Long> {

    @Query("SELECT scm FROM StripeCustomerMapping scm LEFT JOIN User u on u.acctId = scm.accountId where u.emailAddress = :email")
    StripeCustomerMapping findByEmail(@Param("email") String email);
}
