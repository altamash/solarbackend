package com.solar.api.saas.repository;

import com.solar.api.saas.model.chart.views.BillingByCodeView;
import com.solar.api.saas.model.chart.views.BillingDetailView;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillingDetailViewRepositoryCustom {

    List<BillingByCodeView> billingByCodeAndDate(@Param("accountId") Long accountId,
                                                 @Param("subscriptionId") Long subscriptionId,
                                                 @Param("rateCode") String rateCode,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);

    Double lifeTimeSum(@Param("billingCode") String billingCode,
                       @Param("accountId") Long accountId,
                       @Param("subscriptionId") Long subscriptionId);

    List<BillingDetailView> getCumulativeSavings(@Param("accountId") Long accountId,
                                                 @Param("subscriptionId") Long subscriptionId,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);
}
