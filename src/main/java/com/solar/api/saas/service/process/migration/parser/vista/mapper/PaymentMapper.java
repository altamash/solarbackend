package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.solar.api.helper.Utility.SYSTEM_DATE_FORMAT;
import static com.solar.api.helper.Utility.SYSTEM_DATE_TIME_FORMAT;

public class PaymentMapper {

    private final static Logger LOGGER = LoggerFactory.getLogger(PaymentMapper.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(SYSTEM_DATE_FORMAT);
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(SYSTEM_DATE_TIME_FORMAT);

    public static PaymentTransactionDetail toPaymentTransactionDetail(CustomerPayment customerPayment) {
        if (customerPayment == null) {
            return null;
        }
        try {
            String dateTime = customerPayment.getTranDateTime();
            return PaymentTransactionDetail.builder()
                    .invoiceRefId(customerPayment.getInvoiceRefId())
                    .payDetId(customerPayment.getPayDetId())
                    .paymentCode(customerPayment.getPaymentCode())
                    .amt(customerPayment.getAmt())
                    .source(customerPayment.getSource())
                    .sourceId(customerPayment.getSourceId())
                    .tranDate(dateTime != null && !dateTime.isEmpty() ?
                            (dateTime.trim().length() == 19 ? dateTimeFormat.parse(dateTime.trim()) :
                                    Utility.getStartOfDate(dateFormat.parse(dateTime.trim())))
                            : dateTimeFormat.parse(dateTimeFormat.format(Utility.getStartOfDate(new Date()))))
                    .instrumentNum(customerPayment.getInstrumentNum())
                    .issuer(customerPayment.getIssuerType())
                    .issuerId(customerPayment.getIssuerRefNum())
                    .issuerReconStatus(customerPayment.getIssuerReconStatus())
                    .reconDate(customerPayment.getIssuerReconDate() != null && !customerPayment.getIssuerReconDate().isEmpty() ? dateFormat.parse(customerPayment.getIssuerReconDate()) : null)
                    .build();
        } catch (ParseException e) {
            LOGGER.error("Error parsing for " + customerPayment.getInvoiceRefId() + " " + e.getMessage(), e);
        }
        return null;
    }

    public static CustomerPayment toCustomerPayment(PaymentTransactionDetail detail) {
        if (detail == null) {
            return null;
        }
        return CustomerPayment.builder()
                .invoiceRefId(detail.getInvoiceRefId())
                .payDetId(detail.getPayDetId())
                .paymentCode(detail.getPaymentCode())
                .amt(detail.getAmt())
                .source(detail.getSource())
                .sourceId(detail.getSourceId())
                .tranDateTime(detail.getTranDate() != null ? dateTimeFormat.format(detail.getTranDate()) :
                        dateTimeFormat.format(dateTimeFormat.format(new Date())))
                .instrumentNum(detail.getInstrumentNum())
                .issuerType(detail.getIssuer())
                .issuerRefNum(detail.getIssuerId())
                .issuerReconStatus(detail.getIssuerReconStatus())
                .issuerReconDate(detail.getReconDate() != null ? dateFormat.format(detail.getReconDate()) : null)
                .build();
    }

    public static List<PaymentTransactionDetail> toPaymentTransactionDetails(List<CustomerPayment> customerPayments) {
        return customerPayments.stream().map(p -> toPaymentTransactionDetail(p)).collect(Collectors.toList());
    }

    public static List<CustomerPayment> toCustomerPayments(List<PaymentTransactionDetail> details) {
        return details.stream().map(p -> toCustomerPayment(p)).collect(Collectors.toList());
    }

    public static PaymentTransactionDetail resetPaymentTransactionDetail(PaymentTransactionDetail customerPayment,
                                                                         PaymentTransactionDetail customerPaymentUpdate) {
        customerPayment.setPaymentTransactionHead(customerPaymentUpdate.getPaymentTransactionHead());
        customerPayment.setTranDate(customerPaymentUpdate.getTranDate());
        customerPayment.setAmt(customerPaymentUpdate.getAmt());
        customerPayment.setOrigAmt(customerPaymentUpdate.getOrigAmt());
        customerPayment.setStatus(customerPaymentUpdate.getStatus());
        customerPayment.setSource(customerPaymentUpdate.getSource());
        customerPayment.setSourceId(customerPaymentUpdate.getSourceId());
        customerPayment.setInstrumentNum(customerPaymentUpdate.getInstrumentNum());
        customerPayment.setIssuer(customerPaymentUpdate.getIssuer());
        customerPayment.setIssuerId(customerPaymentUpdate.getIssuerId());
        customerPayment.setIssuerReconStatus(customerPaymentUpdate.getIssuerReconStatus());
        customerPayment.setReconExpectedDate(customerPaymentUpdate.getReconExpectedDate());
        customerPayment.setReconDate(customerPaymentUpdate.getReconDate());
        return customerPayment;
    }

    public static PaymentTransactionDetail toUpdatedPaymentTransactionDetail(PaymentTransactionDetail customerPayment
            , PaymentTransactionDetail customerPaymentUpdate) {
        if (customerPaymentUpdate.getPaymentTransactionHead() != null) {
            customerPayment.setPaymentTransactionHead(customerPaymentUpdate.getPaymentTransactionHead());
        }
        if (customerPaymentUpdate.getTranDate() != null) {
            customerPayment.setTranDate(customerPaymentUpdate.getTranDate());
        }
        if (customerPaymentUpdate.getAmt() != null) {
            customerPayment.setAmt(customerPaymentUpdate.getAmt());
        }
        if (customerPaymentUpdate.getOrigAmt() != null) {
            customerPayment.setOrigAmt(customerPaymentUpdate.getOrigAmt());
        }
        if (customerPaymentUpdate.getStatus() != null) {
            customerPayment.setStatus(customerPaymentUpdate.getStatus());
        }
        if (customerPaymentUpdate.getSource() != null) {
            customerPayment.setSource(customerPaymentUpdate.getSource());
        }
        if (customerPaymentUpdate.getSourceId() != null) {
            customerPayment.setSource(customerPaymentUpdate.getSourceId());
        }
        if (customerPaymentUpdate.getInstrumentNum() != null) {
            customerPayment.setInstrumentNum(customerPaymentUpdate.getInstrumentNum());
        }
        if (customerPaymentUpdate.getIssuer() != null) {
            customerPayment.setIssuer(customerPaymentUpdate.getIssuer());
        }
        if (customerPaymentUpdate.getIssuerId() != null) {
            customerPayment.setIssuerId(customerPaymentUpdate.getIssuerId());
        }
        if (customerPaymentUpdate.getIssuerReconStatus() != null) {
            customerPayment.setIssuerReconStatus(customerPaymentUpdate.getIssuerReconStatus());
        }
        if (customerPaymentUpdate.getReconExpectedDate() != null) {
            customerPayment.setReconExpectedDate(customerPaymentUpdate.getReconExpectedDate());
        }
        if (customerPaymentUpdate.getReconDate() != null) {
            customerPayment.setReconDate(customerPaymentUpdate.getReconDate());
        }
        return customerPayment;
    }

}
