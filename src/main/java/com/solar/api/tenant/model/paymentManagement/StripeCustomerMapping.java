package com.solar.api.tenant.model.paymentManagement;

import lombok.*;


import javax.persistence.*;

@Entity
@Table(name = "stripe_customer_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeCustomerMapping
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "entity_id")
    private Long entityId;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "reference_id")
    private String referenceId;
}
