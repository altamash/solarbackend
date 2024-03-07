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
public class BillingDetailView {

    @Id
    @Column(name = "value")
    private Double value;
    @Column(name = "billing_code")
    private String billingCode;
    @Column(name = "subscription_id")
    private Long subscriptionId;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "billing_month_year")
    private String billingMonthYear;
    @Column(name = "month")
    private String month;
    @Column(name = "description")
    private String description;
    @Column(name = "bill_status")
    private String billStatus;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getBillingCode() {
        return billingCode;
    }

    public void setBillingCode(String billingCode) {
        this.billingCode = billingCode;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getBillingMonthYear() {
        return billingMonthYear;
    }

    public void setBillingMonthYear(String billingMonthYear) {
        this.billingMonthYear = billingMonthYear;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }
}
