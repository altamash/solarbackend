package com.solar.api.tenant.controller.v1;

import com.solar.api.configuration.authorization.PermissionsUtil;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.mapper.permission.navigation.NavigationElementDTO;
import com.solar.api.saas.mapper.permission.navigation.NavigationElementMapper;
import com.solar.api.saas.mapper.permission.navigation.NavigationElementMapperMapper;
import com.solar.api.saas.mapper.permission.navigation.NavigationUserMapDTO;
import com.solar.api.saas.model.permission.component.ECompReference;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.process.permission.PermissionSetService;
import com.solar.api.tenant.mapper.permission.AvailablePermissionSetDTO;
import com.solar.api.tenant.mapper.permission.PermissionGroupDTO;
import com.solar.api.tenant.mapper.permission.PermissionGroupMapper;
import com.solar.api.tenant.mapper.user.role.RoleDTO;
import com.solar.api.tenant.mapper.user.role.RoleDetailDTO;
import com.solar.api.tenant.mapper.user.role.RoleMapper;
import com.solar.api.tenant.mapper.user.userType.UserTypeDTO;
import com.solar.api.tenant.mapper.user.userType.UserTypeMapper;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.RoleService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.UserTypeService;
import com.solar.api.tenant.service.process.permission.PermissionGroupService;
import com.solar.api.tenant.service.process.permission.UserPermissions;
import com.solar.api.tenant.service.process.permission.navigation.UserNavigation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.permission.PermissionGroupMapper.*;
import static com.solar.api.tenant.mapper.user.role.RoleMapper.*;

