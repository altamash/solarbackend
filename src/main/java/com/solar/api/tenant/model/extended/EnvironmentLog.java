package com.solar.api.tenant.model.extended;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "environment_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long assetRefId;
    private Long siteRefId;
    private Long sensorId;
    private String sensorType;
    private Long reading;
    private Date datetime;
    private Long batchId;
    private String sourceSystem;
    private String notes;
    private String ext1;
    private String ext2;
    private String ext3;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
