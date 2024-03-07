package com.solar.api.tenant.service.userGroup;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.UserGroup;

import java.util.List;
import java.util.Map;

public interface ResourceService {

    ObjectNode addResources(UserGroup userGroup, List<Long> entityRoleIds);
    List<DefaultUserGroupResponseDTO> findByRefIdAndRefTypeAndStatusToTile(String refId, String refType, boolean status, String isExist);

    Map enableAndDisableResource(Long resourceConsumedId, Boolean status);

    Map enableAndDisableResourceByEntityGroupId(Long entityGroupId, Boolean status);

    List<DefaultUserGroupResponseDTO> findByParentIdTile(String parentId);

    UserGroup findUserGroupById(Long userGroupId);

    UserGroup saveUserGroup(UserGroup userGroup);


}
