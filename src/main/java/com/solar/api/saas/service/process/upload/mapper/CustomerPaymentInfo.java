package com.solar.api.saas.service.process.upload.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPaymentInfo {

    private String action;
    @JsonProperty("payment_info_id")
    private Long paymentInfoId;
    @JsonProperty("acct_id")
    private Long acctId;
    private String externalId;//
    @JsonProperty("payment_src_alias")
    private String paymentSrcAlias;
    @JsonProperty("payment_source")
    private String paymentSource;
    @JsonProperty("account_title")
    private String accountTitle;
    @JsonProperty("account_no")
    private String accountNo;
    @JsonProperty("routing_no")
    private String routingNo;
    @JsonProperty("account_type")
    private String accountType;
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("primary_indicator")
    private String primaryIndicator;
    @JsonProperty("ec_approved")
    private String ecApproved;

    @Override
    public String toString() {
        return "CustomerPaymentInfo{" +
                "action='" + action + '\'' +
                ", acctId=" + acctId +
                ", externalId='" + externalId + '\'' +
                ", paymentSrcAlias='" + paymentSrcAlias + '\'' +
                ", paymentSource='" + paymentSource + '\'' +
                ", accountTitle='" + accountTitle + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", routingNo='" + routingNo + '\'' +
                ", accountType='" + accountType + '\'' +
                ", bankName='" + bankName + '\'' +
                ", primaryIndicator='" + primaryIndicator + '\'' +
                ", ecApproved='" + ecApproved + '\'' +
                '}';
    }
}
