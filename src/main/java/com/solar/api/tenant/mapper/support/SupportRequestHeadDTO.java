package com.solar.api.tenant.mapper.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportRequestHeadDTO {

    private Long id;
    private Long subscriptionId;
    private Long accountId;
    private String firstName;
    private String lastName;
    private String role;
    private String requestAction;
    private String status;
    private String description;
    private String raisedBy;
    private String priority;
    private List<SupportRequestHistoryDTO> supportRequestHistoryList;
    private LocalDateTime createdAt;
}
