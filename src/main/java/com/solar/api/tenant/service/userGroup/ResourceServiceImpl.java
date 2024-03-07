package com.solar.api.tenant.service.userGroup;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.model.userGroup.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    EntityGroupService entityGroupService;

    @Autowired
    Utility utility;

    @Override
    public ObjectNode addResources(UserGroup userGroup, List<Long> entityRoleIds) {
        UserGroup userGroupSaved = entityGroupService.saveUserGroup(userGroup);
        return entityGroupService.saveResources(userGroupSaved, entityRoleIds);
    }

    @Override
    public List<DefaultUserGroupResponseDTO> findByRefIdAndRefTypeAndStatusToTile(String refId, String refType, boolean status, String isExist) {
        UserGroup userGroup = entityGroupService.findByRefIdAndRefTypeAndStatus(refId, refType, status);
        if (userGroup != null) {
            List<EntityGroup> entityGroupsDefault = entityGroupService.findByUserGroup(userGroup.getId());
            List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList = new ArrayList<>();
            UserGroup userGroupExists = entityGroupService.findByRefIdAndStatus(isExist, status);
            List<EntityGroup> entityGroupExists = null;
            if (userGroupExists != null) {
                entityGroupExists = entityGroupService.findByUserGroup(userGroupExists.getId());
            }
            return getValidValues(entityGroupExists, entityGroupsDefault, status, defaultUserGroupResponseDTOList);
        } else {
            return null;
        }
    }

    private List<DefaultUserGroupResponseDTO> getValidValues(List<EntityGroup> entityGroupsExists, List<EntityGroup> entityGroupsDefault, boolean status,
                                                             List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList) {
        entityGroupsDefault.forEach(egDefault -> {
            if (entityGroupsExists != null && !entityGroupsExists.isEmpty()) {
                if (entityGroupsExists.stream()
                        .filter(entityGroup -> egDefault.getEntityRole().getId().equals(entityGroup.getEntityRole().getId()))
                        .findFirst().orElse(null) == null) {
                    defaultUserGroupResponseDTOList.add(getValues(egDefault, egDefault.getEntityRole(),
                            getEmployeeDetail(getEntityRole(egDefault.getEntityRole().getId(), status).getEntity().getId())));
                }
            } else {
                defaultUserGroupResponseDTOList.add(getValues(egDefault, egDefault.getEntityRole(),
                        getEmployeeDetail(getEntityRole(egDefault.getEntityRole().getId(), status).getEntity().getId())));
            }
        });
        return defaultUserGroupResponseDTOList;
    }

    private EmployeeDetail getEmployeeDetail(Long id) {
        return entityGroupService.findEmployeeDetailByEntityRoleId(id);
    }

    private EntityRole getEntityRole(Long id, boolean status) {
        return entityGroupService.findByEntityRoleIdAndStatus(id, status);
    }

    @Override
    public List<DefaultUserGroupResponseDTO> findByParentIdTile(String parentId) {
        List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList = new ArrayList<>();
        List<UserGroup> userGroupList = entityGroupService.findUserGroupByParentId(parentId);
        userGroupList.forEach(userGroup -> {
            List<EntityGroup> entityGroups = entityGroupService.findByUserGroup(userGroup.getId());
            entityGroups.forEach(entityGroup -> {
                EntityRole entityRole = entityGroupService.findByEntityRoleIdAndStatus(entityGroup.getEntityRole().getId(), true);
                EmployeeDetail employeeDetail = entityGroupService.findEmployeeDetailByEntityRoleId(entityRole.getEntity().getId());
                defaultUserGroupResponseDTOList.add(getValues(entityGroup, entityRole, employeeDetail));
            });
        });
        return defaultUserGroupResponseDTOList;
    }

    @Override
    public UserGroup findUserGroupById(Long userGroupId) {
        return entityGroupService.findUserGroupById(userGroupId);
    }

    @Override
    public UserGroup saveUserGroup(UserGroup userGroup) {
        return entityGroupService.saveUserGroup(userGroup);
    }

    //    @Override
    //    public List<ResourceConsumed> findByParentId(String parentId) {
    //        return resourceConsumedRepository.findByParentId(parentId);
    //    }

    private DefaultUserGroupResponseDTO getValues(EntityGroup entityGroup, EntityRole entityRole, EmployeeDetail employeeDetail) {
        return getDefaultUserGroupResponseDTO(entityGroup, entityRole, employeeDetail);
    }

    static DefaultUserGroupResponseDTO getDefaultUserGroupResponseDTO(EntityGroup entityGroup, EntityRole entityRole, EmployeeDetail employeeDetail) {
        return DefaultUserGroupResponseDTO.builder()
                .entityRoleId(entityRole.getId())
                .entityGroupId(entityGroup.getId())
                .userGroupId(entityGroup.getUserGroup().getId())
                .entityId(entityRole.getEntity().getId())
                .employeeName(entityRole.getEntity().getEntityName() == null ? null : entityRole.getEntity().getEntityName())
                .employeeEmail(entityRole.getEntity().getContactPersonEmail() == null ? null : entityRole.getEntity().getContactPersonEmail())
                .employeePhone(entityRole.getEntity().getContactPersonPhone() == null ? null : entityRole.getEntity().getContactPersonPhone())
                .employeeDesignation(entityRole.getFunctionalRoles().getName())
                .employmentType(employeeDetail.getEmploymentType())
                .status(entityRole.isStatus())
                .employeeJoiningDate(employeeDetail.getDateOfJoining() == null ? null : employeeDetail.getDateOfJoining())
                .build();
    }

    @Override
    public Map enableAndDisableResource(Long userGroupId, Boolean status) {
        Map<String, String> response = new HashMap();
        try {
            String message;
            UserGroup userGroup = findUserGroupById(userGroupId);
            userGroup.setStatus(status);
            if (status) {
                message = "Resource enabled successfully";
            } else {
                message = "Resource disabled successfully";
            }
            response.put("code", HttpStatus.OK.toString());
            response.put("data", String.valueOf(saveUserGroup(userGroup)));
            response.put("message", message);
        } catch (Exception ex) {
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.put("data", null);
            response.put("message", ex.getMessage());
        }
        return response;
    }

    @Override
    public Map enableAndDisableResourceByEntityGroupId(Long entityGroupId, Boolean status) {
        Map<String, String> response = new HashMap();
        try {
            String message = Boolean.TRUE.equals(status) ? "Resource enabled successfully" : "Resource disabled successfully";
            response.put("code", HttpStatus.OK.toString());
            response.put("data", String.valueOf(entityGroupService.softDeleteResources(entityGroupId, status)));
            response.put("message", message);
        } catch (Exception ex) {
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.put("data", null);
            response.put("message", ex.getMessage());
        }
        return response;
    }
}
