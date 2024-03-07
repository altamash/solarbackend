package com.solar.api.tenant.model.workflow;

import com.solar.api.tenant.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflow_group_assignment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowGroupAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "wf_group_id", referencedColumnName = "id")
    private WorkflowGroup workflowGroup;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "workflow_group_assignment_user",
            joinColumns = @JoinColumn(name = "group_assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
//    @OneToMany(mappedBy = "workflowGroupAssignment", cascade = CascadeType.MERGE)
    private List<User> assignees;

    private String module;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
