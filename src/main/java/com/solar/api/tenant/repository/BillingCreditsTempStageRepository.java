package com.solar.api.tenant.repository;


import com.solar.api.tenant.model.billingCredits.BillingCreditsTempStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BillingCreditsTempStageRepository extends JpaRepository<BillingCreditsTempStage, Long> {


}
