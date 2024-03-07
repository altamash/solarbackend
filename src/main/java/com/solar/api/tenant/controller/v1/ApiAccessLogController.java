package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.repository.ApiAccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ApiAccessLogController")
@RequestMapping(value = "/apiAccessLog")
public class ApiAccessLogController {

    @Autowired
    private ApiAccessLogRepository repository;

    @GetMapping
    public void findAll() {
        repository.findAll();
    }

    @DeleteMapping
    public void deleteAll() {
        repository.deleteAll();
    }

}
