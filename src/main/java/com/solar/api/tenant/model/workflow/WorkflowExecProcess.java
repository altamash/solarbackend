package com.solar.api.tenant.model.workflow;

import com.solar.api.tenant.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_exec_process")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowExecProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String refProcessType;
    private Long processId;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "workflow_id", referencedColumnName = "id")
    private WorkflowHead workflowHead;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "wf_list_id", referencedColumnName = "id")
    private WorkflowRecipientList workflowRecipientList;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user", referencedColumnName = "acctId")
    private User user;
    @Column(name = "wf_type")
    private String workflowType;
    @Column(length = 20)
    private String status;
    private String level;
    private LocalDateTime submitDate;
    private String action;
    private LocalDateTime actionDateTime;
    private Long actors;
    private String actionNote;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
