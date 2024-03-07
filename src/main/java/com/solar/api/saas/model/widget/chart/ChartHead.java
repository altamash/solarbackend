package com.solar.api.saas.model.widget.chart;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chart_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chartId;
    @Column(length = 5, unique = true)
    private String chartCode;
    private String chartName;
    private String chartType; // From portal attribute
    private String maxXLabels;
    private Integer maxXPoints;
    private Integer minLabelWidth;
    private Boolean showLegend;
    private Boolean enabled;
    private String orientation;
    private String javaMethod;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
