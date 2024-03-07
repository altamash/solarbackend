package com.solar.api.tenant.model.customerSupport;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String summary;
    @Column(length = 1001)
    private String message;
    private String category;
    private String subCategory; //type
    private String priority;
    private Long parentRequestId;
    private Long grandParentRequestId;
    private String sourceType; // Service desk, Project
    private String sourceId; // It is not applicable on Service desk
    private Long requestId; // Auto - It should be unique by Source type
    private Long orgId;
    private String status;
    private Long raisedBy;  //  Logged in user id
    private String firstName;
    private String lastName;
    private String role;
    private Long assignee;  // Marked to
    private String channel;  // Web, Mobile
    private String contractId;
    private String subscriptionId;
    private Boolean internal;   // To mark if the conversation is internal
    private Long customerId;    //  It will not be visible on first time
    private Long workflowId;
    private String variantId;
    @OneToMany(mappedBy = "conversationHead", cascade = CascadeType.MERGE)
    private List<ConversationHistory> conversationHistoryList;
    @OneToMany(mappedBy = "conversationHead", cascade = CascadeType.MERGE)
    private List<ConversationReference> conversationReferenceList;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String remarks;
    private Double estimatedBudget;
    private Double estimatedHours;
    private String coverage;
    private String productId;
    private String module;
    private LocalDateTime plannedDate;

}
