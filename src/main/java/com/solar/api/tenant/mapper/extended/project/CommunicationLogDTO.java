package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommunicationLogDTO {

    private Long id;
    private Long docId;
    private Long levelId;
    private String level;
    private String severity;
    private String type;
    private String approvalRequired;
    private String approver;
    private String approvalDate;
    private String requester;
    private String message;
    private String recSeqNo;
}
