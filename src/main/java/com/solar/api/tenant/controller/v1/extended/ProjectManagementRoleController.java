package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.FunctionalRolesDTO;
import com.solar.api.tenant.service.extended.FunctionalRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.FunctionalRolesMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ProjectManagementRoleController")
@RequestMapping(value = "/projectManagementRole")
public class ProjectManagementRoleController {

    @Autowired
    private FunctionalRolesService functionalRolesService;

    @PostMapping("/functionalRoles/addOrUpdate")
    public FunctionalRolesDTO addOrUpdate(@RequestBody FunctionalRolesDTO functionalRolesDTO) {
        return toFunctionalRolesDTO(functionalRolesService.saveOrUpdate(toFunctionalRoles(functionalRolesDTO)));
    }

    @GetMapping("/functionalRoles/findById/{id}")
    public FunctionalRolesDTO findById(@PathVariable Long id) {
        return toFunctionalRolesDTO(functionalRolesService.findFunctionalRolesById(id));
    }

    @GetMapping("/functionalRoles/findAllFunctionalRoles")
    public List<FunctionalRolesDTO> findAllFunctionalRoles() {
        return toFunctionalRolesDTOs(functionalRolesService.findAllFunctionalRoles());
    }

}
