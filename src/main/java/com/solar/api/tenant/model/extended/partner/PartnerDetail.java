package com.solar.api.tenant.model.extended.partner;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partner_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Transient
    private String measure;
    private Long measureCodeId;
    private String value;

    @ManyToOne
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    private PartnerHead partnerHead;

    @Transient
    private MeasureDefinitionTenantDTO measureDefinitionTenant;
    @Transient
    private Long partnerHeadId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
