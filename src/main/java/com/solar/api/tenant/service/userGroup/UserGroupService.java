package com.solar.api.tenant.service.userGroup;

import com.solar.api.tenant.mapper.tiles.UserGroupResourceTile;
import com.solar.api.tenant.mapper.user.userGroup.EmployeeDTO;
import com.solar.api.tenant.mapper.user.userGroup.UserGroupDTO;
import com.solar.api.tenant.model.userGroup.UserGroup;

import java.util.List;

public interface UserGroupService {

    UserGroup addOrUpdate(UserGroup userGroup);

    List<UserGroup> addAll(List<UserGroup> userGroup);

    UserGroup findById(Long id);

    List<UserGroup> findAll();

    UserGroup getByGroupName(String groupName);

    UserGroup get(Long id);

    UserGroup activateUserGroup(Long groupId, Boolean status);

    List<EmployeeDTO> findExistingResourcesByGroup(Long groupId);

    UserGroup save(UserGroup userGroup);

    UserGroup findByRefIdAndRefTypeAndStatus(String refId, String refType, boolean status);

    List<UserGroup> findUserGroupByParentId(String parentId);

    List<UserGroupDTO> getAllUserGroupByType(String groupType);

    UserGroup findByRefIdAndStatus(String refId, boolean status);

    UserGroup removeUserGroupById(Long id);

    List<UserGroupResourceTile> getAllUserGroupByTypeResources(String groupType);
    UserGroup findByRefIdAndRefTypeAndStatusAndIsDeleted(String refId, String refType, boolean status, boolean isDeleted);
}
