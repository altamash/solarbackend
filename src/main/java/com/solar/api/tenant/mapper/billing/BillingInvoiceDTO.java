package com.solar.api.tenant.mapper.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingInvoiceDTO {

    private Long id;
    private String type; // MONTHLY (default), TRUEUP, SETTLEMENT, REBATE, OTHERS
    private String category; // Individual, Corporate
    private Date dateOfInvoice;
    private Date dueDate;
    private Boolean publishIndicator;
    private Date publishDate;
    private String publishUrl; // = publish_indicator ? seturl of pdf : null
    private Long billingHeadId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
