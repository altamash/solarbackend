package com.solar.api.tenant.mapper.tiles.workorder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.customerSupport.CustomerConversationHeadDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderCustomerDetailTile {

    private Long accountId;
    private String uri;
    private String customerType;
    private String email;
    private String phoneNumber;
    private String name;
    private Long entityId;
    private Long workOrderCount;
    private List<CustomerConversationHeadDTO> customerConversationHeadDTOList;

    public WorkOrderCustomerDetailTile(Long accountId, String uri, String customerType, String email, String phoneNumber, String name, Long entityId) {
        this.accountId = accountId;
        this.uri = uri;
        this.customerType = customerType;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.entityId = entityId;
    }
    public WorkOrderCustomerDetailTile(Long entityId, String name,  String email, String phoneNumber,String customerType, String uri) {
        this.entityId = entityId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.customerType = customerType;
        this.uri = uri;
    }
}
