package com.solar.api.tenant.model.billingCredits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BillingCreditsPostProcessing {

    @Id
    @Column(name = "rate_code")
    private String rateCode;
    @Column(name = "value")
    private String value;
    @Column(name = "customer_subscription_id")
    private Long subscriptionId;
    @Column(name = "subscription_rate_matrix_id")
    private Long subscriptionRateMatrixId;
    @Column(name = "measure_definition_id")
    private Long measureDefinitionId;

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public String getValue() {
        return value;
    }

    public void setVal(String value) {
        this.value = value;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionRateMatrixId() {
        return subscriptionRateMatrixId;
    }

    public void setSubscriptionRateMatrixId(Long subscriptionRateMatrixId) {
        this.subscriptionRateMatrixId = subscriptionRateMatrixId;
    }

    public Long getMeasureDefinitionId() {
        return measureDefinitionId;
    }

    public void setMeasureDefinitionId(Long measureDefinitionId) {
        this.measureDefinitionId = measureDefinitionId;
    }
}
