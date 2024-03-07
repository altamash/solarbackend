package com.solar.api.saas.model.chart.views;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NPVCalculationView {

    @Id
    @Column(name = "rate_code")
    private String rateCode;
    @Column(name = "value")
    private String value;
    @Column(name = "subscription_id")
    private String subscriptionId;
    @Column(name = "matrix_id")
    private Long matrixId;
    @Column(name = "account_id")
    private Long accountId;

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getMatrixId() {
        return matrixId;
    }

    public void setMatrixId(Long matrixId) {
        this.matrixId = matrixId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}

