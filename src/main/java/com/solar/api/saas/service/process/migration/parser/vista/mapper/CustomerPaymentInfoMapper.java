package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.solar.api.tenant.model.payment.info.PaymentInfo;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerPaymentInfoMapper {

    public static PaymentInfo toPaymentInfo(CustomerPaymentInfo customerPaymentInfo) {
        if (customerPaymentInfo == null) {
            return null;
        }
        return PaymentInfo.builder()
                .externalId(customerPaymentInfo.getExternalId())
                .paymentSrcAlias(customerPaymentInfo.getPaymentSrcAlias())
                .paymentSource(customerPaymentInfo.getPaymentSource())
                .accountTitle(customerPaymentInfo.getAccountTitle())
                .accountNumber(customerPaymentInfo.getAccountNo())
                .routingNumber(customerPaymentInfo.getRoutingNo())
                .accountType(customerPaymentInfo.getAccountType())
                .bankName(customerPaymentInfo.getBankName())
                .primaryIndicator(customerPaymentInfo.getPrimaryIndicator() != null ?
                        Boolean.parseBoolean(customerPaymentInfo.getPrimaryIndicator()) : null)
                .ecApproved(customerPaymentInfo.getEcApproved() != null ?
                        Boolean.parseBoolean(customerPaymentInfo.getEcApproved()) : null)
                .build();
    }

    public static List<PaymentInfo> toPaymentInfos(List<CustomerPaymentInfo> customerPaymentInfos) {
        return customerPaymentInfos.stream().map(cpi -> toPaymentInfo(cpi)).collect(Collectors.toList());
    }
}
