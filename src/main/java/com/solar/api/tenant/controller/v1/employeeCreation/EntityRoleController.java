package com.solar.api.tenant.controller.v1.employeeCreation;

import com.solar.api.tenant.mapper.EntityRoleResponseDTO;
import com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityRoleDTO;
import com.solar.api.tenant.service.userGroup.EntityRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.solar.api.tenant.mapper.user.userGroup.EntityRoleMapper.*;

/**
 * Date : 30th Nov, 2022
 *
 * @Shariq This controller is only to fetch resources with their roles
 * @status default value is true
 */

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EntityRoleController")
@RequestMapping(value = "/entityRole")
public class EntityRoleController {

    @Autowired
    EntityRoleService entityRoleService;

    @PostMapping
    public EntityRoleDTO addOrUpdate(@RequestBody EntityRoleDTO entityRoleDTO) {
        return toEntityRoleDTO(entityRoleService.addOrUpdate(toEntityRole(entityRoleDTO)));
    }

    @GetMapping("/{id}")
    public EntityRoleDTO findById(@PathVariable Long id) {
        return toEntityRoleDTO(entityRoleService.findById(id));
    }

    /**
     * ifExist = projectId/WorkOrderId/TaskId : can be null
     * <p>
     * Checking if the respective entity has already been added to resources
     *
     * @param status
     * @param ifExist  = null means all
     * @return
     */
    @GetMapping("/status/{status}/{ifExist}")
    public List<DefaultUserGroupResponseDTO> findByStatus(@PathVariable boolean status, @PathVariable String ifExist) {
        return entityRoleService.findByStatus(status, ifExist);
    }

    @GetMapping("/entity/{id}")
    public List<EntityRoleDTO> findByStatus(@PathVariable Long id) {
        return toEntityRoleDTOs(entityRoleService.findByEntity(id));
    }

    @GetMapping("/getAll")
    public List<EntityRoleResponseDTO> findAllEntityRoles() {
        return entityRoleService.getAllEntityRole();
    }

    @GetMapping("/getByFunctionalRole/{roleId}")
    public List<EntityFunctionalRoleTile> findByFunctionalRole(@PathVariable Long roleId) {
        return entityRoleService.findAllByFunctionRoleId(roleId);
    }

    @PostMapping("/add")
    public Map<String, String> addOrUpdateList(@RequestBody List<EntityRoleDTO> entityRoleDTOs) {
        return entityRoleService.addOrUpdate(toEntityRoles(entityRoleDTOs));
    }

    @DeleteMapping("/removeFunctionalRole/{entityId}/{roleId}")
    public Map<String, String> removeFunctionalRole(@PathVariable Long entityId, @PathVariable Long roleId) {
        return entityRoleService.removeEntityRole(entityId, roleId);
    }

    @GetMapping("/getByFunctionalRoleName/{roleName}")
    public List<EntityFunctionalRoleTile> findAllByFunctionRoleName(@PathVariable String roleName) {
        return entityRoleService.findAllByFunctionRoleName(roleName);
    }

}
