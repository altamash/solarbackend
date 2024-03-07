package com.solar.api.tenant.model.extended.assetHead;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_block_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetBlockDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long assetId;
    private Long refBlockId;
    private Long measureId;
    private String measureValue;
    private Long recordNumber;//line no generated from backend
    private Long assetRefId;

    @Transient
    private Boolean measureUnique;
    @Transient
    private String palletNo;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
