package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.tenant.mapper.EventLog.EventLogMapper;
import com.solar.api.tenant.mapper.EventLog.PagedEventLog;
import com.solar.api.tenant.model.EventLog;
import com.solar.api.tenant.repository.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional("tenantTransactionManager")
public class EventLogServiceImpl implements EventLogService {

    @Autowired
    EventLogRepository eventLogRepository;

    @Override
    public EventLog addOrUpdate(EventLog eventLog) {
        if (eventLog.getId() != null) {
            EventLog eventLogData = eventLogRepository.getOne(eventLog.getId());
            if (eventLogData == null) {
                throw new NotFoundException(EventLog.class, eventLog.getId());
            }
            eventLogData = EventLogMapper.toUpdatedEventLog(eventLogData, eventLog);
            return eventLogRepository.save(eventLogData);
        }
        return eventLogRepository.save(eventLog);
    }

    @Override
    public PagedEventLog findAll(int pageNumber, Integer pageSize, String sort) {
        Sort sortBy;
        if ("-1".equals(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "-1".equals(sort) ? "createdAt" : sort);
        } else {
            List<String> sortColumns = Arrays.stream(sort.split(",")).collect(Collectors.toList());
            sortBy = Sort.by(sortColumns.get(0));
            for (int i = 1; i < sortColumns.size(); i++) {
                sortBy = sortBy.and(Sort.by(sortColumns.get(i)));
            }
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize == null ? SaasSchema.PAGE_SIZE : pageSize, sortBy);
        Page<EventLog> eventLogs = eventLogRepository.findAll(pageable);
        return PagedEventLog.builder()
                .totalItems(eventLogs.getTotalElements())
                .jobs(EventLogMapper.toEventLogDTOs(eventLogs.getContent()))
                .build();
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void deleteAll() {

    }
}
