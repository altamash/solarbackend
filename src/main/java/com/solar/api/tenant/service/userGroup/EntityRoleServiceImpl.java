package com.solar.api.tenant.service.userGroup;

import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.tenant.mapper.EntityRoleResponseDTO;
import com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityRoleMapper;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.model.userGroup.UserGroup;
import com.solar.api.tenant.repository.FunctionalRolesRepository;
import com.solar.api.tenant.repository.UserGroup.EntityRoleRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.repository.project.EmployeeDetailRepository;
import com.solar.api.tenant.service.extended.FunctionalRolesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.solar.api.tenant.service.userGroup.ResourceServiceImpl.getDefaultUserGroupResponseDTO;

@Service
public class EntityRoleServiceImpl implements EntityRoleService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityRoleRepository entityRoleRepository;
    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private FunctionalRolesRepository functionalRolesRepository;
    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    @Autowired
    private EntityGroupService entityGroupService;

    @Autowired
    private FunctionalRolesService functionalRolesService;

    @Override
    public EntityRole addOrUpdate(EntityRole entityRole) {
        if (entityRole.getId() != null) {
            EntityRole entityRoleData = findById(entityRole.getId());
            if (entityRoleData == null) {
                throw new NotFoundException(CompanyPreference.class, entityRole.getId());
            }
            entityRoleData = EntityRoleMapper.toUpdateEntityRole(entityRoleData, entityRole);
            return entityRoleRepository.save(entityRoleData);
        }
        entityRole = entityRoleRepository.save(checkForValidEntityAndRole(entityRole));
        addToDefaultUserGroup(entityRole);
        return entityRole;
    }

    private void addToDefaultUserGroup(EntityRole entityRole) {
        entityGroupService.addOrUpdate(EntityGroup.builder()
                .entityRole(entityRole)
                .status(true)
                .userGroup(entityGroupService.findDefaultUserGroup())
                .build());
    }

    private EntityRole checkForValidEntityAndRole(EntityRole entityRole) {
        Entity entity = entityRepository.findByIdAndEntityType(entityRole.getEntity().getId(), "Employee");
        if (entity != null) {
            EntityRole entityRoleExist = entityRoleRepository.findByEntityIdAndFunctionRoleIdAndIsDeleted(entityRole.getEntity().getId(), entityRole.getFunctionalRoles().getId());
            if (entityRoleExist == null) {
                entityRole.setEntity(entity);
                entityRole.setFunctionalRoles(functionalRolesRepository.findById(entityRole.getFunctionalRoles().getId())
                        .orElseThrow(() -> new NotFoundException(FunctionalRoles.class, entityRole.getFunctionalRoles().getId())));
                entityRole.getEntity().setOrganization(null);
                entityRole.getEntity().setUserLevelPrivileges(null);
                entityRole.getEntity().setEntityRoles(null);
                return entityRole;
            } else {
                throw new NotFoundException(EntityRole.class, "entity id " + entityRole.getEntity().getId() + " already exist with functional role " + entityRole.getId());
            }
        } else {
            throw new NotFoundException(Entity.class, entityRole.getEntity().getId());
        }
    }

    @Override
    public EntityRole findById(Long id) {
        EntityRole entityRole = entityRoleRepository.findById(id).orElseThrow(() -> new NotFoundException(EntityRole.class, id));
        entityRole.getEntity().setOrganization(null);
        entityRole.getEntity().setUserLevelPrivileges(null);
        entityRole.getFunctionalRoles().setEntityRoles(null);
        return entityRole;
    }

    @Override
    public List<EntityRole> findByEntity(Long id) {
        return entityRoleRepository.findByEntity(entityRepository.findById(id).orElseThrow(() -> new NotFoundException(Entity.class, id)));
    }

    /**
     * @param status
     * @return List<DefaultUserGroupResponseDTO>
     * @author Shariq
     * <p>
     * Getting data from defaultUserGroup (UserGroup : Id = 1)
     * Fetching list of EntityRoles
     * Getting active EntityRoles within the list of EntityRoles
     * <p>
     * Since : 30th Nov, 2022
     */
    @Override
    public List<DefaultUserGroupResponseDTO> findByStatus(boolean status, String isExist) {
        List<EntityGroup> entityGroups = entityGroupService.findByUserGroup(1L);
        return getResponseDTO(entityGroups, status, isExist);
    }

    @Override
    public EntityRole findEntityRoleByIdAndStatus(Long entityRoleId, boolean status) {
        return entityRoleRepository.findByIdAndStatus(entityRoleId, status);
    }

    private List<DefaultUserGroupResponseDTO> getResponseDTO(List<EntityGroup> entityGroupsDefault, boolean status, String isExist) {
        List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList = new ArrayList<>();
        UserGroup userGroup = entityGroupService.findByRefIdAndStatus(isExist, status);
//        if (isExist.equalsIgnoreCase("null") ) {
//            userGroup = entityGroupService.findByRefIdIsNullAndStatus(status);
//        }
//        else {
//            userGroup = entityGroupService.findByRefIdAndStatus(isExist, status);
//        }
        if (userGroup != null) {
            return getValidValues(entityGroupService.findByUserGroup(userGroup.getId()), entityGroupsDefault, status, defaultUserGroupResponseDTOList);
        } else {
            return getValidValues(null, entityGroupsDefault, status, defaultUserGroupResponseDTOList);
        }
    }

    private List<DefaultUserGroupResponseDTO> getValidValues(List<EntityGroup> entityGroupsExists, List<EntityGroup> entityGroupsDefault, boolean status,
                                                             List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList) {
        entityGroupsDefault.forEach(egDefault -> {
            try {
                if (entityGroupsExists != null && !entityGroupsExists.isEmpty()) {
                    if (entityGroupsExists.stream()
                            .filter(entityGroup -> egDefault.getEntityRole().getId().equals(entityGroup.getEntityRole().getId()))
                            .findFirst().orElse(null) == null) {
                        EmployeeDetail employeeDetail = getEmployeeDetail(getEntityRole(egDefault.getEntityRole().getId(), status).getEntity().getId());
                        if (employeeDetail != null)
                            defaultUserGroupResponseDTOList.add(getValues(egDefault, egDefault.getEntityRole(), employeeDetail));
                    }
                } else {
                    /* Shariq Old Code Stopped dtd: 2023-02-06 By Sana with coordination Hussain.
                    EmployeeDetail employeeDetail = getEmployeeDetail( getEntityRole(egDefault.getEntityRole().getId(), status).getEntity().getId() );
                    if (employeeDetail != null)    defaultUserGroupResponseDTOList.add(getValues(egDefault, egDefault.getEntityRole(), employeeDetail));*/
                    /** * This block added by dtd: 2023-02-06 By Sana with coordination Hussain. */
                    EmployeeDetail employeeDetail = null;
                    if (egDefault.getEntityRole() != null) {
                        EntityRole entityRole = getEntityRole(egDefault.getEntityRole().getId(), status);
                        if (entityRole != null) {
                            Entity entity = entityRole.getEntity();
                            if (entity != null) {
                                employeeDetail = getEmployeeDetail(entity.getId());
                            }
                        }
                        if (employeeDetail != null)
                            defaultUserGroupResponseDTOList.add(getValues(egDefault, egDefault.getEntityRole(), employeeDetail));
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
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

    private DefaultUserGroupResponseDTO getValues(EntityGroup entityGroup, EntityRole entityRole, EmployeeDetail
            employeeDetail) {
        return getDefaultUserGroupResponseDTO(entityGroup, entityRole, employeeDetail);
    }

    @Override
    public List<DefaultUserGroupResponseDTO> findByDefaultUserGroup(Long userGroupId) {
        return null;
    }

    @Override
    public EmployeeDetail findEmployeeDetailByEntityId(Long id) {
        return employeeDetailRepository.findByEntityId(id);
    }

    @Override
    public List<EntityRole> findAllByEntityId(Long entityId) {
        return entityRoleRepository.findAllByEntityId(entityId);
    }

    @Override
    public List<EntityRole> findAll() {
        return entityRoleRepository.findAll();
    }

    @Override
    public List<EntityFunctionalRoleTile> findAllByFunctionRoleId(Long roleId) {
        return entityRoleRepository.findByFunctionalRoleId(roleId);
    }

    /**
     * Description: Method to find all entity roles for EntityRoleResponseDTO
     * Created By: Ibtehaj
     *
     * @return
     */
    @Override
    public List<EntityRoleResponseDTO> getAllEntityRole() {
        return entityRoleRepository.getAllEntityRole();
    }

    @Override
    public Map<String, String> addOrUpdate(List<EntityRole> entityRoles) {
        Map<String, String> response = new HashMap<>();
        try {
            entityRoles.forEach(this::addOrUpdate);
            response.put("code", HttpStatus.OK.value() + "");
            response.put("data", null);
            response.put("message", "Entity role created");
        } catch (Exception e) {
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value() + "");
            response.put("message", "cannot create Entity Role!");
            throw new SolarApiException(e.getMessage());
        }
        return response;
    }

    /**
     * Description: Method to find entity role by using entity id and functional role id
     * Created By: Ibtehaj
     *
     * @param entityId
     * @param roleId
     * @return
     */
    @Override
    public EntityRole findByEntityIdAndFunctionRoleId(Long entityId, Long roleId) {
        return entityRoleRepository.findByEntityIdAndFunctionRoleId(entityId, roleId);
    }

    /**
     * Description: Method for saving list of entity roles
     * Created By: Ibtehaj
     *
     * @param entityRoles
     * @return
     */
    @Override
    public List<EntityRole> saveAll(List<EntityRole> entityRoles) {
        return entityRoleRepository.saveAll(entityRoles);
    }

    /**
     * Description: Method for finding list of entity roles using entity id and isDeleted
     * Created By: Ibtehaj
     *
     * @param entityId
     * @param isDeleted
     * @return
     */
    @Override
    public List<EntityRole> findAllByEntityIdAndIsDeleted(Long entityId, Boolean isDeleted, List<Long> existingRoleIds) {
        return entityRoleRepository.findAllByEntityIdAndIsDeleted(entityId, isDeleted, existingRoleIds);
    }

    @Override
    public Map<String, String> removeEntityRole(Long entityId, Long roleId) {
        Map<String, String> mapResponse = new HashMap<>();
        List<EntityGroup> entityGroups = entityGroupService.findByEntityRoleId(entityId, roleId);
        if (entityGroups.size() <= 1) {
            EntityRole entityRole = findByEntityIdAndFunctionRoleId(entityId, roleId);
            entityRole.setIsDeleted(true);
            entityRole.setStatus(false);
            addOrUpdate(entityRole);

            if (!entityGroups.isEmpty()) {
                EntityGroup entityGroup = entityGroups.get(0);
                entityGroup.setStatus(false);
                entityGroup.setIsDeleted(true);
                entityGroupService.addOrUpdate(entityGroup);
            }
            mapResponse.put("code", HttpStatus.OK.value() + "");
            mapResponse.put("data", null);
            mapResponse.put("message", "Entity Role Removed!");
        } else {
            mapResponse.put("code", HttpStatus.CONFLICT.value() + "");
            mapResponse.put("data", null);
            mapResponse.put("message", "Entity " + entityId + " with Role " + roleId + " Involved in Project/Work Order!");
        }
        return mapResponse;
    }

    public List<EntityRole> findAllByEntityIdAndIsDeleted(Long entityId, Boolean isDeleted) {
        return entityRoleRepository.findAllByEntityIdAndIsDeleted(entityId, isDeleted);
    }

    @Override
    public List<EntityFunctionalRoleTile> findAllByFunctionRoleName(String roleName) {
        List<String> rolesList = Arrays.asList(roleName.split(","));
        return entityRoleRepository.findByFunctionalRoleName(rolesList);
    }
}
