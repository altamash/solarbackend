package com.solar.api.tenant.model.extended.assetHead;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "asset_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Client schema only
public class AssetDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String measureCode;//no use
    @Transient
    private String measure;
    private Long measureCodeId;
    private String value;
    private String filterByInd;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;
    private String category;

    @ManyToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id")
    private AssetHead assetHead;

    @Transient
    private MeasureDefinitionTenantDTO measureDefinitionTenant;
    @Transient
    private Long assetHeadId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
