package com.solar.api.tenant.repository.UserGroup;

import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroup;
import com.solar.api.tenant.model.userGroup.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultUserGroupRepository extends JpaRepository<DefaultUserGroup, Long> {
}
