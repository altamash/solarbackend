package com.solar.api.tenant.model.extended.project;

import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "phases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long registerHierarchyId;
    private Long parentPhaseId;
    //Data Type is String due to initial zeros in the data
    private String externalReferenceId;
    private String phaseName;
    private String level;
    @ManyToOne
    @JoinColumn(name = "project_head_id")
    private ProjectHead projectHead;

    @OneToMany(mappedBy = "phase", cascade = CascadeType.MERGE)
    private Set<ActivityHead> activityHeads;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
