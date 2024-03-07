package com.solar.api.tenant.repository.permission;

import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.user.userType.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {

    PermissionGroup findByName(String name);
    Set<PermissionGroup> findByIdIn(List<Long> name);

//    @Query("from PermissionGroup pg where :userTypes in elements(pg.userLevels)")
//    @Query("from PermissionGroup pg LEFT JOIN FETCH pg.permissionSets p where :userTypes in elements(p.userLevels)")
    List<PermissionGroup> findByUserLevel(UserType userLevel);
}
