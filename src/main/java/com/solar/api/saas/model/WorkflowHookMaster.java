package com.solar.api.saas.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wf_hook_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowHookMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hookConstant;
    private String hookName;
    private String module; // From view
    private String wfFunction;
    private String wfService;
    @Column(name = "type_a_enabled")
    private Boolean typeAEnabled; // From view as approvals
    @Column(name = "type_b_enabled")
    private Boolean typeBEnabled; // From view as notifications
    private Boolean dynamicRecipient; // if true list in workflow_hook_map is optional

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