@CrossOrigin
@RestController("PermissionController")
@RequestMapping(value = "/permission")
public class PermissionController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionGroupService permissionGroupService;
    @Autowired
    private PermissionSetService permissionSetService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPermissions userPermissions;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private PermissionsUtil permissionsUtil;
    @Autowired
    private UserNavigation userNavigation;
    @Autowired
    private MasterTenantRepository masterTenantRepository;

    // AvailablePermissionSet /////////////////////////////////////////////////
    @PreAuthorize("checkAccess()")
    @GetMapping("/availablePermissionSet")
    public Set<AvailablePermissionSetDTO> getAvailablePermissionSets() {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(permissionGroupService.getAvailablePermissionSets());
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/availablePermissionSet/level")
    public Set<AvailablePermissionSetDTO> getAvailablePermissionSetsByUserLevels(@RequestParam(value = "userLevelCSV") String userLevelCSV) {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(permissionGroupService
                .getAvailablePermissionSetsByUserLevels(Arrays.asList(userLevelCSV).stream().map(level -> level.trim())
                        .collect(Collectors.toList())));
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/availablePermissionSet")
    public void addAvailablePermissionSet(Long permissionSetId) {
        permissionGroupService.addAvailablePermissionSet(permissionSetId);
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/availablePermissionSets")
    public void addAvailablePermissionSet(String permissionSetIdCSV) {
        permissionGroupService.addAvailablePermissionSet(permissionSetIdCSV);
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/availablePermissionSet")
    public void removeAvailablePermissionSet(Long permissionSetId) {
        permissionGroupService.removeAvailablePermissionSet(permissionSetId);
    }

    // PermissionGroup /////////////////////////////////////////////////
    @PreAuthorize("checkAccess()")
    @PostMapping("/group")
    public PermissionGroupDTO savePermissionGroup(@RequestBody PermissionGroupDTO permissionGroup) {
        PermissionGroupDTO permissionGroupDTO = toPermissionGroupDTO(permissionGroupService.saveOrUpdate(toPermissionGroup(permissionGroup)));
        permissionsUtil.setNavigationUserMap();
        return permissionGroupDTO;
    }

    @PreAuthorize("checkAccess()")
    @PutMapping("/group")
    public PermissionGroupDTO updatePermissionGroup(@RequestBody PermissionGroupDTO permissionGroup) {
        PermissionGroupDTO permissionGroupDTO = toPermissionGroupDTO(permissionGroupService.saveOrUpdate(toPermissionGroup(permissionGroup)));
        permissionsUtil.setNavigationUserMap();
        return permissionGroupDTO;
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/group/{permissionGroupId}/set/{permissionSetId}")
    public Set<AvailablePermissionSetDTO> addPermissionSetIdToGroup(@PathVariable("permissionGroupId") Long permissionGroupId, @PathVariable("permissionSetId") Long availablePermissionSetId) {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(permissionGroupService.addPermissionSetId(permissionGroupId, availablePermissionSetId));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/group/{permissionGroupId}/set/{permissionSetId}")
    public Set<AvailablePermissionSetDTO> removePermissionSetIdFromGroup(@PathVariable("permissionGroupId") Long permissionGroupId, @PathVariable("permissionSetId") Long availablePermissionSetId) {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(permissionGroupService.removePermissionSetId(permissionGroupId, availablePermissionSetId));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/group/{id}")
    public PermissionGroupDTO findById(@PathVariable("id") Long id) {
        return toPermissionGroupDTO(permissionGroupService.findById(id));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/group/name/{name}")
    public PermissionGroupDTO findByName(@PathVariable("name") String name) {
        return toPermissionGroupDTO(permissionGroupService.findByName(name));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/group")
    public List<PermissionGroupDTO> findAll(@RequestParam(name = "getRemainingPermissionSets", defaultValue = "false")
                                                boolean getRemainingPermissionSets) {
        return toPermissionGroupDTOs(permissionGroupService.findAll(getRemainingPermissionSets));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/group/level/{userLevel}")
    public List<PermissionGroupDTO> findByUserLevel(@PathVariable String userLevel,
                                                               @RequestParam(name = "getRemainingPermissionSets", defaultValue = "false")
                                                               boolean getRemainingPermissionSets) {
        return toPermissionGroupDTOs(permissionGroupService.findByUserLevel(userLevel, getRemainingPermissionSets));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/group/{id}")
    public void delete(@PathVariable("id") Long id) {
        permissionGroupService.delete(id);
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/group")
    public void deleteAll() {
        permissionGroupService.deleteAll();
    }

    // Role /////////////////////////////////////////////////
    @PreAuthorize("checkAccess()")
    @PostMapping("/role")
    public RoleDTO saveRole(@RequestBody RoleDTO role) {
        RoleDTO roleDTO = toRoleDTO(roleService.saveOrUpdate(toRole(role)));
        permissionsUtil.setNavigationUserMap();
        return roleDTO;
    }

    @PreAuthorize("checkAccess()")
    @PutMapping("/role")
    public RoleDTO updateRole(@RequestBody RoleDTO role) {
        RoleDTO roleDTO = toRoleDTO(roleService.saveOrUpdate(toRole(role)));
        permissionsUtil.setNavigationUserMap();
        return roleDTO;
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/role/{roleId}/group/{permissionGroupId}")
    public Set<PermissionGroupDTO> addPermissionGroupIdToRole(@PathVariable("roleId") Long roleId, @PathVariable(
            "permissionGroupId") Long permissionGroupId) {
        return PermissionGroupMapper.toPermissionGroupDTOs(roleService.addPermissionGroup(roleId, permissionGroupId));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/role/{roleId}/group/{permissionGroupId}")
    public Set<PermissionGroupDTO> removePermissionGroupIdFromRole(@PathVariable("roleId") Long roleId,
                                                                   @PathVariable("permissionGroupId") Long permissionGroupId) {
        return PermissionGroupMapper.toPermissionGroupDTOs(roleService.removePermissionGroup(roleId,
                permissionGroupId));
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/role/{roleId}/set/{permissionSetId}")
    public Set<AvailablePermissionSetDTO> addPermissionSetIdToRole(@PathVariable("roleId") Long roleId,
                                                                   @PathVariable("permissionSetId") Long permissionSetId) {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(roleService.addPermissionSet(roleId,
                permissionSetId));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/role/{roleId}/set/{permissionSetId}")
    public Set<AvailablePermissionSetDTO> removePermissionSetIdFromRole(@PathVariable("roleId") Long roleId,
                                                                        @PathVariable("permissionSetId") Long permissionSetId) {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(roleService.removePermissionSet(roleId,
                permissionSetId));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/role/{id}")
    public RoleDTO findRoleById(@PathVariable("id") Long id) {
        return toRoleDTO(roleService.findById(id));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/role/name/{name}")
    public RoleDTO findRoleByName(@PathVariable("name") String name) {
        return toRoleDTO(roleService.findByName(name));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/role")
    public Set<RoleDTO> findAllRole(@RequestParam(name = "getRemainingPermissionGroups", defaultValue = "false")
                                            boolean getRemainingPermissionGroups) {
        return toRoleDTOs(new HashSet(roleService.findAll(getRemainingPermissionGroups)));
    }

    @GetMapping("/role/level/{userLevel}")
    public Set<RoleDTO> findAllRole(@PathVariable String userLevel, @RequestParam(name = "getRemainingPermissionGroups",
            defaultValue = "false") boolean getRemainingPermissionGroups) {
        return toRoleDTOs(new HashSet<>(roleService.findByUserLevel(userLevel, getRemainingPermissionGroups)));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/role/{id}")
    public void deleteRole(@PathVariable("id") Long id) {
        roleService.delete(id);
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/role")
    public void deleteAllRoles() {
        roleService.deleteAll();
    }

    // Key
    @PreAuthorize("checkAccess()")
    @GetMapping
    public String getPPK2() {
        return userPermissions.getPPK2();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/userType")
    public List<UserTypeDTO> getAllUserTypes() {
        return UserTypeMapper.toUserTypeDTOs(userTypeService.getAllUserTypes());
    }

    // User permissions
    @PreAuthorize("checkAccess()")
    @GetMapping("/user")
    public List<String> getUserPermissions() {
        return userPermissions.getUserPermissionsEncrypted(ECompReference.UI.getType());
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/user/{userId}/role/{roleId}")
    public Set<RoleDTO> addRole(Long userId, Long roleId) {
        return RoleMapper.toRoleDTOs(userService.addRole(userId, roleId));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/user/{userId}/role/{roleId}")
    public Set<RoleDTO> removeRole(Long userId, Long roleId) {
        return RoleMapper.toRoleDTOs(userService.removeRole(userId, roleId));
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/user/{userId}/group/{permissionGroupId}")
    public Set<PermissionGroupDTO> addPermissionGroupIdToUser(@PathVariable("userId") Long userId, @PathVariable(
            "permissionGroupId") Long permissionGroupId) {
        return PermissionGroupMapper.toPermissionGroupDTOs(userService.addPermissionGroup(userId, permissionGroupId));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/user/{userId}/group/{permissionGroupId}")
    public Set<PermissionGroupDTO> removePermissionGroupIdFromUser(@PathVariable("userId") Long userId,
                                                                   @PathVariable("permissionGroupId") Long permissionGroupId) {
        return PermissionGroupMapper.toPermissionGroupDTOs(userService.removePermissionGroup(userId,
                permissionGroupId));
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/user/{userId}/set/{permissionSetId}")
    public Set<AvailablePermissionSetDTO> addPermissionSetIdToUser(@PathVariable("userId") Long userId,
                                                                   @PathVariable("permissionSetId") Long permissionSetId) {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(userService.addPermissionSet(userId,
                permissionSetId));
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/user/{userId}/set/{permissionSetId}")
    public Set<AvailablePermissionSetDTO> removePermissionSetIdFromUser(@PathVariable("userId") Long userId,
                                                                        @PathVariable("permissionSetId") Long permissionSetId) {
        return PermissionGroupMapper.toAvailablePermissionSetDTOs(userService.removePermissionSet(userId,
                permissionSetId));
    }

    @GetMapping("/adminRole/level/{Comp-Key}/{userLevel}")
    public Set<RoleDetailDTO> findAllAdminRolesAndPermissions(@PathVariable("Comp-Key") Long compKey,
                                                              @PathVariable String userLevel) {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return toRoleDetailDTOs(new HashSet<>(roleService.findByUserLevel(userLevel, false)));
    }

}
