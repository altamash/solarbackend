package com.solar.api.tenant.model.extended.order;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "claim_file")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String claimType;
    private Long partnerId;
    private Long assetId;
    private Long locationId;
    private Long filer;
    private Date claimDateTime;
    private String status;
    private Double approvedAmt;
    private String approvalType;
    private Long approver;
    private Date approveDateTime;
    private String reasonDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
