package com.solar.api.tenant.model.extended.project;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pr_rate_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrRateGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private Long description;
    private Long prRateId;
    private String sequenceNumber;
    private String overtimeApplicableInd;
    private String category;
    private String referenceFunction;
    private String notes;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
