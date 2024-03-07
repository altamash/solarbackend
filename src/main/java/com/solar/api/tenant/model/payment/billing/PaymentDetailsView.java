package com.solar.api.tenant.model.payment.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDetailsView {

    @Id
    @Column(name = "account_id")
    BigInteger accountId;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Column(name = "subscription_id")
    BigInteger subscriptionId;
    @Column(name = "premise_no")
    String premiseNo;
    @Column(name = "garden_src")
    String gardenSrc;
    @Column(name = "garden_name")
    String gardenName;
    @Column(name = "subscription_type")
    String subscriptionType;
    @Column(name = "subscription_rate_matrix_id")
    BigInteger subscriptionRate_Matrix_Id;
    @Column(name = "bill_head_id")
    BigInteger billHeadId;
    @Column(name = "billed_amount")
    Double billedAmount;
    @Column(name = "pending_amount")
    Double pendingAmount;
    @Column(name = "billing_month_year")
    String billingMonthYear;
    @Column(name = "bill_status")
    String billStatus;
    @Column(name = "invoice_id")
    BigInteger invoiceId;
    @Column(name = "payment_id")
    BigInteger paymentId;
    @Column(name = "total_paid_amount")
    Double totalPaidAmount;
    @Column(name = "tran_date")
    LocalDateTime transactionDate;
    @Column(name = "source")
    String source;
    @Column(name = "instrument_num")
    String instrumentNum;
    @Column(name = "issuer")
    String issuer;
    @Column(name = "bill_credit")
    Double bill_credit;
    String fullName;

}
