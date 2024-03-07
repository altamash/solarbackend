package com.solar.api.tenant.mapper.billing.paymentManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentManagementGraphDTO {

    private Integer year;
    private String month;
    private String label;
    private List<Integer> ListCount;
    private Long count;

    public PaymentManagementGraphDTO(String label, Long count) {
        this.label = label;
        this.count = count;
    }

}
