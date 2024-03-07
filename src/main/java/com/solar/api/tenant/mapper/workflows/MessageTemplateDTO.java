package com.solar.api.tenant.mapper.workflows;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageTemplateDTO {

    private Long id;
    private String msgTmplName;
    private String msgTemplRefId;
    private String messageType;
    private String parentTmplId; // TODO: Not decided
    private String format;
    private String templateHTMLCode;
    private String templateStructure;
    private String actionsCode;
    private String returnMessage;
    private String subject;
    private String header;
    private String footer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
