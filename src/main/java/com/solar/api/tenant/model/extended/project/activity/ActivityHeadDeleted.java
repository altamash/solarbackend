package com.solar.api.tenant.model.extended.project.activity;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_head_deleted")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHeadDeleted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long activityId;
    private Long projectId;
    private String comments;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
