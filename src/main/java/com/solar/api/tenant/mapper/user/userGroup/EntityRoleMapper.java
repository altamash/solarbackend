package com.solar.api.tenant.mapper.user.userGroup;

import com.solar.api.tenant.mapper.EntityRoleResponseDTO;
import com.solar.api.tenant.mapper.contract.EntityMapper;
import com.solar.api.tenant.mapper.extended.FunctionalRolesMapper;
import com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile;
import com.solar.api.tenant.model.userGroup.EntityRole;

import java.util.List;
import java.util.stream.Collectors;

public class EntityRoleMapper {

    public static EntityRole toEntityRole(EntityRoleDTO entityRoleDTO) {
        if (entityRoleDTO == null) {
            return null;
        }
        return EntityRole.builder()
                .id(entityRoleDTO.getId())
                .entity(EntityMapper.toEntity(entityRoleDTO.getEntity()))
                .functionalRoles(FunctionalRolesMapper.toFunctionalRoles(entityRoleDTO.getFunctionalRoles()))
                .status(entityRoleDTO.isStatus())
                .createdBy(entityRoleDTO.getCreatedBy())
                .updatedBy(entityRoleDTO.getUpdatedBy())
                .build();
    }

    public static EntityRoleDTO toEntityRoleDTO(EntityRole entityRole) {
        if (entityRole == null) {
            return null;
        }

        return EntityRoleDTO.builder()
                .id(entityRole.getId())
                .entity(EntityMapper.toEntityDTO(entityRole.getEntity()))
                .functionalRoles(FunctionalRolesMapper.toFunctionalRolesDTO(entityRole.getFunctionalRoles()))
                .status(entityRole.isStatus())
                .createdBy(entityRole.getCreatedBy())
                .updatedBy(entityRole.getUpdatedBy())
                .isDeleted(entityRole.getIsDeleted())
                .build();
    }

    public static EntityRole toUpdateEntityRole(EntityRole entityRole, EntityRole entityRoleUpdate) {
        entityRole.setStatus(entityRoleUpdate.isStatus() == entityRole.isStatus() ? entityRole.isStatus()
                : entityRoleUpdate.isStatus());
        entityRole.setCreatedBy(entityRoleUpdate.getCreatedBy() == null ? entityRole.getCreatedBy() : entityRoleUpdate.getCreatedBy());
        entityRole.setUpdatedBy(entityRoleUpdate.getUpdatedBy() == null ? entityRole.getUpdatedBy() : entityRoleUpdate.getUpdatedBy());
        entityRole.setIsDeleted(entityRoleUpdate.getIsDeleted() == entityRole.getIsDeleted() ? entityRole.getIsDeleted()
                : entityRoleUpdate.getIsDeleted());
        return entityRole;
    }

    public static List<EntityRole> toEntityRoles(List<EntityRoleDTO> entityRoleDTOS) {
        return entityRoleDTOS.stream().map(u -> toEntityRole(u)).collect(Collectors.toList());
    }

    public static List<EntityRoleDTO> toEntityRoleDTOs(List<EntityRole> entityRoles) {
        return entityRoles.stream().map(u -> toEntityRoleDTO(u)).collect(Collectors.toList());
    }

    public static List<EntityRoleResponseDTO> toEntityRoleResponseDTOs(List<EntityRole> entityRoles) {
        return entityRoles.stream().map(EntityRoleMapper::toEntityRoleResponseDTO).collect(Collectors.toList());
    }

    public static EntityRoleResponseDTO toEntityRoleResponseDTO(EntityRole entityRole) {
        if (entityRole == null) {
            return null;
        }

        return EntityRoleResponseDTO.builder()
                .entityRoleId(entityRole.getId())
                .entityId(entityRole.getEntity().getId())
                .functionalRoleId(entityRole.getFunctionalRoles().getId())
                .entityName(entityRole.getEntity().getEntityName())
                .functionalRoleName(entityRole.getFunctionalRoles().getName())
                .build();
    }

    public static List<EntityFunctionalRoleTile> toEntityFunctionalRoleTiles(List<EntityRole> entityRoles) {
        return entityRoles.stream().map(EntityRoleMapper::toEntityFunctionalRoleTile).collect(Collectors.toList());
    }

    public static EntityFunctionalRoleTile toEntityFunctionalRoleTile(EntityRole entityRole) {
        if (entityRole == null) {
            return null;
        }

        return EntityFunctionalRoleTile.builder()
                .entityRoleId(entityRole.getId())
                .entityId(entityRole.getEntity().getId())
                .functionalRoleId(entityRole.getFunctionalRoles().getId())
                .functionalRoleName(entityRole.getFunctionalRoles().getName())
                .entityName(entityRole.getEntity().getEntityName())
                .category(entityRole.getFunctionalRoles().getCategory())
                .email(entityRole.getEntity().getContactPersonEmail())
                .build();
    }

}
