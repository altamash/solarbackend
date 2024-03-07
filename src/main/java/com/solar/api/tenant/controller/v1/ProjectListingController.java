package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.extended.FunctionalRolesDTO;
import com.solar.api.tenant.service.RoleService;
import com.solar.api.tenant.service.acquisition.AcquisitionService;
import com.solar.api.tenant.service.extended.FunctionalRolesService;
import com.solar.api.tenant.service.projectListing.ProjectListingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.FunctionalRolesMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ProjectListingController")
@RequestMapping(value = "/projectListing")
public class ProjectListingController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectListingService projectListingService;
    @Autowired
    RoleService roleService;
    @Autowired
    Utility utility;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/getAllManagers")
    public ResponseEntity<?> getAllManagers() {
        return projectListingService.getAllManagers();
    }


}
