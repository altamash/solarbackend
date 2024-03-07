package com.solar.api.tenant.service.userGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Message;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementEntityTile;
import com.solar.api.tenant.mapper.user.userGroup.EntityGroupDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityGroupMapper;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.model.userGroup.UserGroup;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.repository.UserGroup.EntityGroupRepository;
import com.solar.api.tenant.repository.UserGroup.EntityRoleRepository;
import com.solar.api.tenant.repository.project.EmployeeDetailRepository;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.user.userGroup.EntityGroupMapper.toEntityGroups;

@Service
public class EntityGroupServiceImpl implements EntityGroupService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    EntityGroupRepository entityGroupRepository;

    @Autowired
    UserGroupService userGroupService;

    @Autowired
    EntityRoleRepository entityRoleRepository;
    @Autowired
    EmployeeDetailRepository employeeDetailRepository;

    @Autowired
    EntityGroupServiceImplExt entityGroupServiceImplExt;

    @Override
    public EntityGroup addOrUpdate(EntityGroup entityGroup) {
        EntityGroup entityGroupData = getEntityGroup(entityGroup);
        if (entityGroupData != null) return entityGroupData;
        /**
         * There will only be one entityRole associated with one group
         */
        if (entityGroup.getEntityRole() != null && entityGroup.getUserGroup() != null) {
            if (ifExist(entityGroup)) {
                throw new AlreadyExistsException(EntityRole.class, "id", String.valueOf(entityGroup.getEntityRole().getId()),
                        UserGroup.class, "id", String.valueOf(entityGroup.getUserGroup().getId()));
            } else {
                return entityGroupRepository.save(checkForValidEntityRoleAndUserGroup(entityGroup));
            }
        }
        throw new NotFoundException(EntityRole.class, "id", "", UserGroup.class, "id", "");
    }

    @Override
    public EntityGroup addOrUpdateWithoutThrow(EntityGroup entityGroup) {
        EntityGroup entityGroupData = getEntityGroup(entityGroup);
        if (entityGroupData != null) return entityGroupData;
        /**
         * There will only be one entityRole associated with one group
         */
        if (entityGroup.getEntityRole() != null && entityGroup.getUserGroup() != null) {
            if (!ifExist(entityGroup)) {
                return entityGroupRepository.save(checkForValidEntityRoleAndUserGroup(entityGroup));
            }
        }
        return null;
    }

    @Nullable
    private EntityGroup getEntityGroup(EntityGroup entityGroup) {
        if (entityGroup.getId() != null) {
            EntityGroup entityGroupData = findById(entityGroup.getId());
            if (entityGroupData == null) {
                throw new NotFoundException(EntityGroup.class, entityGroup.getId());
            }
            entityGroup.setUpdatedBy(entityGroup.getUpdatedBy());
            EntityGroupMapper.toUpdateEntityGroup(entityGroupData, entityGroup);
            return entityGroupRepository.save(entityGroupData);
        }
        return null;
    }

    private boolean ifExist(EntityGroup entityGroup) {
        EntityGroup entityGroupExist = entityGroupRepository.findByEntityRoleAndUserGroupAndIsDeleted(
                findEntityRoleById(entityGroup.getEntityRole().getId()),
                userGroupService.findById(entityGroup.getUserGroup().getId()), false);
        return entityGroupExist != null;
    }

    private EntityRole findEntityRoleById(Long id) {
        EntityRole entityRole = entityRoleRepository.findById(id).orElseThrow(() -> new NotFoundException(EntityRole.class, id));
        entityRole.getEntity().setOrganization(null);
        entityRole.getEntity().setUserLevelPrivileges(null);
        entityRole.getFunctionalRoles().setEntityRoles(null);
        return entityRole;
    }

    private EntityGroup checkForValidEntityRoleAndUserGroup(EntityGroup entityGroup) {
        EntityRole entityRole = findEntityRoleById(entityGroup.getEntityRole().getId());
        UserGroup userGroup = userGroupService.findById(entityGroup.getUserGroup().getId());
        if (entityRole != null) {
            entityGroup.setCreatedBy(entityGroup.getEntityRole().getCreatedBy());
            entityGroup.setEntityRole(entityRole);
            entityGroup.setUserGroup(userGroup);
            entityGroup.setIsDeleted(false);
            return entityGroup;
        } else {
            throw new NotFoundException(EntityRole.class, entityGroup.getEntityRole().getId());
        }
    }

    private UserGroup getDefaultUserGroup() {
        return userGroupService.findById(1L);
    }

    @Override
    public EntityGroup findById(Long id) {
        return entityGroupRepository.findById(id).orElseThrow(() -> new NotFoundException(EntityGroup.class, id));
    }

    @Override
    public List<EntityGroup> findByStatus(boolean status) {
        return entityGroupRepository.findByStatus(status);
    }

    @Override
    public List<EntityGroup> findByUserGroup(Long id) {
        return entityGroupRepository.findByUserGroupAndStatus(userGroupService.findById(id), true);
    }

    @Override
    public EntityGroup softDeleteResources(Long id, Boolean status) {
        EntityGroup entityGroup = findById(id);
        entityGroup.setIsDeleted(status);
        return addOrUpdate(entityGroup);
    }

    @Override
    public EntityGroup findByEntityRoleAndUserGroup(EntityRole entityRole, UserGroup userGroup) {
        return entityGroupRepository.findByEntityRoleAndUserGroupAndIsDeleted(entityRole, userGroup, false);
    }

    @Override
    public UserGroup findDefaultUserGroup() {
        return userGroupService.findById(1L);
    }


    @Override
    public List<UserGroup> addClosedGroupAndResources(String refType, String refId, String projectId, String
            groupName, List<EntityGroupDTO> entityGroupDTOList) {
        /*
            1- will create closed group of w/o under user group tbl
            2- enter data in entity group tbl (entity role id and group id)
            3- enter data in resource consumed tbl (entity group id and w/o id)
            group type = closed
       **/
        return entityGroupServiceImplExt.addClosedGroupAndResources(refType, refId, projectId, groupName, entityGroupDTOList);
    }

    @Override
    public List<UserGroup> findUserGroupByParentId(String parentId) {
        return userGroupService.findUserGroupByParentId(parentId);
    }

    @Override
    public Map addResourcesInTheGroup(String workOrderName, String refType, String refId, String
            projectId, List<EntityGroupDTO> entityGroupDTOList) {
        Map map = new HashMap();
        try {
            List<EntityGroup> entityGroups = toEntityGroups(entityGroupDTOList);
            List<UserGroup> userGroupList = new ArrayList<>();
            entityGroupRepository.saveAll(entityGroups);
            for (EntityGroup entityGroup : toEntityGroups(entityGroupDTOList)) {
                UserGroup userGroup = userGroupService.findById(entityGroup.getUserGroup().getId());
                if (userGroup != null) {
                    userGroupList.add(UserGroup.builder()
                            .parentId(projectId)
                            .refId(refId)
                            .refType(refType)
                            .status(true)
                            .createdBy("admin")
                            .build());
                } else {
                    addClosedGroupAndResources(refType, refId, projectId, workOrderName, entityGroupDTOList);
                }
                if (!userGroupList.isEmpty()) {
                    userGroupService.addAll(userGroupList);
                }
            }
            map.put("message", Message.MSG_SAVE_RESOURCES_SUCCESSFULLY.getMessage());
            map.put("code", HttpStatus.OK.value());
            map.put("date", null);
        } catch (Exception ex) {
            map.put("Message", Message.ERROR_SAVE_RESOURCES_FAILED.getMessage());
            map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            map.put("date", null);

        }
        return map;
    }

    @Override
    public EntityRole findByEntityRoleIdAndStatus(Long entityRoleId, boolean status) {
        return entityRoleRepository.findByIdAndStatus(entityRoleId, status);
    }

    @Override
    public EmployeeDetail findEmployeeDetailByEntityRoleId(Long entityRoleId) {
        return employeeDetailRepository.findByEntityId(entityRoleId);
    }

    @Override
    public UserGroup findByRefIdAndRefTypeAndStatus(String refId, String refType, boolean status) {
        return userGroupService.findByRefIdAndRefTypeAndStatus(refId, refType, status);
    }

    @Override
    public UserGroup findByRefIdAndStatus(String refId, boolean status) {
        return userGroupService.findByRefIdAndStatus(refId, status);
    }

    @Override
    public List<DefaultUserGroupResponseDTO> findByRefIdAndRefTypeAndStatusToTile(String refId, String refType,
                                                                                  boolean status, String isExist) {
        return null;
    }

    @Override
    public UserGroup findUserGroupById(Long userGroupId) {
        return userGroupService.findById(userGroupId);
    }

    @Override
    public UserGroup saveUserGroup(UserGroup userGroup) {
        return userGroupService.addOrUpdate(userGroup);
    }

    @Override
    public ObjectNode saveResources(UserGroup userGroup, List<Long> entityRoleIds) {
        ObjectNode response = new ObjectMapper().createObjectNode();
//        deleteEntityGroup(userGroup.getId(), entityRoleIds);
        entityRoleIds.forEach(id -> {
            EntityRole entityRole = findEntityRoleById(id);
            if (entityRole != null && entityRole.getEntity().getEntityType().equalsIgnoreCase("Employee")) {
                EntityGroup entityGroupExist = entityGroupRepository.findByEntityRoleAndUserGroupAndIsDeleted(entityRole, userGroup, false);
                addOrUpdate(EntityGroup.builder()
                        .id(entityGroupExist != null ? entityGroupExist.getId() : null)
                        .userGroup(userGroup)
                        .entityRole(entityRole)
                        .updatedBy(userGroup.getUpdatedBy())
                        .createdBy(userGroup.getCreatedBy())
                        .status(true).build());
            }
        });
        return response.put("message", "Resources Added!");
    }

    private void deleteEntityGroup(Long id, List<Long> entityRoleIds) {
        List<EntityGroup> deletedRoles = entityGroupRepository.findAllByEntityGroupIdAndIsDeleted(id, false, entityRoleIds);
        List<Long> deleteRoleIds = deletedRoles.stream().map(dr -> dr.getId()).collect(Collectors.toList());

        if (deleteRoleIds.size() > 0) {
            deletedRoles.stream().forEach(delRole -> {
                delRole.setIsDeleted(true);
                delRole.setStatus(false);
            });
            entityGroupRepository.saveAll(deletedRoles);
        }
        List<EntityGroup> deletedRolesUpdate = entityGroupRepository.findAllByEntityGroupIdAndIsDeletedTrue(id, true, entityRoleIds);
        List<Long> deleteRoleUpdateIds = deletedRolesUpdate.stream().map(dr -> dr.getId()).collect(Collectors.toList());
        if (deleteRoleUpdateIds.size() > 0) {
            deletedRolesUpdate.stream().forEach(delRole -> {
                delRole.setIsDeleted(false);
                delRole.setStatus(true);
            });
            entityGroupRepository.saveAll(deletedRolesUpdate);
        }


    }

    @Override
    public List<EntityGroup> findAllByEntityGroupIdAndIsDeleted(Long id, Boolean isDeleted, List<Long> entityRoleIds) {
        return entityGroupRepository.findAllByEntityGroupIdAndIsDeleted(id, isDeleted, entityRoleIds);
    }

    @Override
    public List<EntityGroup> findAllByEntityGroupIdAndIsDeletedTrue(Long id, Boolean isDeleted, List<Long> entityRoleIds) {
        return entityGroupRepository.findAllByEntityGroupIdAndIsDeletedTrue(id, isDeleted, entityRoleIds);
    }

    @Override
    public List<EntityGroup> getUnattachedEntityGroup(String refId, String refType) {
        List<EntityGroup> parentResources = null;
        List<EntityGroup> childResources = null;

        ArrayList<EntityGroup> uniqueElementsList = new ArrayList();
        try {

            UserGroup parentGroup = userGroupService.findByRefIdAndRefTypeAndStatus(refId, refType, true);
            parentResources = findByUserGroup(parentGroup.getId());

            List<UserGroup> userGroupList = userGroupService.findUserGroupByParentId(parentGroup.getId().toString());
            List<Long> srcNos = userGroupList.stream().map(UserGroup::getId).collect(Collectors.toList());
            childResources = entityGroupRepository.findByUserGroupIds(srcNos);

            for (EntityGroup entityGroup : parentResources) {

                Long sameResourcesCount = childResources.stream().filter(entityGroup1 -> entityGroup1.getEntityRole().getId() == entityGroup.getEntityRole().getId()).count();
                if (sameResourcesCount == 0L)
                    uniqueElementsList.add(entityGroup);

            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return uniqueElementsList;
    }

    /**
     * Description: Method to find list of entity group by entity role ids and user group id
     * Created By: Ibtehaj
     *
     * @param entityRoleIds
     * @param groupId
     * @return
     */
    @Override
    public List<EntityGroup> findByEntityRolesAndUserGroup(List<Long> entityRoleIds, Long groupId) {
        return entityGroupRepository.findByEntityRolesAndUserGroup(entityRoleIds, groupId);
    }

    /**
     * Description: Method to saving list of entity groups
     * Created By: Ibtehaj
     *
     * @param entityGroups
     * @return
     */
    @Override
    public List<EntityGroup> saveAll(List<EntityGroup> entityGroups) {
        return entityGroupRepository.saveAll(entityGroups);
    }

    @Override
    public Integer userGroupCountByEntityId(Long entityId, Boolean isDeleted, Boolean status) {
        return entityGroupRepository.findUserGroupCountByEntityId(entityId, isDeleted, status);
    }

    public Integer userGroupCountByFunctionalRoleId(Long functionalRoleId, Long entityId, Boolean status) {
        return entityGroupRepository.findUserGroupCountByFunctionalRoleId(functionalRoleId, entityId, status);
    }

    @Override
    public List<EntityGroup> findByEntityRoleId(Long entityId, Long functionalRoleId) {
        return entityGroupRepository.findByEntityRoleId(entityId, functionalRoleId);
    }

    @Override
    public Integer getResourceCountByRefId(String refId) {
        return entityGroupRepository.getResourceCountByRefId(refId, true, true, false);
    }

    @Override
    public List<ProjectManagementEntityTile> getResourceByRefIdAndEntityId(List<String> refIds, Long entityId) {
        return entityGroupRepository.getResourceByRefIdAndEntityId(refIds, true, true, false, entityId);
    }

    @Override
    public List<EntityGroup> findAllByUserGroup(UserGroup userGroup) {
        return entityGroupRepository.findAllByUserGroup(userGroup, true, false);
    }
}
