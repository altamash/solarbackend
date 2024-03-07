package com.solar.api.tenant.mapper.tiles.customermanagement;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.model.user.UserTemplate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerManagementTileMapper {
    private static SimpleDateFormat formatter = new SimpleDateFormat(Utility.FULL_MONTH_DATE_YEAR_FORMAT);

    public static CustomerManagementTile toCustomerManagementTile(UserTemplate userTemplate) {
        if (userTemplate == null) {
            return null;
        }
        return CustomerManagementTile.builder()
                .accountId(userTemplate.getAccountId())
                .entityId(userTemplate.getEntityId())
                .customerType(userTemplate.getCustomerType())
                .emailAddress(userTemplate.getEmailAddress())
                .firstName(userTemplate.getFirstName())
                .lastName(userTemplate.getLastName())
                .status(userTemplate.getStatus())
                .stateCustomer(userTemplate.getStateCustomer())
                .zipCode(userTemplate.getZipCode())
                .profileUrl(userTemplate.getProfileUrl())
                .region(userTemplate.getRegion())
                .phone(userTemplate.getPhone())
                .signedDocument(userTemplate.getSignedDocument())
                .registeredDate(userTemplate.getRegisteredDate() != null ? userTemplate.getRegisteredDate().toString() : "")
                .createdAtString(userTemplate.getCreatedAt())
                .subscriptionTotal(userTemplate.getSubscriptionTotal() != null ? userTemplate.getSubscriptionTotal() : "0")
                .isChecked(userTemplate.getIsChecked())
                .selfInitiative((userTemplate.getSelfInitiative() ))
                .mongoProjectId(userTemplate.getMongoProjectId())
                .hasPassword(userTemplate.getHasPassword() != null)
                .hasLogin(userTemplate.getHasLogin())
                .mobileAllowed(userTemplate.getMobileAllowed())
                .utilityInfoCount(userTemplate.getUtilityInfoCount())
                .supportTicketCount(userTemplate.getSupportTicketCount())
                .paymentInfoCount(userTemplate.getPaymentInfoCount())
                .contractCount(userTemplate.getContractCount())
                .addressCount(userTemplate.getAddressCount())
                .generatedAt(userTemplate.getGeneratedAt() != null ? formatter.format(userTemplate.getGeneratedAt()) : null)
                .agentName(userTemplate.getAgentName() != null ? userTemplate.getAgentName() : "Unassigned")
                .agentImage(userTemplate.getAgentImage())
                .groupBy(userTemplate.getGroupBy())
                .isLeaf((userTemplate.getIsLeaf() != null && userTemplate.getIsLeaf() == 1))
                .build();
    }
    public static CustomerManagementTile toCustomerManagementTileGroupBy(UserTemplate userTemplate) {
        if (userTemplate == null) {
            return null;
        }
        return CustomerManagementTile.builder()
                .groupBy(userTemplate.getGroupBy())
                .isLeaf((userTemplate.getIsLeaf() != null && userTemplate.getIsLeaf() == 1))
                .build();
    }
    public static List<CustomerManagementTile> toCustomerManagementTiles(List<UserTemplate> userTemplates) {
        return userTemplates.stream().map(ut -> toCustomerManagementTile(ut)).collect(Collectors.toList());
    }
    public static List<CustomerManagementTile> toCustomerManagementTilesGroupBy(List<UserTemplate> userTemplates) {
        return userTemplates.stream().map(ut -> toCustomerManagementTileGroupBy(ut)).collect(Collectors.toList());
    }
}
