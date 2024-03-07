package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPaymentInfo {

    private String externalId;
    private String paymentSrcAlias;
    private String paymentSource;
    private String accountTitle;
    private String accountNo;
    private String routingNo;
    private String accountType;
    private String bankName;
    private String primaryIndicator;
    private String ecApproved;

    @Override
    public String toString() {
        return "CustomerPaymentInfo{" +
                "externalId='" + externalId + '\'' +
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
