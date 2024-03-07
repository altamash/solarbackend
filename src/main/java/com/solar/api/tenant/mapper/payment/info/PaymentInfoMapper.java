package com.solar.api.tenant.mapper.payment.info;

import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.model.payment.info.PaymentInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PaymentInfoMapper {

    public static PaymentInfo toPaymentInfo(PaymentInfoDTO paymentInfoDTO) {
        return PaymentInfo.builder()
                .id(paymentInfoDTO.getId())
                .portalAccount(UserMapper.toUser(paymentInfoDTO.getPortalAccount()))
                .acctId(paymentInfoDTO.getAcctId())
                .paymentSrcAlias(paymentInfoDTO.getPaymentSrcAlias())
                .sequenceNumber(paymentInfoDTO.getSequenceNumber())
                .paymentSource(paymentInfoDTO.getPaymentSource())
                .accountTitle(paymentInfoDTO.getAccountTitle())
                .accountNumber(paymentInfoDTO.getAccountNo())
                .routingNumber(paymentInfoDTO.getRoutingNo())
                .accountType(paymentInfoDTO.getAccountType())
                .bankName(paymentInfoDTO.getBankName())
                .primaryIndicator(paymentInfoDTO.getPrimaryIndicator())
                .ecApproved(paymentInfoDTO.getEcApproved())
//                .cvv(paymentInfoDTO.getCvvNo())
//                .cardNumber(paymentInfoDTO.getCardNo())
                .cardProvider(paymentInfoDTO.getCardProvider())
                .cardType(paymentInfoDTO.getCardType())
                .isPrimary(paymentInfoDTO.getIsPrimary())
                .build();
    }

    public static PaymentInfoDTO toPaymentInfoDTO(PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return null;
        }
        String acctNumber = paymentInfo.getAccountNumber();
        String routingNumber = paymentInfo.getRoutingNumber();
//        String cvvNumber = paymentInfo.getCvv();
//        String cardNumber = paymentInfo.getCardNumber();
        return PaymentInfoDTO.builder()
                .id(paymentInfo.getId())
//                .portalAccount(UserMapper.toUserDTO(paymentInfo.getPortalAccount()))
//                .portalAccountId(paymentInfo.getPortalAccount() != null ? UserMapper.toUserDTO(paymentInfo
//                .getPortalAccount()).getAcctId() : null)
                .acctId(paymentInfo.getAcctId())
                .paymentSrcAlias(paymentInfo.getPaymentSrcAlias())
                .sequenceNumber(paymentInfo.getSequenceNumber())
                .paymentSource(paymentInfo.getPaymentSource())
                .accountTitle(paymentInfo.getAccountTitle())
                .accountNo( acctNumber !=null ?
                        String.join("", Collections.nCopies((acctNumber.length() - acctNumber.length() >= 4 ? 4 :
                        acctNumber.length()), "*")) +
                        acctNumber.substring(acctNumber.length() - acctNumber.length() >= 4 ? 4 : acctNumber.length()) : null)
                .routingNo(routingNumber != null ? String.join("", Collections.nCopies((routingNumber.length() - routingNumber.length() >= 4
                        ? 4 : routingNumber.length()), "*")) +
                        routingNumber.substring(routingNumber.length() - routingNumber.length() >= 4 ? 4 :
                                routingNumber.length()) : null)
                .accountType(paymentInfo.getAccountType())
                .bankName(paymentInfo.getBankName())
                .primaryIndicator(paymentInfo.getPrimaryIndicator())
                .ecApproved(paymentInfo.getEcApproved())
//                .cvvNo( cvvNumber != null ? String.join("", Collections.nCopies((cvvNumber.length() - cvvNumber.length() >= 4 ? 4 :
//                        cvvNumber.length()), "*")) +
//                        cvvNumber.substring(cvvNumber.length() - cvvNumber.length() >= 4 ? 4 : cvvNumber.length()) : null)
//                .cardNo( cardNumber != null ? String.join("", Collections.nCopies((cardNumber.length() - cardNumber.length() >= 4 ? 4 :
//                        cardNumber.length()), "*")) +
//                        cardNumber.substring(cardNumber.length() - cardNumber.length() >= 4 ? 4 : cardNumber.length()) : null)
                .cardProvider(paymentInfo.getCardProvider())
                .cardType(paymentInfo.getCardType())
                .build();
    }

    public static PaymentInfoDTO toPaymentInfoDecodedDTO(PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return null;
        }
        return PaymentInfoDTO.builder()
                .accountNo(paymentInfo.getAccountNumber())
                .routingNo(paymentInfo.getRoutingNumber())
