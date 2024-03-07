package com.solar.api.saas.controller.v1.permission;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.permission.PermissionDTO;
import com.solar.api.saas.mapper.permission.PermissionSetDTO;
import com.solar.api.saas.mapper.permission.component.ComponentTypeProvisionDTO;
import com.solar.api.saas.model.permission.component.ComponentTypeProvision;
import com.solar.api.saas.repository.ComponentTypeProvisionRepository;
import com.solar.api.saas.service.process.permission.PermissionService;
import com.solar.api.saas.service.process.permission.PermissionSetService;
import com.solar.api.tenant.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.solar.api.saas.mapper.permission.PermissionMapper.*;
import static com.solar.api.saas.mapper.permission.component.ComponentLibraryMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASPermissionController")
@RequestMapping(value = "/saas/permission")
public class SAASPermissionController {

    @Autowired
    private ComponentTypeProvisionRepository componentTypeProvisionRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ApplicationContext context;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private PermissionSetService permissionSetService;

    // Permission /////////////////////////////////////////////////
    @PostMapping
    public PermissionDTO savePermission(@RequestBody PermissionDTO permission, @RequestParam(value =
            "componentLibraryId", required = false) Long componentLibraryId) {
        return toPermissionDTO(permissionService.saveOrUpdate(toPermission(permission), componentLibraryId));
    }

    @PutMapping
    public PermissionDTO updatePermission(@RequestBody PermissionDTO permission, @RequestParam(value =
            "componentLibraryId", required = false) Long componentLibraryId) {
        return toPermissionDTO(permissionService.saveOrUpdate(toPermission(permission), componentLibraryId));
    }

    @GetMapping("/{id}")
    public PermissionDTO findById(@PathVariable("id") Long id) {
        return toPermissionDTO(permissionService.findById(id));
    }

    @GetMapping("/name/{name}")
    public PermissionDTO findByName(@PathVariable("name") String name) {
        return toPermissionDTO(permissionService.findByName(name));
    }

    @GetMapping("/getPermissionSetIdPermissionNameMap")
    public Map<Long, List<String>> getPermissionSetIdPermissionNameMap() {
        return permissionSetService.getPermissionSetIdPermissionNameMap();
    }

    @GetMapping
    public List<PermissionDTO> findAll() {
        return toPermissionDTOs(permissionService.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        permissionService.delete(id);
    }

    @DeleteMapping
    public void deleteAll() {
        permissionService.deleteAll();
    }

    // PermissionSet /////////////////////////////////////////////////
    @PostMapping("/set")
    public PermissionSetDTO savePermissionSet(@RequestBody PermissionSetDTO permissionSet) {
        return toPermissionSetDTO(permissionSetService.saveOrUpdate(toPermissionSet(permissionSet)));
    }

    @PutMapping("/set")
    public PermissionSetDTO updatePermissionSet(@RequestBody PermissionSetDTO permissionSet) {
        return toPermissionSetDTO(permissionSetService.saveOrUpdate(toPermissionSet(permissionSet)));
    }

    @GetMapping("/set/{permissionSetId}/level/add")
    public List<String> addUserLevel(@PathVariable("permissionSetId") Long permissionSetId, @RequestParam(value = "userLevelCSV") String userLevelCSV) {
        return permissionSetService.addUserLevel(permissionSetId, Arrays.asList(userLevelCSV).stream().map(level -> level.trim()).collect(Collectors.toList()));
    }

    @GetMapping("/set/{permissionSetId}/level/remove")
    public List<String> removeUserLevel(@PathVariable("permissionSetId") Long permissionSetId, @RequestParam(value = "userLevelCSV") String userLevelCSV) {
        return permissionSetService.removeUserLevel(permissionSetId, Arrays.asList(userLevelCSV).stream().map(level -> level.trim()).collect(Collectors.toList()));
    }

    @PostMapping("/set/{permissionSetId}/permission/{permissionId}")
    public void addPermissionToPermissionSet(@PathVariable("permissionSetId") Long permissionSetId, @PathVariable("permissionId") Long permissionId) {
        permissionSetService.addPermission(permissionSetId, permissionId);
    }

    @PostMapping("/set/{permissionSetId}")
    public void addPermissionsToPermissionSetByName(@PathVariable("permissionSetId") Long permissionSetId, @RequestParam("permissionNamesCSV") String permissionNamesCSV) {
        permissionSetService.addPermissionsByName(permissionSetId, permissionNamesCSV);
    }

    @DeleteMapping("/set/{permissionSetId}/permission/{permissionId}")
    public void removePermissionFromPermissionSet(@PathVariable("permissionSetId") Long permissionSetId, @PathVariable("permissionId") Long permissionId) {
        permissionSetService.removePermission(permissionSetId, permissionId);
    }

    @GetMapping("/set/{id}")
    public PermissionSetDTO findPermissionSetById(@PathVariable("id") Long id) {
        return toPermissionSetDTO(permissionSetService.findById(id));
    }

    @GetMapping("/set/name/{name}")
    public PermissionSetDTO findPermissionSetByName(@PathVariable("name") String name) {
        return toPermissionSetDTO(permissionSetService.findByName(name));
    }

    @GetMapping("/set")
    public List<PermissionSetDTO> findAllPermissionSet() {
        return toPermissionSetDTOs(permissionSetService.findAll());
    }

    @DeleteMapping("/set/{id}")
    public void deletePermissionSet(@PathVariable("id") Long id) {
        permissionSetService.delete(id);
    }

    @DeleteMapping("/set")
    public void deleteAllPermissionSet() {
        permissionSetService.deleteAll();
    }

    // ComponentTypeProvision /////////////////////////////////////////////////
    @PostMapping("/componentTypeProvision")
    public ComponentTypeProvisionDTO saveComponentTypeProvision(@RequestBody ComponentTypeProvisionDTO componentTypeProvision) {
        return toComponentTypeProvisionDTO(componentTypeProvisionRepository.save(toComponentTypeProvision(componentTypeProvision)));
    }

    @PutMapping("/componentTypeProvision")
    public ComponentTypeProvisionDTO updateComponentTypeProvision(@RequestBody ComponentTypeProvisionDTO componentTypeProvision) {
        return toComponentTypeProvisionDTO(componentTypeProvisionRepository.save(toComponentTypeProvision(componentTypeProvision)));
    }

    @GetMapping("/componentTypeProvision/{id}")
    public ComponentTypeProvisionDTO findComponentTypeProvisionById(@PathVariable("id") Long id) {
        return toComponentTypeProvisionDTO(componentTypeProvisionRepository.findById(id).orElseThrow(() -> new NotFoundException(ComponentTypeProvision.class, id)));
    }

    @GetMapping("/componentTypeProvision/compReference/{compReference}")
    public ComponentTypeProvisionDTO findComponentTypeProvisionByName(@PathVariable("name") String compReference) {
        return toComponentTypeProvisionDTO(componentTypeProvisionRepository.findByCompReference(compReference));
    }

    @GetMapping("/componentTypeProvision")
    public List<ComponentTypeProvisionDTO> findAllComponentTypeProvisions() {
        return toComponentTypeProvisionDTOs(componentTypeProvisionRepository.findAll());
    }

    @DeleteMapping("/componentTypeProvision/{id}")
    public void deleteComponentTypeProvision(@PathVariable("id") Long id) {
        componentTypeProvisionRepository.deleteById(id);
    }

    @DeleteMapping("/componentTypeProvision")
    public void deleteAllComponentTypeProvisions() {
        componentTypeProvisionRepository.deleteAll();
    }

}
