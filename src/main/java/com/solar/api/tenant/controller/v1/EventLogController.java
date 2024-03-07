package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.EventLog.PagedEventLog;
import com.solar.api.tenant.service.EventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EventLogController")
@RequestMapping(value = "/eventLog")
public class EventLogController {

    @Autowired
    private EventLogService eventLogService;

    @GetMapping("/{page}/{pagesize}/{sort}")
    public PagedEventLog findAll(@PathVariable("page") int pageNumber,
                                 @PathVariable("pagesize") Integer pageSize,
                                 @PathVariable("sort") String sort) {
        return eventLogService.findAll(pageNumber, pageSize, sort);
    }
}
