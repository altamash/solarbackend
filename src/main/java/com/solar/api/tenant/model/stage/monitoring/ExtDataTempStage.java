package com.solar.api.tenant.model.stage.monitoring;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ext_data_temp_stage")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtDataTempStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 255)
    private String refType;
    private String refId;
    private String subsId;
    @Column(length = 20)
    private String authType;
    private String authU;
    private String authP;
    private String productName;
    @Column(length = 50)
    private String brand;
    @Column(length = 20)
    private String monPlatform;
    @Column(length = 500)
    private String mpJson;
    private Date LastFetchDate;
    private Date endDate;
    @Column(length = 10)
    private String subsStatus;
    @Column(length = 10)
    private String inverterStatus;
    private String groupId;
    private String subscriptionName;
    private String systemSize;
    private Long custAdd;
    @Column(name= "site_location_id")
    private Long siteLocationId;
    @Column(length = 500)
    private String extJson;

}
