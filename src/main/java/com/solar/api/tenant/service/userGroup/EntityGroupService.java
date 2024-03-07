package com.solar.api.tenant.service.userGroup;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementEntityTile;
import com.solar.api.tenant.mapper.user.userGroup.EntityGroupDTO;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.model.userGroup.UserGroup;

import java.util.List;
import java.util.Map;

public interface EntityGroupService {

    EntityGroup addOrUpdate(EntityGroup entityGroup);

    EntityGroup addOrUpdateWithoutThrow(EntityGroup entityGroup);

    EntityGroup findById(Long id);

    List<EntityGroup> findByStatus(boolean status);

    List<EntityGroup> findByUserGroup(Long id);

    EntityGroup softDeleteResources(Long id, Boolean status);

    EntityGroup findByEntityRoleAndUserGroup(EntityRole entityRole, UserGroup userGroup);

    /**
     * For internal usage
     *
     * @return
     */
    UserGroup findDefaultUserGroup();

    List<UserGroup> addClosedGroupAndResources(String refType, String refId, String projectId, String groupName, List<EntityGroupDTO> entityGroupDTOList);

    List<UserGroup> findUserGroupByParentId(String parentId);

    Map addResourcesInTheGroup(String workOrderName, String refType, String refId, String projectId, List<EntityGroupDTO> entityGroupDTOList);

    EntityRole findByEntityRoleIdAndStatus(Long entityRoleId, boolean status);

    EmployeeDetail findEmployeeDetailByEntityRoleId(Long entityRoleId);

    UserGroup findByRefIdAndRefTypeAndStatus(String refId, String refType, boolean status);

    UserGroup findByRefIdAndStatus(String refId, boolean status);

    List<DefaultUserGroupResponseDTO> findByRefIdAndRefTypeAndStatusToTile(String refId, String refType, boolean status, String isExist);

    UserGroup findUserGroupById(Long userGroupId);

    UserGroup saveUserGroup(UserGroup userGroup);

    ObjectNode saveResources(UserGroup userGroup, List<Long> entityRoleIds);

    List<EntityGroup> getUnattachedEntityGroup(String refId, String refType);

    List<EntityGroup> findByEntityRolesAndUserGroup(List<Long> entityRoleIds, Long groupId);

    List<EntityGroup> saveAll(List<EntityGroup> entityGroups);

    Integer userGroupCountByEntityId(Long entityId,Boolean isDeleted,Boolean status);

    Integer userGroupCountByFunctionalRoleId(Long functionalRoleId, Long entityId, Boolean status);

    List<EntityGroup> findByEntityRoleId(Long entityId,Long functionalRoleId);

    List<EntityGroup> findAllByEntityGroupIdAndIsDeleted(Long Id, Boolean isDeleted, List<Long> entityRoleIds);

    List<EntityGroup> findAllByEntityGroupIdAndIsDeletedTrue(Long Id, Boolean isDeleted, List<Long> entityRoleIds);
    Integer getResourceCountByRefId(String refId);

    List<ProjectManagementEntityTile> getResourceByRefIdAndEntityId(List<String> refIds, Long entityId);
    List<EntityGroup> findAllByUserGroup(UserGroup userGroup);
}
