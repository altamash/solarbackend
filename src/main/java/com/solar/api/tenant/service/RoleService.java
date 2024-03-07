package com.solar.api.tenant.service;

import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.user.role.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

//    Role saveOrUpdate(Role user);

    Role saveOrUpdate(Role role);

//    Role update(Role role);

    Role findById(Long id);

    Role findByName(String name);

//    Role findByName(String name);

//    List<Role> findByPermissionName(String permissionName);

    List<Role> findByUserLevel(String userLevel);

    List<Role> findByUserLevel(String userLevel, boolean getRemainingPermissionGroups);

    List<Role> findAll(boolean getRemainingPermissionGroups);

    void delete(Long id);

    void delete(String name);

    void deleteAll();

    // Role permissions
    Set<PermissionGroup> addPermissionGroup(Long roleId, Long permissionGroupId);

    Set<PermissionGroup> removePermissionGroup(Long roleId, Long permissionGroupId);

    Set<AvailablePermissionSet> addPermissionSet(Long roleId, Long availablePermissionSetId);

    Set<AvailablePermissionSet> removePermissionSet(Long roleId, Long availablePermissionSetId);

    }
