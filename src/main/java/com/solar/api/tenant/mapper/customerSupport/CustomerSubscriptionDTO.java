package com.solar.api.tenant.mapper.customerSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSubscriptionDTO {
    private Long acctId;
    private Long entityId;
    private String customerName;
    private String subsId;
    private String variantId;
    private String subscriptionName;
    private String devNo;

    private String invertedBrand; //variant name

    private String monitoringBrand; //mp

    private String image;

    private String systemSize;

    private String siteLocation;

    private Long plocId;

    private  String productId;
    private  String subsStatus;

    private String customerType;

    private String gardenImageUri;

    private  String gardenName;

    public CustomerSubscriptionDTO(Long acctId, Long entityId, String customerName, String variantId) {
        this.acctId = acctId;
        this.entityId = entityId;
        this.customerName = customerName;
        this.variantId = variantId;
    }

    public CustomerSubscriptionDTO(Long acctId, Long entityId, String productId, String variantId, String customerName, String subsId, String monitoringBrand, String invertedBrand, String image, String subscriptionName, Long plocId, String customerType, String gardenImageUri) {
        this.acctId = acctId;
        this.entityId = entityId;
        this.customerName = customerName;
        this.subsId = subsId;
        this.monitoringBrand = monitoringBrand;
        this.invertedBrand = invertedBrand;
        this.image = image;
        this.subscriptionName=subscriptionName;
        this.plocId = plocId;
        this.productId = productId;
        this.variantId = variantId;
        this.customerType = customerType;
        this.gardenImageUri = gardenImageUri;
        this.gardenName = invertedBrand;
    }

    public CustomerSubscriptionDTO(Long acctId, Long entityId, String customerName, String subsId, String variantId, String subscriptionName, String devNo, String invertedBrand, String monitoringBrand, String image, String systemSize, String siteLocation, Long plocId, String productId) {
        this.acctId = acctId;
        this.entityId = entityId;
        this.customerName = customerName;
        this.subsId = subsId;
        this.variantId = variantId;
        this.subscriptionName = subscriptionName;
        this.devNo = devNo;
        this.invertedBrand = invertedBrand;
        this.monitoringBrand = monitoringBrand;
        this.image = image;
        this.systemSize = systemSize;
        this.siteLocation = siteLocation;
        this.plocId = plocId;
        this.productId = productId;
    }

}
