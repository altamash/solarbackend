package com.solar.api.tenant.model.support;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_status_workflow")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportStatusWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ssw_id;
    private String statusNow;
    private String possibleStatuses;
    private String target;
    private String role;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
