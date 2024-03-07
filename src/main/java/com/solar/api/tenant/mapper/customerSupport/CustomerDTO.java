package com.solar.api.tenant.mapper.customerSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {
    private Long acctId;
    private String customerName;
    private String customerType;
    private  String imageUrl;

    private Long entityId;
    private List<CustomerSubscriptionDTO> subscriptions;

    public CustomerDTO(String customerName, Long entityId) {
        this.customerName = customerName;
        this.entityId = entityId;
    }
}
