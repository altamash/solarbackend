package com.solar.api.tenant.model.subscription;

import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "customer_subscription", indexes = {@Index(columnList = "id", name = "id_index")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id")
    private User userAccount;
    @Transient
    private Long userAccountId;
    private Date startDate;
    private Date endDate;
    private String subscriptionStatus;
    private String billStatus;
    private String arrayLocationRef;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "address_id")
    private Address address;
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.MERGE)
    private List<CustomerSubscriptionMapping> customerSubscriptionMappings;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "type", referencedColumnName = "code")
    private SubscriptionType type;
    private String subscriptionType;
    private String subscriptionTemplate;
    /*@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_rate_matrix_head_id")*/
    @Transient
    private SubscriptionRateMatrixHead subscriptionRateMatrixHead; // seems redundant
    private Long subscriptionRateMatrixId; // always populated
    /*@OneToMany(mappedBy = "subscription", cascade = CascadeType.MERGE)*/
    @Transient
    private List<BillingHead> billingHeads;
    @Transient
    private String externalId;
    @Transient
    private String action;
    @Transient
    private Date autoTerminationDate;
    @Transient
    private String rollValue;
    @Transient
    private Boolean markedForDeletion;

    private Date terminationDate;
    private Date closedDate;
    private String terminationReason;

//    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "contract_mapping_id")
//    private ContractMapping contractMapping;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String terminationNotificationSent;

    @Column(name = "ext_subs_id")
    private String extSubsId;
    @Column(name = "invoice_template_id")
    private Long invoiceTemplateId;

    private String gardenSrc;
}
