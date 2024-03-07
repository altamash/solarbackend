package com.solar.api.tenant.mapper.EventLog;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedEventLog {

    long totalItems;
    List<com.solar.api.tenant.model.eventLog.EventLogDTO> jobs;
}
