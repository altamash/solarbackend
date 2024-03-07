package com.solar.api.tenant.model.extended.resources;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "hr_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HRDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Long hrHeadId; //PTE,FTE,OTHERS
    //private String measure; // columns
    private String value; //columns default value

    @ManyToOne
    @JoinColumn(name = "hr_head_id", referencedColumnName = "id")
    private HRHead hrHead;

    @Transient
    private MeasureDefinitionTenantDTO measureDefinitionTenant;
    @Transient
    private String measure;
    private Long measureCodeId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String filterByInd;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;

}
