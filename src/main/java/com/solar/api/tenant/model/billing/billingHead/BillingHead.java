package com.solar.api.tenant.model.billing.billingHead;

import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "billing_head", indexes = {
        @Index(columnList = "userAccountId", name = "userAccountId_index"),
        @Index(columnList = "subscriptionId", name = "subscriptionId_index")
})
// , uniqueConstraints = @UniqueConstraint(columnNames = {"subscriptionId", "billingMonthYear"})
@Audited(withModifiedFlag = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
    private BillingInvoice invoice;
    @Transient
    private Long invoiceId;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_account")
    @NotAudited
    private User userAccount; // userAccountId; mandatory
    private Long userAccountId;
    /*@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription")
    @NotAudited*/
    @Transient
    private CustomerSubscription subscription; // seems redundant
    private Long subscriptionId; // always populated
    @Column(length = 30)
    private String custProdId; // always populated
    private String billType;
    private Double amount;
    private Date generatedOn;
    private String billingMonthYear;
    private String billStatus;
    private Date invoiceDate;
    private Date dueDate;
    private Date defermentDate;
    @OneToMany(mappedBy = "billingHead", cascade = CascadeType.MERGE)
    private Set<BillingDetail> billingDetails;

    @Column(columnDefinition = "boolean default false")
    private Boolean billSkip;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean paymentLocked;
    private Boolean reverseLocked;
    private Boolean reconcileLocked;
    @Column(name = "publish_ind")
    private Integer publishIndicator;
}
