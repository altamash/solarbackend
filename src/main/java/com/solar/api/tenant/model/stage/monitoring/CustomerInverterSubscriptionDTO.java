package com.solar.api.tenant.model.stage.monitoring;
import lombok.*;
import java.io.Serializable;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInverterSubscriptionDTO implements Serializable {
    private String subscriptionId;
    private String subscriptionName;
}