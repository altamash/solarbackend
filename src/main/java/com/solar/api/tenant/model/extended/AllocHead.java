package com.solar.api.tenant.model.extended;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "alloc_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocId;
    private Long orderId;
    private Long assetId;
    private Long locationId;
    private Long qty;
    private String status;
    private String description;
    private Date dateTime;
    private Long submittedBy;
    private Long approverId;
    private Date approveDateTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
