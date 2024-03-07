package com.solar.api.tenant.model.extended.assetHead;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "asset_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String assetName;
    private Long registerId;
    private String description;
    private Date regDate;
    private Date activeDate;
    private String status;
    private Boolean recordLevelInd;

    @JsonIgnore
    @OneToMany(mappedBy = "assetHead", cascade = CascadeType.MERGE)
    private List<AssetDetail> assetDetails;

    @Transient
    private String blocks;
    @Transient
    private RegisterHierarchy registerHierarchy;
    private String serialized;//yes/no
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
