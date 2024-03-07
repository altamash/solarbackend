package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

//    @Query("from Role role where :permissionName in elements(role.permissionSets)")
//    List<Role> findByPermissionName(String permissionName);

//    @Query("select r.permissionSets from Role r LEFT JOIN FETCH r.permissionSets where r = :role")
//    Set<String> getAuthoritiesFromPermissionSets(Role role);

    void deleteByName(String name);

    @Query("from Role role where :permissionGroups in elements(role.permissionGroups)")
    List<Role> findByPermissionGroupsContaining(List<PermissionGroup> permissionGroups);

    List<Role> findByUserLevel(UserType userLevel);
}
