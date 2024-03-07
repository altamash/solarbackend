package com.solar.api.saas.repository.permission;

import com.solar.api.saas.model.permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
   /* @Query("select DISTINCT p from PermissionSet p LEFT JOIN FETCH p.componentLibraries where p.name in :names")
    List<PermissionSet> getPermissionSetsFetchComponentLibrary(Set<String> names);*/

    Permission findByName(String name);

    List<Permission> findByIdIn(Set<Long> ids);

    @Query("SELECT MAX(p.id) FROM Permission p WHERE p.id < :nextIdGroup")
    Long getLastIdentifier(Long nextIdGroup);

    List<Permission> findByNameIn(List<String> names);
}
