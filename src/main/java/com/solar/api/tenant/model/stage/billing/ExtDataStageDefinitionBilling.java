package com.solar.api.tenant.model.stage.billing;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "ext_data_stage_definition_billing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtDataStageDefinitionBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(length = 20)
    private String refType;
    private String refId;
    private String subsId;
    private String subscriptionType;
    private String category;
    private String variantAlias;
    @Column(length = 20)
    private String parserCode;
    private Boolean preGenerate ;
    private Long physicalLocationId;
    @Column(length = 500)
    private String billingJson;
    private Date startDate;
    private Date endDate;
    @Column(length = 10)
    private String subsStatus;
    private Integer billingCycle; // equal to generate_cycle in subscription_type
    private Integer billingFrequency;
    private Date terminationDate;
    private Date closedDate;
    private String terminationReason;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
