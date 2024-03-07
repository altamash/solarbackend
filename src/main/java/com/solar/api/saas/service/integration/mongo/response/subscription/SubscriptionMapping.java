package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.tiles.workorder.SubscriptionInformation;
import com.solar.api.tenant.mapper.tiles.workorder.SubscriptionInformationTile;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionMapping {

    private Variant variant;
    private Subscription subscription;

    public static CustomerSubscriptionDTO toCustomerSubscriptionDTO(SubscriptionDetailTemplate subscriptionDetailTemplate) {

        return new CustomerSubscriptionDTO(
                subscriptionDetailTemplate.getPremiseNo(),
                subscriptionDetailTemplate.getVariantName(),
                subscriptionDetailTemplate.getUserAcctId(),
                subscriptionDetailTemplate.getActive(),
                subscriptionDetailTemplate.getSubId(),
                subscriptionDetailTemplate.getStatus(),
                subscriptionDetailTemplate.getVariantId(),
                subscriptionDetailTemplate.getSubName(),
                subscriptionDetailTemplate.getSiteLocationId(),
                subscriptionDetailTemplate.getCustomerAddress(),
                subscriptionDetailTemplate.getAdd1(),
                subscriptionDetailTemplate.getAdd2(),
                subscriptionDetailTemplate.getExt1(),
                subscriptionDetailTemplate.getExt2(),
                subscriptionDetailTemplate.getZipCode()
        );

    }

    public static List<CustomerSubscriptionDTO> toCustomerSubscriptionDTOs(List<SubscriptionDetailTemplate> subscriptionDetailTemplateList) {
        return subscriptionDetailTemplateList.stream().map(SubscriptionMapping::toCustomerSubscriptionDTO).collect(Collectors.toList());
    }

    public static SubscriptionInformationTile convertToSubscriptionInformationTile(SubscriptionInformation subscriptionInformation) {
        return new SubscriptionInformationTile(
                subscriptionInformation.getEntityName(),
                subscriptionInformation.getRefType(),
                subscriptionInformation.getSubsStatus(),
                subscriptionInformation.getUri(),
                subscriptionInformation.getSubscriptionId(),
                subscriptionInformation.getInverterNumber(),
                subscriptionInformation.getPaymentSource()
        );
    }
}
