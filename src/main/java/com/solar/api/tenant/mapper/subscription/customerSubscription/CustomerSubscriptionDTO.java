package com.solar.api.tenant.mapper.subscription.customerSubscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.service.integration.mongo.response.subscription.Group;
import com.solar.api.saas.service.integration.mongo.response.subscription.Measure;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscriptionMapping.CustomerSubscriptionMappingDTO;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHeadDTO;
import com.solar.api.tenant.mapper.user.address.AddressDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSubscriptionDTO {

    private Long id;
    private String subscriptionType;
    private String subscriptionTemplate;
    private Long subscriptionRateMatrixId;
    private Long userAccountId;
    private Date startDate;
    private Date endDate;
    private String subscriptionStatus;
    private String billStatus;
    private String arrayLocationRef;
    private AddressDTO address;
    private SubscriptionRateMatrixHeadDTO subscriptionRateMatrixHead;
    private List<CustomerSubscriptionMappingDTO> customerSubscriptionMappings;
    private Date terminationDate;
    private Date closedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String terminationReason;
    private String rollValue;
    private Boolean markedForDeletion;
    private Long contractId; // subscription parent contract

    // MongoDB
    private String subscriptionId;
    private Group productGroup;
    private Group variantGroup;
    private Measure measures;
//    List<DataBlock> dataBlocks;
    private String active;
    private String zipCode;
    private PhysicalLocationDTO physicalLocationDTO;
    private String premiseNo;
    private String variantName;
    private String variantId;
    private  String subscriptionName;
    private Long siteLocationId;
    private Long customerAddress;
    private String subActiveSince;

        public CustomerSubscriptionDTO(String premiseNo, String variantName, Long userAcctId, String subActiveSince, String subscriptionId, String subscriptionStatus, String variantId, String subscriptionName, Long siteLocationId, Long customerAddress, String add1, String add2,  String ext1, String ext2, String zipCode) {
      this.premiseNo=premiseNo;
      this.variantName=variantName;
      this.userAccountId=userAcctId;
      this.subActiveSince=subActiveSince;
      this.subscriptionId=subscriptionId;
      this.subscriptionStatus=subscriptionStatus;
      this.variantId=variantId;
      this.subscriptionName=subscriptionName;
      this.siteLocationId=siteLocationId;
      this.customerAddress=customerAddress;
      this.physicalLocationDTO = new PhysicalLocationDTO(id,add1,add2, zipCode, ext1,ext2);
    }


}
