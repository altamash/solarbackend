package com.solar.api.tenant.model.extended.project;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_inventories_serial")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInventorySerial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long assetSerialNumberId;
    private Long locationId;
    private String status;

    @ManyToOne
    @JoinColumn(name = "project_inventory_id", referencedColumnName = "id")
    private ProjectInventory projectInventory;

    @Transient
    private String serialNumber;
    @Transient
    private Long projectInventoryId;
    @Transient
    private String palletNumber;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
