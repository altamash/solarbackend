package com.solar.api.tenant.mapper.tiles.workorder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionInformationTile {

    private String entityName;
    private String refType;
    private String subsStatus;
    private String uri;
    private String subscriptionId;
    private String inverterNumber;
    private String paymentSource;

    public SubscriptionInformationTile(String entityName, String refType, String subsStatus, String uri, String subscriptionId, String inverterNumber, String paymentSource) {
        this.entityName = entityName;
        this.refType = refType;
        this.subsStatus = subsStatus;
        this.uri = uri;
        this.subscriptionId = subscriptionId;
        this.inverterNumber = inverterNumber;
        this.paymentSource = paymentSource;
    }
}
