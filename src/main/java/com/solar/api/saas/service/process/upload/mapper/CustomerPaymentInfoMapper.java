package com.solar.api.saas.service.process.upload.mapper;

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
                .action(customerPaymentInfo.getAction())
                .id(customerPaymentInfo.getPaymentInfoId() != null && customerPaymentInfo.getPaymentInfoId().equals("") ? null : customerPaymentInfo.getPaymentInfoId())
                .acctId(customerPaymentInfo.getAcctId() != null && customerPaymentInfo.getAcctId().equals("") ? null : customerPaymentInfo.getAcctId())
                .paymentSrcAlias(customerPaymentInfo.getPaymentSrcAlias() != null && customerPaymentInfo.getPaymentSrcAlias().equals("") ? null : customerPaymentInfo.getPaymentSrcAlias())
                .paymentSource(customerPaymentInfo.getPaymentSource() != null && customerPaymentInfo.getPaymentSource().equals("") ? null : customerPaymentInfo.getPaymentSource())
                .accountTitle(customerPaymentInfo.getAccountTitle() != null && customerPaymentInfo.getAccountTitle().equals("") ? null : customerPaymentInfo.getAccountTitle())
                .accountNumber(customerPaymentInfo.getAccountNo() != null && customerPaymentInfo.getAccountNo().equals("") ? null : customerPaymentInfo.getAccountNo())
                .routingNumber(customerPaymentInfo.getRoutingNo() != null && customerPaymentInfo.getRoutingNo().equals("") ? null : customerPaymentInfo.getRoutingNo())
                .accountType(customerPaymentInfo.getAccountType() != null && customerPaymentInfo.getAccountType().equals("") ? null : customerPaymentInfo.getAccountType())
                .bankName(customerPaymentInfo.getBankName() != null && customerPaymentInfo.getBankName().equals("") ? null : customerPaymentInfo.getBankName())
                .primaryIndicator(customerPaymentInfo.getPrimaryIndicator() != null && !customerPaymentInfo.getPrimaryIndicator().isEmpty() ?
                        Boolean.parseBoolean(customerPaymentInfo.getPrimaryIndicator()) : null)
                .ecApproved(customerPaymentInfo.getEcApproved() != null && !customerPaymentInfo.getEcApproved().isEmpty()?
                        Boolean.parseBoolean(customerPaymentInfo.getEcApproved()) : null)
                .build();
    }

    public static List<PaymentInfo> toPaymentInfos(List<CustomerPaymentInfo> customerPaymentInfos) {
        return customerPaymentInfos.stream().map(cpi -> toPaymentInfo(cpi)).collect(Collectors.toList());
    }
}
