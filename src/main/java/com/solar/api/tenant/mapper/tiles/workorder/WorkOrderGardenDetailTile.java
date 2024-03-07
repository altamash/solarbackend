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
public class WorkOrderGardenDetailTile {

    private Long accountId;
    private String uri;
    private String refId;
    private String refType;
    private String productName;
    private String subsStatus;
    private Long workOrderCount;
    private List<CustomerConversationHeadDTO> customerConversationHeadDTOList;

    public WorkOrderGardenDetailTile(Long accountId,  String refId, String refType, String productName, String subsStatus, String uri) {
        this.accountId = accountId;
        this.refId = refId;
        this.refType = refType;
        this.productName = productName;
        this.subsStatus = subsStatus;
        this.uri = uri;
    }
}
