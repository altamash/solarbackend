package com.solar.api.tenant.model.stage.monitoring;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "ext_data_stage_definition")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtDataStageDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20)
    private String refType; // (Variant)
    private String refId; // (variant ID)
    private String subsId; // “334kj34lk534hlh“
    @Column(length = 20)
    private String authType; // standard
    private String authU;
    private String authP;

    private String productName;
    @Column(length = 50)
    private String brand;
    @Column(length = 20)
    private String monPlatform;
    @Column(length = 500)
    private String mpJson;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date LastFetchDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate; // (OPTIONAL)
    @Column(length = 10)
    private String subsStatus;
    @Column(length = 10)
    private String inverterStatus;
    private String groupId; // (not used but can be used incase of partial refresh)

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String subscriptionName;
    private String systemSize;
    private Long custAdd;
    @Column(name= "site_location_id")
    private  Long siteLocationId;
    @Column(length = 500)
    private String extJson;

}
