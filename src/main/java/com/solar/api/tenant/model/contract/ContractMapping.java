package com.solar.api.tenant.model.contract;

import com.solar.api.tenant.model.subscription.CustomerSubscription;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@javax.persistence.Entity
@Table(name = "contract_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sub_contract_id", referencedColumnName = "id")
    private CustomerSubscription subContract; //TODO: To be changed to String when migrated to mongo -- remove this column because we have moved the mapping to customer_subscription
    //    @OneToMany(mappedBy = "contractMapping", cascade = CascadeType.MERGE)
//    List<CustomerSubscription> subscriptions;
    @Column(length = 50)
    private String subContractType;
    @Column(length = 20)
    private String status;
    //  For Mongo DB
    private Long entityId;
    private Long productId;
    private Long variantId;
    private Long reservationId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
