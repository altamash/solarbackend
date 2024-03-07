package com.solar.api.tenant.model.process;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "job_manager_tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobManagerTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long batchId;
    private String jobName;
    private String jobComponent; // unused
    private Long componentId; // unused
    private Date requestDatetime;
    private Date executionDatetime;
    private Date endDatetime;
    private Long duration;
    private String status;
    private Boolean errors;
    @Column(length = 1000)
    private String requestMessage;
    @Column(length = 1000)
    private String responseMessage;
    @Lob
    private byte[] interfaceMessage;
    @Lob
    private byte[] log;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
