package com.solar.api.tenant.mapper.tiles.customermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerManagementTile {
    private Boolean isLeaf;
    private Long entityId;
    private Long accountId;
    private String firstName;
    private String lastName;
    private String customerType;
    private String status;
    private String subscriptionTotal;
    private String emailAddress;
    private String phone;
    private String generatedAt;
    private String registeredDate;
    private String stateCustomer;
    private String zipCode;
    private String region;
    private String profileUrl;
    private String signedDocument;
    private String isChecked;
    private String agentName;
    private String agentImage;
    private String mongoProjectId;
    private Long correspondenceCount;
    private Boolean selfInitiative;
    private Boolean hasPassword;
    private Boolean hasLogin;
    private Boolean mobileAllowed;
    private Long utilityInfoCount;
    private Long supportTicketCount;
    private Long paymentInfoCount;
    private Long contractCount;
    private Long addressCount;
    private String groupBy;
    private String createdAtString;
}
