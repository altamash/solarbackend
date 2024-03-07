package com.solar.api.tenant.model.trueup;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "billcred_recon_detail_garden_v")
@org.hibernate.annotations.Immutable
@Getter
@Setter
public class BillcredReconDetailGardenView {

    @Id
    @Column(name = "invoice_id")
    private Long invoiceId;
    @Column(name = "billing_month_year")
    private Date billingMonthYear;
    @Column(name = "subscription_id")
    private Long subscriptionId;
    @Column(name = "garden_id")
    private String gardenId;
    @Column(name = "premise_no")
    private String premiseNo;
    @Column(name = "garden_name")
    private String gardenName;
    @Column(name = "billing_credits")
    private Double billingCredits;
    @Column(name = "billed_amt")
    private Double billedAmt;
    @Column(name = "paid_amt")
    private Double paidAmt;
}
