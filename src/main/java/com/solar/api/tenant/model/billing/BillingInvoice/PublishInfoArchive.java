package com.solar.api.tenant.model.billing.BillingInvoice;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "publish_info_archive")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishInfoArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long referenceId;
    private String type; // INVOICE, NOTIFICATION
    private String channel; // SMS, Email
    private String channelRecipient;
    private String status; // NEW, FAILED, SUCCESS
    private Integer attemptCount;
    private Date dateSent;
}
