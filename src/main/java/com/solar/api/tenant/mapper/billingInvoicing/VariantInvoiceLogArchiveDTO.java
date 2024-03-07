package com.solar.api.tenant.mapper.billingInvoicing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantInvoiceLogArchiveDTO {

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
    private List<InvoiceLog> invoiceLogList;
}
