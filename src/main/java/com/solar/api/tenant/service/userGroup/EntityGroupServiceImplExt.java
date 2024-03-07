package com.solar.api.tenant.service.userGroup;

import com.solar.api.tenant.mapper.user.userGroup.EntityGroupDTO;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.UserGroup;
import com.solar.api.tenant.repository.UserGroup.EntityGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.solar.api.tenant.mapper.user.userGroup.EntityGroupMapper.toEntityGroups;

/**
 * Added to avoid circular reference
 */
@Service
public class EntityGroupServiceImplExt {

    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private EntityGroupRepository entityGroupRepository;

    public List<UserGroup> addClosedGroupAndResources(String refType, String refId, String projectId, String
            groupName, List<EntityGroupDTO> entityGroupDTOList) {
        /*
            1- will create closed group of w/o under user group tbl
            2- enter data in entity group tbl (entity role id and group id)
            3- enter data in resource consumed tbl (entity group id and w/o id)
            group type = closed
       **/
        List<UserGroup> userGroupList = new ArrayList<>();
        userGroupList.add(UserGroup.builder()
                .userGroupName(groupName)
                .userGroupType("closed")
                .status(Boolean.TRUE)
                .isActive(Boolean.TRUE)
                .refType(refType)
                .refId(refId)
                .parentId(projectId)
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .updatedAt(null).isDeleted(Boolean.FALSE).build());
        userGroupService.addAll(userGroupList);
        List<EntityGroup> entityGroupList = new ArrayList<>();
        List<EntityGroup> entityGroups = toEntityGroups(entityGroupDTOList);
        userGroupList.forEach(userGroup -> {
            entityGroups.forEach(entityGroup -> {
                entityGroupList.add(EntityGroup.builder()
                        .userGroup(userGroup)
                        .status(entityGroup.isStatus())
                        .entityRole(entityGroup.getEntityRole())
                        .createdBy("admin")
                        .build());
            });
        });
        entityGroupRepository.saveAll(entityGroupList);
        return userGroupList;
    }
}
