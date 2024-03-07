package com.solar.api.tenant.model.extended.pallet;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pallet_locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalletLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long palletId;
    private String seqNo;
    private Long locationId;
    private Long rackId;
    private String opened;
    private String openedBy;
    private String openDatetime;
    private String vehicleType;
    private String vehicleRefNum;
    private String mobileRackRef;
    private Long lane;
    private Long depth;
    private Long height;
    private String transferId;
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
