package com.solar.api.tenant.mapper.payment.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentModeDTO {

    private Long id;
    private String paymentMode;
    private String reconcileIndicator;
    private String reversalIndicator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentModeDTO(Long id, String paymentMode, String reconcileIndicator, String reversalIndicator, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.paymentMode = paymentMode;
        this.reconcileIndicator = reconcileIndicator;
        this.reversalIndicator = reversalIndicator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public PaymentModeDTO(Long id, String paymentMode, String reconcileIndicator, String reversalIndicator) {
        this.id = id;
        this.paymentMode = paymentMode;
        this.reconcileIndicator = reconcileIndicator;
        this.reversalIndicator = reversalIndicator;
    }
}
