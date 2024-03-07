package com.solar.api.tenant.model.extended.project.activity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Transient
    private Long activityId;
    private Long measureId;
    private String value;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "activity_id", referencedColumnName = "id")
    private ActivityHead activityHead;

    @UpdateTimestamp
    private LocalDateTime lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;
}
