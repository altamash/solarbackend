package com.solar.api.tenant.model.extended.pallet;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pallet_container_asset_lists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletContainerAssetList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long palletId;
    private Long assetId;
    private Long serialNo;
    private Long inventoryBarcodeUsed;
    private String status;
    private Long boxInd;
    private Long quantity;
    private Long boxHeight;
    private Long boxLength;
    private Long boxDepth;
    private Long boxRefId;
    private Long unitNetWeight; //gms

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