//                .cvvNo(paymentInfo.getCvv())
//                .cardNo(paymentInfo.getCardNumber())
                .build();
    }

    public static PaymentInfo toUpdatedPaymentInfo(PaymentInfo paymentInfo, PaymentInfo paymentInfoUpdate) {
        paymentInfo.setPaymentSrcAlias(paymentInfoUpdate.getPaymentSrcAlias() == null ?
                paymentInfo.getPaymentSrcAlias() :
                paymentInfoUpdate.getPaymentSrcAlias());
        paymentInfo.setPortalAccount(paymentInfoUpdate.getPortalAccount() == null ? paymentInfo.getPortalAccount() :
                paymentInfoUpdate.getPortalAccount());
        paymentInfo.setSequenceNumber(paymentInfoUpdate.getSequenceNumber() == null ?
                paymentInfo.getSequenceNumber() : paymentInfoUpdate.getSequenceNumber());
        paymentInfo.setPaymentSource(paymentInfoUpdate.getPaymentSource() == null ? paymentInfo.getPaymentSource() :
                paymentInfoUpdate.getPaymentSource());
        paymentInfo.setAccountTitle(paymentInfoUpdate.getAccountTitle() == null ? paymentInfo.getAccountTitle() :
                paymentInfoUpdate.getAccountTitle());
        paymentInfo.setAccountNumber(paymentInfoUpdate.getAccountNumber() == null ? paymentInfo.getAccountNumber() :
                (paymentInfoUpdate.getAccountNumber().contains("*") ? paymentInfo.getAccountNumber() :
                        paymentInfoUpdate.getAccountNumber()));
        paymentInfo.setRoutingNumber(paymentInfoUpdate.getRoutingNumber() == null ? paymentInfo.getRoutingNumber() :
                (paymentInfoUpdate.getRoutingNumber().contains("*") ? paymentInfo.getRoutingNumber() :
                        paymentInfoUpdate.getRoutingNumber()));
        paymentInfo.setAccountType(paymentInfoUpdate.getAccountType() == null ? paymentInfo.getAccountType() :
                paymentInfoUpdate.getAccountType());
        paymentInfo.setBankName(paymentInfoUpdate.getBankName() == null ? paymentInfo.getBankName() :
                paymentInfoUpdate.getBankName());
        paymentInfo.setPrimaryIndicator(paymentInfoUpdate.getPrimaryIndicator() == null ?
                paymentInfo.getPrimaryIndicator() : paymentInfoUpdate.getPrimaryIndicator());
        paymentInfo.setEcApproved(paymentInfoUpdate.getEcApproved() == null ? paymentInfo.getEcApproved() :
                paymentInfoUpdate.getEcApproved());
//        paymentInfo.setCvv(paymentInfoUpdate.getCvv() == null ? paymentInfo.getCvv() :
//                (paymentInfoUpdate.getCvv().contains("*") ? paymentInfo.getCvv() :
//                        paymentInfoUpdate.getCvv()));
//        paymentInfo.setCardNumber(paymentInfoUpdate.getCardNumber() == null ? paymentInfo.getCardNumber() :
//                (paymentInfoUpdate.getCardNumber().contains("*") ? paymentInfo.getCardNumber() :
//                        paymentInfoUpdate.getCardNumber()));
        paymentInfo.setCardProvider(paymentInfoUpdate.getCardProvider() == null ? paymentInfo.getCardProvider() :
                paymentInfoUpdate.getCardProvider());
        paymentInfo.setCardType(paymentInfoUpdate.getCardType() == null ? paymentInfo.getCardType() :
                paymentInfoUpdate.getCardType());
        return paymentInfo;
    }

    public static Set<PaymentInfo> toPaymentInfos(List<PaymentInfoDTO> paymentInfoDTOS) {
        return paymentInfoDTOS.stream().map(pi -> toPaymentInfo(pi)).collect(Collectors.toSet());
    }

    public static List<PaymentInfoDTO> toPaymentInfoDTOs(List<PaymentInfo> paymentInfos) {
        return paymentInfos.stream().map(pi -> toPaymentInfoDTO(pi)).collect(Collectors.toList());
    }

    public static List<PaymentInfoDTO> toPaymentInfoDecodedDTOs(List<PaymentInfo> paymentInfos) {
        return paymentInfos.stream().map(pi -> toPaymentInfoDecodedDTO(pi)).collect(Collectors.toList());
    }
}
