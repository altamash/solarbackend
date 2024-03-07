package com.solar.api.tenant.service;

import com.solar.api.tenant.mapper.EventLog.PagedEventLog;
import com.solar.api.tenant.model.EventLog;

public interface EventLogService {

    EventLog addOrUpdate(EventLog EventLog);

    PagedEventLog findAll(int pageNumber, Integer pageSize, String sort);

    void delete(Long id);

    void deleteAll();

}
