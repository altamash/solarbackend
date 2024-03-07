package com.solar.api.tenant.mapper.payment.billing;

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
public class PaymentTransactionDetailDTO {

    private Long payDetId;
    private Long paymentId;
    private Date tranDate;
    private Long lineSeqNo;
    private Double amt;
    private Double origAmt;
    private String status;
    private String source;
    private String sourceId;
    private String instrumentNum;
    private String issuer;
    private Long issuerId;
    private String issuerReconStatus;
    private Date reconExpectedDate;
    private Date reconDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
