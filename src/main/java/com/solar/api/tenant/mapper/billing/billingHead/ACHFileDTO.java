package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ACHFileDTO {

    @JsonProperty("head_id")
    private Long headId;
    @JsonProperty("subs_id")
    private Long subsId;
    @JsonProperty("Account Number")
    private String accountNumber;
    @JsonProperty("Amount")
    private Double amount;
    @JsonProperty("Discretionary Data")
    private Long discretionaryData; //invoice no.

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String effectiveDate; //day after the txn was generated
    @JsonProperty("Identification")
    private String identification; //PN
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Payment Information")
    private String paymentInformation; //CR or DR
    @JsonProperty("Routing Transit")
    private String routingNumber;
    @JsonProperty("Transaction Code")
    private String transactionCode;
    @JsonProperty("Transaction Code Custom")
    private Long transactionCodeCustom;

    @Override
    public String toString() {
        return "ACHFileDTO{" +
                "headId=" + headId +
                ", subsId=" + subsId +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", discretionaryData=" + discretionaryData +
                ", effectiveDate='" + effectiveDate + '\'' +
                ", identification='" + identification + '\'' +
                ", name='" + name + '\'' +
                ", paymentInformation='" + paymentInformation + '\'' +
                ", routingNumber='" + routingNumber + '\'' +
                ", transactionCode='" + transactionCode + '\'' +
                ", transactionCodeCustom=" + transactionCodeCustom +
                '}';
    }
}
