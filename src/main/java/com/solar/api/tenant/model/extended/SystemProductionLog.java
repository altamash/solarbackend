package com.solar.api.tenant.model.extended;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "system_production_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemProductionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long siteid;
    private Long assetCompRef;
    private Integer peakPower;
    private String status;
    private Date recDateTime;
    private Long assetId;
    private String premiseNo;
    private String assetDesc;
    private String sourceSystem;
    private String externalAcctId;
    private String ext2;
    private String ext3;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
