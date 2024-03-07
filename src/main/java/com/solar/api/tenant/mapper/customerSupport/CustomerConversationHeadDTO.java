package com.solar.api.tenant.mapper.customerSupport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.controlPanel.ControlPanelStaticDataDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerConversationHeadDTO {
    private Long conversationHeadId;
    private String sourceType; // Service desk, Project
    private String status;
    private String workOrderId;
    private String organizationName;

}
