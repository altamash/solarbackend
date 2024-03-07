package com.solar.api.tenant.model.billingInvoicing;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "variant_invoice_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantInvoiceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    private LocalDateTime submitDateTime;
    private LocalDateTime executionDateTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String jobType;
    private Long initiator;
    private Date billingMonth;
    private Date invoicingMonth;
    private Date dueDate;
    @OneToMany(mappedBy = "variantInvoiceLog", cascade = CascadeType.MERGE)
    private List<InvoiceLog> invoiceLogList;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
