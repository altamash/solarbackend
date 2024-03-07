package com.solar.api.tenant.model.support;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "support_request_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "support_request_head_id")
    private SupportRequestHead supportRequestHead;
    @Transient
    private Long srId;
    @Column(length = 1001)
    private String message;
    private String firstName;
    private String lastName;
    private String role;
    private String requestAction;
    private Long responderUserId;
    private Integer nowWaitingOn;
    private Date responseDateTime;
    private String sequenceNo;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
