package com.solar.api.tenant.mapper.EventLog;

import com.solar.api.tenant.model.EventLog;

import java.util.List;
import java.util.stream.Collectors;

public class EventLogMapper {

    public static EventLog toEventLog(com.solar.api.tenant.model.eventLog.EventLogDTO eventLogDTO) {
        if (eventLogDTO == null) {
            return null;
        }
        return EventLog.builder()
                .id(eventLogDTO.getId())
                .dateTime(eventLogDTO.getDateTime())
                .eventType(eventLogDTO.getEventType())
                .request(eventLogDTO.getRequest())
                .error(eventLogDTO.getError())
                .log(eventLogDTO.getLog())
                .throwable(eventLogDTO.getThrowable())
                .build();
    }

    public static com.solar.api.tenant.model.eventLog.EventLogDTO toEventLogDTO(EventLog eventLog) {
        if (eventLog == null) {
            return null;
        }
        return com.solar.api.tenant.model.eventLog.EventLogDTO.builder()
                .id(eventLog.getId())
                .dateTime(eventLog.getDateTime())
                .eventType(eventLog.getEventType())
                .request(eventLog.getRequest())
                .error(eventLog.getError())
                .log(eventLog.getLog())
                .throwable(eventLog.getThrowable())
                .build();
    }

    public static EventLog toUpdatedEventLog(EventLog eventLog, EventLog eventLogUpdate) {
        eventLog.setDateTime(eventLogUpdate.getDateTime() == null ? eventLog.getDateTime() :
                eventLogUpdate.getDateTime());
        eventLog.setEventType(eventLogUpdate.getEventType() == null ? eventLog.getEventType() :
                eventLogUpdate.getEventType());
        eventLog.setRequest(eventLogUpdate.getRequest() == null ? eventLog.getRequest() : eventLogUpdate.getRequest());
        eventLog.setLog(eventLogUpdate.getLog() == null ? eventLog.getLog() : eventLogUpdate.getLog());
        eventLog.setError(eventLogUpdate.getError() == null ? eventLog.getError() : eventLogUpdate.getError());
        eventLog.setThrowable(eventLogUpdate.getThrowable() == null ? eventLog.getThrowable() :
                eventLogUpdate.getThrowable());
        return eventLog;
    }

    public static List<EventLog> toEventLogs(List<com.solar.api.tenant.model.eventLog.EventLogDTO> eventLogDTOS) {
        return eventLogDTOS.stream().map(e -> toEventLog(e)).collect(Collectors.toList());
    }

    public static List<com.solar.api.tenant.model.eventLog.EventLogDTO> toEventLogDTOs(List<EventLog> eventLogs) {
        return eventLogs.stream().map(a -> toEventLogDTO(a)).collect(Collectors.toList());
    }
}
