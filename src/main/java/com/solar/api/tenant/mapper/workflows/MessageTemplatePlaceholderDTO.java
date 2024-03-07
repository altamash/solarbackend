package com.solar.api.tenant.mapper.workflows;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.workflow.MessagePlaceholder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageTemplatePlaceholderDTO {

    private Long id;
    private Long msgTmpId;
    private MessagePlaceholder messagePlaceholder;
    private Long iterationNum;
    private String defaultMsgText; // TODO: Recheck type and/or size
    private Boolean optional;
}
