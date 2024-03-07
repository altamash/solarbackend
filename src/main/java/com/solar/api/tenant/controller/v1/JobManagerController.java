package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.process.job.PagedJobManagerTenantDTO;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("JobManagerController")
@RequestMapping(value = "/job")
public class JobManagerController {

    @Autowired
    private JobManagerTenantService jobManagerTenantService;

    @GetMapping("/{page}/{pagesize}/{sort}")
    public PagedJobManagerTenantDTO findAll(@PathVariable("page") int pageNumber,
                                            @PathVariable("pagesize") Integer pageSize,
                                            @PathVariable("sort") String sort) {
        return jobManagerTenantService.findAll(pageNumber, pageSize, sort);
    }
}
