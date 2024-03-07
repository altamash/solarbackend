package com.solar.api.saas.service.process.permission;

import com.solar.api.saas.model.permission.PermissionSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionSetService {

    PermissionSet saveOrUpdate(PermissionSet permissionSet);

    List<PermissionSet> saveAll(List<PermissionSet> permissionSets);

    List<String> addUserLevel(Long permissionSetId, List<String> userLevel);

    List<String> removeUserLevel(Long permissionSetId, List<String> userLevel);

    void addPermission(Long permissionSetId, Long permissionId);

    void addPermissionsByName(Long permissionSetId, String permissionNamesCSV);

    void removePermission(Long permissionSetId, Long permissionId);

    PermissionSet findById(Long id);

    PermissionSet findByName(String name);

    List<PermissionSet> findByIdIn(Set<Long> ids);

    PermissionSet findByIdFetchPermissions(Long id);

    Map<Long, List<String>> getPermissionSetIdPermissionNameMap();

    Long getNextIdentifier(String compReference);

    List<PermissionSet> findAll();

    void delete(Long id);

    void deleteAll();
}
