package com.solar.api.tenant.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_authority_delegation_archive")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalAuthorityDelegationArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long approvalAuthorityDelegationId;
    private Long primaryUserId;
    private Long delegateUserId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private String endBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
