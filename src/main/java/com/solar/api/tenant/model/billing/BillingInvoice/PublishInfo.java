package com.solar.api.tenant.model.billing.BillingInvoice;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "publish_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long referenceId;
    private String type; // INVOICE, NOTIFICATION
    private String channel; // SMS, Email
    private String channelRecipient;
    private String status; // NEW, FAILED, SUCCESS
    private Integer statusNum;
    private Integer attemptCount;
    private Date dateSent;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
