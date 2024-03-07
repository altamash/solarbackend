package com.solar.api.tenant.model.docuSign;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "external_call_back_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalCallBackLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 15)
    private String refCode;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ref_id")
    private SigningRequestTracker refId;
    @Column(length = 50)
    private String extRequestId; // id for our request from Zoho
    @Column(length = 50)
    private String callBackId; // id send by Zoho (if available)
    private Date dateTime;
    @Column(length = 10)
    private String status; // NEW > when received. APPLIED > when document is received asa PDF and saved in Docu Library

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
