package com.solar.api.tenant.model.eventLog;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventLogDTO {

    private Long id;
    private Date dateTime;
    private String eventType;
    private String request;
    private String log;
    private String error;
    private String throwable;
}
