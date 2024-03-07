package com.solar.api.tenant.model.extended.assetHead;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_supplier")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long assetId;
    private Long supplierId;
    //    private Long serialNumber;
    private Boolean primarySupplier;
    private Long scanId;
    private String ext1;
    private String ext2;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
