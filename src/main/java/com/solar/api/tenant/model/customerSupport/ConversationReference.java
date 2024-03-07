package com.solar.api.tenant.model.customerSupport;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "conversation_reference")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "conv_head_id")
    private ConversationHead conversationHead;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "conv_history_id", nullable = true)
    private ConversationHistory conversationHistory;
    @Transient
    private String uri;
    private Long referenceId;
    private String referenceType;
}
