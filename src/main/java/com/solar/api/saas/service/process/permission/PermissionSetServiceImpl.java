package com.solar.api.saas.service.process.permission;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.permission.PermissionMapper;
import com.solar.api.saas.model.permission.Permission;
import com.solar.api.saas.model.permission.PermissionSet;
import com.solar.api.saas.model.permission.component.ECompReference;
import com.solar.api.saas.model.permission.userLevel.EUserLevel;
import com.solar.api.saas.model.permission.userLevel.PermissionUserLevel;
import com.solar.api.saas.repository.permission.PermissionSetRepository;
import com.solar.api.saas.repository.permission.PermissionUserLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionSetServiceImpl implements PermissionSetService {

    @Autowired
    private PermissionSetRepository repository;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private PermissionUserLevelRepository permissionUserLevelRepository;

    @Override
    public PermissionSet saveOrUpdate(PermissionSet permissionSet) {
        if (permissionSet.getUserLevelNames() != null) {
            Set<PermissionUserLevel> userLevels =
                    permissionUserLevelRepository.findByNameIn(permissionSet.getUserLevelNames().stream().map(EUserLevel::get).collect(Collectors.toList()));
            permissionSet.setUserLevels(userLevels);
        }
        if (permissionSet.getId() != null) {
            PermissionSet permissionSetDb = findById(permissionSet.getId());
            permissionSetDb = PermissionMapper.toUpdatedPermissionSet(permissionSetDb, permissionSet);
            return repository.save(permissionSetDb);
        }
        return repository.save(permissionSet);
    }

    @Override
    public List<PermissionSet> saveAll(List<PermissionSet> permissionSets) {
        return repository.saveAll(permissionSets);
    }

    @Override
    public List<String> addUserLevel(Long permissionSetId, List<String> userLevel) {
        PermissionSet permissionSet = findById(permissionSetId);
        Set<EUserLevel> eUserLevels = userLevel.stream().map(EUserLevel::get).collect(Collectors.toSet());
        permissionSet.getUserLevels().addAll(new HashSet(eUserLevels));
        repository.save(permissionSet);
        return permissionSet.getUserLevels().stream().map(type -> type.getName().getName()).collect(Collectors.toList());
    }

    @Override
    public List<String> removeUserLevel(Long permissionSetId, List<String> userLevel) {
        PermissionSet permissionSet = findById(permissionSetId);
        Set<EUserLevel> eUserLevels = userLevel.stream().map(EUserLevel::get).collect(Collectors.toSet());
        permissionSet.getUserLevels().removeAll(new HashSet(eUserLevels));
        repository.save(permissionSet);
        return permissionSet.getUserLevels().stream().map(type -> type.getName().getName()).collect(Collectors.toList());
    }

    @Override
    public void addPermission(Long permissionSetId, Long permissionId) {
        PermissionSet permissionSet = findByIdFetchPermissions(permissionSetId);
        permissionSet.addPermission(permissionService.findById(permissionId));
        repository.save(permissionSet);
    }

    @Override
    public void addPermissionsByName(Long permissionSetId, String permissionNamesCSV) {
        List<String> permissionNames =
                Arrays.stream(permissionNamesCSV.split(",")).map(String::trim).collect(Collectors.toList());
        List<Permission> permissionsInDb =
                permissionService.findByNameIn(permissionNames);
        PermissionSet permissionSet = findByIdFetchPermissions(permissionSetId);
        permissionsInDb.removeIf(p -> permissionSet.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()).contains(p.getName()));
        if (!permissionsInDb.isEmpty()) {
            permissionSet.addPermissions(permissionsInDb);
            repository.save(permissionSet);
        }
    }

    @Override
    public void removePermission(Long permissionSetId, Long permissionId) {
        PermissionSet permissionSet = findById(permissionSetId);
        permissionSet.removePermission(permissionService.findById(permissionId));
        repository.save(permissionSet);
    }

    @Override
    public PermissionSet findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(PermissionSet.class, id));
    }

    @Override
    public PermissionSet findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<PermissionSet> findByIdIn(Set<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public PermissionSet findByIdFetchPermissions(Long id) {
        return repository.findByIdFetchPermissions(id);
    }

    /**
     * Inmemory PermissionSet id, Permission name map
    */
    @Cacheable("pNamesMap")
    @Override
    public Map<Long, List<String>> getPermissionSetIdPermissionNameMap() {
        Map<Long, List<String>> permissionSetIdPermissionNameMap = new HashMap<>();
        for (PermissionSet permissionSet : repository.findAllFetchPermissions()) {
            List<String> permissionNames = permissionSet.getPermissions().stream()
                    .filter(p -> ECompReference.API.getType().equals(p.getComponentLibrary().getComponentTypeProvision().getCompReference()))
                    .map(Permission::getName).collect(Collectors.toList());
            if (!permissionNames.isEmpty()) {
                permissionSetIdPermissionNameMap.put(permissionSet.getId(), permissionNames);
            }
        }
        return permissionSetIdPermissionNameMap;
    }

    @Override
    public Long getNextIdentifier(String compReference) {
        Long lastIdentifier = null;
        if (ECompReference.UI.getType().equals(compReference)) {
                lastIdentifier = repository.getLastIdentifier(50000l);
        } else if (ECompReference.API.getType().equals(compReference)) {
                lastIdentifier = repository.getLastIdentifier(100000l);
        }
        return lastIdentifier == null ? 1 : lastIdentifier;
    }

    @Override
    public List<PermissionSet> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
