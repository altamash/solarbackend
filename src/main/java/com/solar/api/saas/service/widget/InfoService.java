package com.solar.api.saas.service.widget;

import java.util.Map;

public interface InfoService {

    Long getCustomersCountByStatus(String status);

    Double getReceivableAggregate(String billStatus);

    Double findLifeTimeSum(String billingCode, Long accountId, Long subscriptionId);

    void analyze();

    void npv();

    void absav();

    void tpf();

    void mpa();

    void lifeTimeSumBatch();

    Map<String, Object> calculateNPV(Long accountId, Long subscriptionId);


}
