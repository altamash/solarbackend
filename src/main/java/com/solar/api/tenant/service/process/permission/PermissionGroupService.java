package com.solar.api.tenant.service.process.permission;

import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;

import java.util.List;
import java.util.Set;

public interface PermissionGroupService {

    Set<AvailablePermissionSet> getAvailablePermissionSets();

    Set<AvailablePermissionSet> getAvailablePermissionSetsByUserLevels(List<String> names);

    void addAvailablePermissionSet(Long permissionSetId);

    void addAvailablePermissionSet(String permissionSetIdCSV);

    void removeAvailablePermissionSet(Long permissionSetId);

    PermissionGroup saveOrUpdate(PermissionGroup permissionGroup);

    Set<AvailablePermissionSet> addPermissionSetId(Long permissionGroupId, Long availablePermissionSetId);

    Set<AvailablePermissionSet> removePermissionSetId(Long permissionGroupId, Long availablePermissionSetId);

    PermissionGroup findById(Long id);

    PermissionGroup findByName(String name);

    Set<PermissionGroup> findByIdIn(List<Long> name);

    List<PermissionGroup> findByUserLevel(String userLevel);

    List<PermissionGroup> findByUserLevel(String userLevel, boolean getRemainingPermissionSets);

    List<PermissionGroup> findAll(boolean getRemainingPermissionSets);

    void delete(Long id);

    void deleteAll();
}
