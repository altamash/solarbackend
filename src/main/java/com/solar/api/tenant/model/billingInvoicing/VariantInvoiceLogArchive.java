package com.solar.api.tenant.model.billingInvoicing;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "variant_invoice_log_archive")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantInvoiceLogArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    private LocalDateTime submitDateTime;
    private LocalDateTime executionDateTime;
    private String status;
    private String jobType;
    private Long initiator;
    private Date billingMonth;
    private Date invoicingMonth;
    private Date dueDate;
    private LocalDateTime archivalDateTime;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
