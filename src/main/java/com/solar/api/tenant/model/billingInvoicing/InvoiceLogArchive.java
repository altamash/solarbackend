package com.solar.api.tenant.model.billingInvoicing;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_log_archive")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLogArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String invoiceStatus;
    private String sequenceNo;
    private String email;
    private String fileName;
    private String uniqueCode;
    private String passcode;
    private String fileIntegrityCheck;
    private String emailedIndicator;
    private LocalDateTime emailDateTime;
    private String emailStatus;
    private String emailContent;
    private LocalDateTime archivalDateTime;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
