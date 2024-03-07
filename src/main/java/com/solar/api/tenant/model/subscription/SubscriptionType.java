package com.solar.api.tenant.model.subscription;

import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subscription_type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Transient
    private String collectionId;
    @Column(unique = true)
    private String subscriptionName;
    private String primaryGroup;
    @Column(length = 10, unique = true)
    private String code;
    private Integer generateCycle;
    private Integer billingCycle;
    private String alias; //(optional)
    private Boolean preGenerate;
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.MERGE) // or ManyToMany ?
    private List<CustomerSubscription> customerSubscriptions;
    @OneToMany(mappedBy = "subscriptionType", cascade = CascadeType.MERGE)
    private List<SubscriptionRateMatrixHead> subscriptionRateMatrixHeads;
    @OneToMany(mappedBy = "type", cascade = CascadeType.MERGE) // or ManyToMany ?
    private List<CustomerSubscription> customerSubscriptionsByType;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
