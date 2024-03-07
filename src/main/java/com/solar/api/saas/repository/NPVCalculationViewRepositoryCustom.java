package com.solar.api.saas.repository;

import com.solar.api.saas.model.chart.views.NPVCalculationView;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NPVCalculationViewRepositoryCustom {

    List<Integer> getSubscriptionCount(@Param("accountId") Long accountId);

    Integer getKWDCCount(@Param("accountId") Long accountId);

    Integer getDEPCount(@Param("accountId") Long accountId);

    Integer getYLDCCount(@Param("accountId") Long accountId);

    List<NPVCalculationView> getRateCodes(@Param("subscriptionId") Long subscriptionId,
                                          @Param("matrixId") Long matrixId);

    NPVCalculationView getDEP(@Param("accountId") Long accountId, @Param("subscriptionId") Long subscriptionId);

    NPVCalculationView getYLD(@Param("accountId") Long accountId, @Param("subscriptionId") Long subscriptionId);
}
