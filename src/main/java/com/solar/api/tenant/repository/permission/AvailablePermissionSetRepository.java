package com.solar.api.tenant.repository.permission;

import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.user.userType.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AvailablePermissionSetRepository extends JpaRepository<AvailablePermissionSet, Long> {

    @Query("from AvailablePermissionSet aps where :userLevels in elements(aps.userLevels)")
    List<AvailablePermissionSet> findByUserLevelsContaining(List<UserType> userLevels);
}
