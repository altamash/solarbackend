package com.solar.api.tenant.model.workflow;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_hook_map")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowHookMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long hookId;
//    private Long workflowHeadId;
//    private Long workflowHookMasterId;
//    private Long WorkflowRecipientId;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "wf_id", referencedColumnName = "id")
    private WorkflowHead workflowHead;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "wf_list_id", referencedColumnName = "id")
    private WorkflowRecipientList workflowRecipientList; // (bcc if dynamic reciepent is true)
    @Column(length = 15)
    private String listTarget;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "email_template_id", referencedColumnName = "id")
    private MessageTemplate emailTemplate; // (for all recievers)
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "notif_template_id", referencedColumnName = "id")
    private MessageTemplate notifTemplate;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "requester_email_template_id", referencedColumnName = "id")
    private MessageTemplate requesterEmailTemplate;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "requester_notif_template_id", referencedColumnName = "id")
    private MessageTemplate requesterNotifTemplate;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
