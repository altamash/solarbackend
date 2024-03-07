package com.solar.api.tenant.model.docuSign;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sign_req_tracker")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigningRequestTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sign_temp_id")
    private DocumentSigningTemplate documentSigningTemplate;
    @Column(length = 100)
    private String extTemplateId;
    @Column(length = 50)
    private String extRequestId; // Request Number from Zoho Sign once document is submitted.
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "entity_id", referencedColumnName = "id")
    private com.solar.api.tenant.model.contract.Entity entity;
    private Date requestDateTime;
    private String requestMessage;
    @Column(length = 10)
    private String status;
    private Date expiryDate;
    private int attemptCount; // number on resend
    @OneToMany(mappedBy = "refId", cascade = CascadeType.MERGE)
    private List<ExternalCallBackLog> externalCallBackLogs;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
