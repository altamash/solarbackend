package com.solar.api.tenant.model.extended.assetHead;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_serial_number")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetSerialNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String serialNumber;
    private Long assetId;
    private Long suppId;
    private Long manuId;
    private String notes;

    private String palletNo;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
