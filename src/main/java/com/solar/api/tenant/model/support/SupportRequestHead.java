package com.solar.api.tenant.model.support;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "support_request_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subscriptionId;
    private Long accountId;
    private String firstName;
    private String lastName;
    private String role;
    private String requestAction;
    @Column(name = "status", columnDefinition = "varchar(100) default 'NEW'")
    private String status;
    private String description;
    private String raisedBy;
    private String priority;
    @OneToMany(mappedBy = "supportRequestHead", cascade = CascadeType.MERGE)
    private List<SupportRequestHistory> supportRequestHistories;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
