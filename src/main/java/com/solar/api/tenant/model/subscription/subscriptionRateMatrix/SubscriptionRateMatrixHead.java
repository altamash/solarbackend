package com.solar.api.tenant.model.subscription.subscriptionRateMatrix;

import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.SubscriptionType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subscription_rate_matrix_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRateMatrixHead implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subscriptionCode;
    @Column(unique = true)
    private String subscriptionTemplate;
    private Boolean active;
    @OneToMany(mappedBy = "subscriptionRateMatrixHead", cascade = CascadeType.MERGE)
    private List<SubscriptionRateMatrixDetail> subscriptionRateMatrixDetails;
    @OneToMany(mappedBy = "subscriptionRateMatrixHead", cascade = CascadeType.MERGE)
    private List<CustomerSubscriptionMapping> customerSubscriptionMappings;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_type_id")
    private SubscriptionType subscriptionType;
    /*@OneToMany(mappedBy = "subscriptionRateMatrixHead", cascade = CascadeType.MERGE)
    private List<CustomerSubscription> customerSubscriptions;*/

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
