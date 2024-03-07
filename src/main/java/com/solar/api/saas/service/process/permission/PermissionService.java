package com.solar.api.saas.service.process.permission;

import com.solar.api.saas.model.permission.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    Permission saveOrUpdate(Permission permission, Long componentLibraryId);

    List<Permission> saveAll(List<Permission> permissions);

    Permission findById(Long id);

    List<Permission> findByIdIn(Set<Long> ids);

    Permission findByName(String name);

    List<Permission> findByNameIn(List<String> names);

    List<Permission> findAll();

    Long getNextIdentifier(String compReference);

    void delete(Long id);

    void deleteAll();
}
