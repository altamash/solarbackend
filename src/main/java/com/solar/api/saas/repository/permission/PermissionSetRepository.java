package com.solar.api.saas.repository.permission;

import com.solar.api.saas.model.permission.PermissionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PermissionSetRepository extends JpaRepository<PermissionSet, Long> {
    PermissionSet findByName(String name);

    @Query("SELECT p FROM PermissionSet p LEFT JOIN FETCH p.permissions where p.id in :ids")
    List<PermissionSet> findByIdIn(Set<Long> ids);

    @Query("SELECT p FROM PermissionSet p LEFT JOIN FETCH p.permissions LEFT JOIN FETCH p.userLevels where p.id = :id")
    PermissionSet findByIdFetchPermissions(Long id);

    @Query("SELECT p FROM PermissionSet p LEFT JOIN FETCH p.permissions LEFT JOIN FETCH p.userLevels")
    List<PermissionSet> findAllFetchPermissions();

    @Query("SELECT MAX(c.id) FROM PermissionSet c WHERE c.id < :nextIdGroup")
    Long getLastIdentifier(Long nextIdGroup);
}
