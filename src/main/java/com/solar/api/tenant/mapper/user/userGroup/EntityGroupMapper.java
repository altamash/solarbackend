package com.solar.api.tenant.mapper.user.userGroup;

import com.solar.api.tenant.mapper.EntityGroupTemplate;
import com.solar.api.tenant.mapper.tiles.EntityGroupTile;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.UserGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EntityGroupMapper {

    //EntityGroup
    public static EntityGroup toEntityGroup(EntityGroupDTO entityGroupDTO) {
        return EntityGroup.builder()
                .id(entityGroupDTO.getId())
                .status(entityGroupDTO.isStatus())
                .entityRole(EntityRoleMapper.toEntityRole(entityGroupDTO.getEntityRole()))
                .userGroup(UserGroupMapper.toUserGroup(entityGroupDTO.getUserGroup()))
                .createdBy(entityGroupDTO.getCreatedBy())
                .updatedBy(entityGroupDTO.getUpdatedBy())
                .build();
    }

    public static EntityGroupDTO toEntityGroupDTO(EntityGroup entityGroup) {
        if (entityGroup == null) {
            return null;
        }

        return EntityGroupDTO.builder()
                .id(entityGroup.getId())
                .status(entityGroup.isStatus())
                .entityRole(EntityRoleMapper.toEntityRoleDTO(entityGroup.getEntityRole()))
                .userGroup(UserGroupMapper.toUserGroupDTO(entityGroup.getUserGroup()))
                .createdBy(entityGroup.getCreatedBy())
                .updatedBy(entityGroup.getUpdatedBy())
                .build();
    }

    public static EntityGroup toUpdateEntityGroup(EntityGroup entityGroup, EntityGroup entityGroupUpdate) {
        entityGroup.setStatus(entityGroupUpdate.isStatus() == entityGroup.isStatus()
                ? entityGroup.isStatus() : entityGroupUpdate.isStatus());
        entityGroup.setUpdatedAt(LocalDateTime.parse(String.valueOf(new org.joda.time.LocalDateTime())));
        entityGroup.setUpdatedBy(entityGroupUpdate.getUpdatedBy() != null ? entityGroupUpdate.getUpdatedBy() : "");
        return entityGroup;
    }

    public static List<EntityGroup> toEntityGroups(List<EntityGroupDTO> entityGroupDTOs) {
        return entityGroupDTOs.stream().map(u -> toEntityGroup(u)).collect(Collectors.toList());
    }

    public static List<EntityGroupDTO> toEntityGroupDTOs(List<EntityGroup> entityGroups) {
        return entityGroups.stream().map(u -> toEntityGroupDTO(u)).collect(Collectors.toList());
    }

    public static List<EntityGroup> toEntityGroups(List<EntityGroupDTO> entityGroupDTOs, UserGroup userGroup) {
        return entityGroupDTOs.stream().map(u -> toEntityGroup(u,userGroup)).collect(Collectors.toList());
    }
    //EntityGroup
    public static EntityGroup toEntityGroup(EntityGroupDTO entityGroupDTO, UserGroup userGroup) {
        return EntityGroup.builder()
                 .id(entityGroupDTO.getId())
                .status(entityGroupDTO.isStatus())
                .entityRole(EntityRoleMapper.toEntityRole(entityGroupDTO.getEntityRole()))
                .userGroup(userGroup)
                .createdBy(entityGroupDTO.getCreatedBy())
                .updatedBy(entityGroupDTO.getUpdatedBy())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static List<EntityGroupTile> toUserGroupTiles(List<EntityGroupTemplate> entityGroupTemplates){
        return entityGroupTemplates.stream().map(EntityGroupMapper::toEntityGroupTile).collect(Collectors.toList());

    }

    public static EntityGroupTile toEntityGroupTile(EntityGroupTemplate entityGroupTemplate) {
        return EntityGroupTile.builder()
                .entityGroupId(entityGroupTemplate.getEntityGroupId())
                .entityName(entityGroupTemplate.getEntityName())
                .email(entityGroupTemplate.getEmail())
                .designation(entityGroupTemplate.getDesignation())
                .contactNumber(entityGroupTemplate.getContactNumber())
                .employeeType(entityGroupTemplate.getEmployeeType())
                .joiningDate(entityGroupTemplate.getJoiningDate())
                .imageURI(entityGroupTemplate.getImageURI())
                .entityId(entityGroupTemplate.getEntityId())
                .entityRoleId(entityGroupTemplate.getEntityRoleId())
                .build();
    }
}
