package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import com.solar.api.tenant.model.billingCredits.SearchParamsBillingCredits;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillingCreditsService {

    /**
     * Billing Credits
     */


    List<BillingCredits> addOrUpdateBillingCredits(List<BillingCredits> billingCredits);

    List<BillingCredits> save(List<BillingCredits> billingCredits);

    void mapBillingCredits();

    void deleteAll();

    /**
     * Billing Credits Csv
     */

    List<BillingCreditsCsv> findBillingCreditsCsvByPaymentType(String paymentType);

    List<BillingCreditsCsv> saveAll(List<BillingCreditsCsv> billingCreditsCsv);

    void deleteAllBillingCredits();

    Page<BillingCredits> comprehensiveSearch(Pageable pageable, SearchParamsBillingCredits searchParamsBillingCredits);
    List<BillingCredits> findAll();

    BillingCredits findByPremiseNoAndMonthAndGardenSrc(String premiseNo, String month, String gardenId);
    List<BillingCredits> findAllByPremiseNoAndMonthAndGardenSrc(List<String> premiseNos, String month, List<String> gardenIds);
    void dataConversionForBillingCredits(Long jobId);

    List<BillingCredits> manageBillingCreditsForProjection(List<CustomerSubscription> customerSubscriptions, List<String> months,Long jobId);
}
