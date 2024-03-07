package com.solar.api.tenant.model.report;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_iterator_definition")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportIteratorDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String iteratorName;
    private String filterCodes;
    private String templateName;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
