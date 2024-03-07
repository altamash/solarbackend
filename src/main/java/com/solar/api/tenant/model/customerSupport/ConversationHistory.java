package com.solar.api.tenant.model.customerSupport;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversation_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long parentId;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "conv_head_id")
    private ConversationHead conversationHead;
    @Column(length = 1001)
    private String message;
    private Long responderUserId;    //  Responder
    private String firstName;
    private String lastName;
    private String role;
    private Boolean internal;   // To mark if the conversation is internal
    private Boolean entryType;
    private Boolean approvedIndicator;
    private String priority;
    private Long referenceHistoryId;
    private Long scheduleId;
    @OneToMany(mappedBy = "conversationHistory", cascade = CascadeType.MERGE)
    private List<ConversationReference> conversationReferenceList;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
