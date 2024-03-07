package com.solar.api.tenant.model.ca;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ca_referral_info")
public class CaReferralInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "source")
    private String source; //newspaper, social

    @Column(name = "rep_id")
    private Long repId;

    @Column(name = "promo_code")
    private String promoCode;

    @ManyToOne
    @JoinColumn(name = "entity_id")
    private com.solar.api.tenant.model.contract.Entity entity;
}
