package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.service.StavemRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("StavemController")
@RequestMapping(value = "/stavem")
public class StavemController {

    @Autowired
    private StavemRolesService stavemRolesService;

    @GetMapping("/dumpEngagementRoles")
    public ObjectNode dumpEngagementRoles() {
        return stavemRolesService.dumpEngagementRoles();
    }

    @GetMapping("/dumpProjectData")
    public ObjectNode dumpProjectData() {
        return stavemRolesService.dumpProjectData();
    }

    @GetMapping("/dumpAttendanceLogs")
    public ObjectNode dumpAttendanceLogs() {
        return stavemRolesService.dumpAttendanceLogs();
    }

    @GetMapping("/dumpPhases")
    public ObjectNode dumpPhases() {
        return stavemRolesService.dumpPhases();
    }
}