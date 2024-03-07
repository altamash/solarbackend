package com.solar.api.tenant.model.extended.project;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "project_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;
    private Long measureCodeId;
    private String filterByInd;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;
    private String category;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectHead projectHead;
    @Transient
    private String measure;
    @Transient
    private MeasureDefinitionTenantDTO measureDefinitionTenant;
    @Transient
    private Long projectId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
