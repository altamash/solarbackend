package com.solar.api.saas.model.widget.chart;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chart_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labelId;
    private Long chartId;
    private Integer seqNo;
    private String labelName;
    private String borderColor;
    private String baseColor;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
