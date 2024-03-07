package com.solar.api.tenant.model.billing.BillingInvoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "invoice_customer_v")
@Immutable
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceCustomerV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonIgnore
    private String id;
    private Long invoiceId;
    private Date dateOfInvoice;
    private String billStatus;
    private Long customerSubscriptionId;
    private String portalSubsId;
    private String billingMonthYear;
    private Double amount;
    private String premise;
    private String garden;
    private Long accountId;
    private String invoiceUrl;
}
