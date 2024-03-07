package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.project.EngagementRoleDTO;
import com.solar.api.tenant.mapper.extended.project.EngagementRoleMapper;
import com.solar.api.tenant.service.extended.project.EngagementRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EngagementRoleController")
@RequestMapping(value = "/engagementRole")
public class EngagementRoleController {

    @Autowired
    private EngagementRoleService engagementRoleService;

    @GetMapping("/findAll")
    public List<EngagementRoleDTO> findAll() {
        return EngagementRoleMapper.toEngagementRoleDTOs(engagementRoleService.findAll());
    }

    @GetMapping("/findByExternalRoleId/{engagementRoleId}")
    public EngagementRoleDTO findByExternalRoleId(@PathVariable String externalRoleId) {
        return EngagementRoleMapper.toEngagementRoleDTO(engagementRoleService.findByExternalRoleId(externalRoleId));
    }
}
