package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerPayment {

    @JsonProperty("invoice_ref_id")
    private Long invoiceRefId;
    @JsonProperty("payment_code")
    private String paymentCode;
    @JsonProperty("pay_det_id")
    private Long payDetId;
    @JsonProperty("amt")
    private Double amt;
    @JsonProperty("source")
    private String source;
    @JsonProperty("source_id")
    private String sourceId;
    @JsonProperty("tran_date_time")
    private String tranDateTime;
    @JsonProperty("instrument_num")
    private String instrumentNum;
    @JsonProperty("issuer_type")
    private String issuerType;
    @JsonProperty("issuer_ref_num")
    private String issuerRefNum;
    @JsonProperty("issuer_recon_status")
    private String issuerReconStatus;
    @JsonProperty("issuer_recon_date")
    private String issuerReconDate;
}



