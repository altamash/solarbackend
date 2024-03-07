package com.solar.api.tenant.mapper.billingInvoicing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.billingInvoicing.VariantInvoiceLog;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceLogArchiveDTO {

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
    private String message; // code of html generated
    private VariantInvoiceLog variantInvoiceLog;
}
