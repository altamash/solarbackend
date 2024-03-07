package com.solar.api.saas.model.chart.views;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@Builder
public class BillingByCodeView {

    @Column(name = "value")
    private Double value;
    @Column(name = "billing_code")
    private String billingCode;
    @Column(name = "billing_month_year")
    private Date billingMonthYear;
    @Column(name = "month")
    private String month;

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

    public Date getBillingMonthYear() {
        return billingMonthYear;
    }

    public void setBillingMonthYear(Date billingMonthYear) {
        this.billingMonthYear = billingMonthYear;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
