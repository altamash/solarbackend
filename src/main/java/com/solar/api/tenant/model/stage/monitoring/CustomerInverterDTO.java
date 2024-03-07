package com.solar.api.tenant.model.stage.monitoring;
import lombok.*;
import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInverterDTO implements Serializable {
    private String variantId;
    private String variantName;
    private List<CustomerInverterSubscriptionDTO> subscriptions;
}