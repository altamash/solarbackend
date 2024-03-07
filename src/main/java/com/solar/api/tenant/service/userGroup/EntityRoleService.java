package com.solar.api.tenant.service.userGroup;

import com.solar.api.tenant.mapper.EntityRoleResponseDTO;
import com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.userGroup.EntityRole;

import java.util.List;
import java.util.Map;

public interface EntityRoleService {

    EntityRole addOrUpdate(EntityRole entityRole);

    EntityRole findById(Long id);

    List<EntityRole> findByEntity(Long id);

    List<DefaultUserGroupResponseDTO> findByStatus(boolean status, String isExist);

    EntityRole findEntityRoleByIdAndStatus(Long entityRoleId, boolean status);

    List<DefaultUserGroupResponseDTO> findByDefaultUserGroup(Long userGroupId);

    EmployeeDetail findEmployeeDetailByEntityId(Long id);

    List<EntityRole> findAllByEntityId(Long entityId);

    List<EntityRole> findAll();

    List<EntityFunctionalRoleTile> findAllByFunctionRoleId(Long roleId);

    List<EntityRoleResponseDTO> getAllEntityRole();

    Map<String, String> addOrUpdate(List<EntityRole> entityRoles);

    EntityRole findByEntityIdAndFunctionRoleId(Long entityId, Long roleId);

    List<EntityRole> saveAll(List<EntityRole> entityRoles);

    List<EntityRole> findAllByEntityIdAndIsDeleted(Long entityId, Boolean isDeleted, List<Long> existingRoleIds);

    Map<String, String> removeEntityRole(Long entityId, Long roleId);

    List<EntityRole> findAllByEntityIdAndIsDeleted(Long entityId, Boolean isDeleted);

    List<EntityFunctionalRoleTile> findAllByFunctionRoleName(String roleName);

}
