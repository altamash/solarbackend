package com.solar.api.tenant.service.extended.project;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.contract.EntityDTO;
import com.solar.api.tenant.mapper.contract.EntityDetailDTO;
import com.solar.api.tenant.mapper.contract.EntityDetailMapper;
import com.solar.api.tenant.mapper.contract.EntityMapper;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper;
import com.solar.api.tenant.mapper.extended.project.EmployeeDetailDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeDetailMapper;
import com.solar.api.tenant.mapper.extended.project.EmployeeManagementDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.mapper.user.userGroup.EntityRoleDTO;
import com.solar.api.tenant.mapper.user.userGroup.EntityRoleMapper;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.EAuthenticationType;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.repository.UserGroup.EntityGroupRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.UserTypeService;
import com.solar.api.tenant.service.contract.EntityDetailService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.contract.OrganizationService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.extended.FunctionalRolesService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.userGroup.EntityGroupService;
import com.solar.api.tenant.service.userGroup.EntityRoleService;
import com.solar.api.tenant.service.userGroup.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class EmployeeManagementServiceImpl implements EmployeeManagementService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private EntityService entityService;

    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private FunctionalRolesService functionalRolesService;
    @Autowired
    private EmployeeDetailService employeeDetailService;
    @Autowired
    private PhysicalLocationService physicalLocationService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserService userService;
    @Autowired
    private EntityDetailService entityDetailService;
    @Autowired
    private EntityRoleService entityRoleService;
    @Autowired
    private EntityGroupService entityGroupService;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    @Autowired
    private EntityGroupRepository entityGroupRepository;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private EntityRepository entityRepository;


    /**
     * Description: Method for saving and updating employee
     * author: iraj
     * updated by: ibtehaj
     *
     * @param employeeManagementDTO
     * @param compKey
     * @param file
     * @return
     */
    @Override
    public EmployeeManagementDTO saveEmployeeManagement(EmployeeManagementDTO employeeManagementDTO, Long compKey, MultipartFile file, String createdBy) {
        Entity ent = EntityMapper.toEntity(employeeManagementDTO.getEntityDTO());
        Entity entity = null;
        if (fieldCheck(employeeManagementDTO)) {
            Organization organization = organizationService.findById(1L);
            EmployeeDetailDTO employeeDetailDTO = employeeManagementDTO.getEmployeeDetailDTO();
            UserDTO userDTO = employeeManagementDTO.getUserDTO();
            // by default organization 1
            ent.setOrganization(organization);
            entity = saveOrUpdateEntity(employeeManagementDTO, ent, organization);

            EmployeeDetail employeeDetailSaved = null;
            if (entity != null) {
                employeeDetailSaved = saveOrUpdateED(employeeManagementDTO, entity);
                if (file != null) {
                    try {
                        uploadToStorage(file, entity.getId(), compKey);
                    } catch (URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    } catch (StorageException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

                User savedUser = userService.createOrUpdateUserForEntity(entity, employeeDetailDTO, userDTO);
                UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.findByEntityIdAndAcctId(entity.getId(), savedUser.getAcctId());
                if (userLevelPrivilege == null) {
                    saveOrUpdateULP(entity, savedUser, organization);
                }
            }
            PhysicalLocation physicalLocation = null;
            if (entity != null && Objects.nonNull(employeeManagementDTO.getPhysicalLocationDTO())) {
                physicalLocation = saveOrUpdatePL(employeeManagementDTO, entity);
            }

            if (entity != null && Objects.nonNull(employeeManagementDTO.getEntityRoleDTOs())) {
                addEntityRoles(employeeManagementDTO.getEntityRoleDTOs(), entity, createdBy);
            }
        } else {
            throw new NotFoundException(EntityDTO.class, "EntityDTO/EmployeeDetailDTO is empty.");
        }
        return null;
    }

    private void addEntityRoles(List<EntityRoleDTO> entityRoleDTOS, Entity entity, String createdBy) {
        if (!entity.getEntityType().equalsIgnoreCase(EUserType.EMPLOYEE.getName())) {
            throw new IllegalStateException("User type must be Employee");
        }
        List<EntityRole> entityRoles = EntityRoleMapper.toEntityRoles(entityRoleDTOS);
        deleteEntityRoles(entityRoles, entity);
        // add entityRole
        entityRoles.forEach(er -> {
            EntityRole entityRole = entityRoleService.findByEntityIdAndFunctionRoleId(entity.getId(), er.getFunctionalRoles().getId());
            if (entityRole != null && (entityRole.getIsDeleted() || er.getId() == null)) {
                er.setId(entityRole.getId());
                er.setIsDeleted(false);
                er.setUpdatedBy(createdBy);
            } else {
                er.setCreatedBy(createdBy);
                er.setIsDeleted(false);
            }
            er.setEntity(entity);
            EntityRole entityRoleSave = entityRoleService.addOrUpdate(er);
            EntityGroup entityGroup = EntityGroup.builder()
                    .entityRole(entityRoleSave)
                    .userGroup(userGroupService.findById(1L))
                    .build();
            saveOrUpdateEntityGroup(entityGroup);

        });
    }

    @Override
    public List<EmployeeManagementDTO> findAllEmployee(String entityType) {
        return toEmployeeManagementDTOs(EntityMapper.toEntityDTOSkimmedList(entityService.findActiveByEntityType(entityType)));
    }

    private List<EmployeeManagementDTO> toEmployeeManagementDTOs(List<EntityDTO> entities) {
        List<EmployeeManagementDTO> employeeManagementDTOS = new ArrayList<>();
        if (entities.size() != 0) {
            List<Long> entIds = entities.stream().map(EntityDTO::getId).collect(Collectors.toList());
            List<EmployeeDetailDTO> employeeDetails = EmployeeDetailMapper.toEmployeeDetailDTOs(employeeDetailService.findAllByEntityIdIn(entIds));
            //List<PhysicalLocationDTO> physicalLocationDTOS = PhysicalLocationMapper.toPhysicalLocationDTOs(physicalLocationService.findAllByEntityIdIn(entIds));
            List<EntityDetail> details = entityDetailService.findAllByEntityIdIn(entIds);
            List<EntityDetailDTO> entityDetails = new ArrayList<>();
            if (details != null) {
                entityDetails = EntityDetailMapper.toEntityDetailDTOList(details);
            }

            for (EntityDTO ent : entities) {

                Optional<EmployeeDetailDTO> empDet = employeeDetails.stream()
                        .filter(empD -> empD.getEntityId().equals(ent.getId())).findFirst();

                Optional<EntityDetailDTO> entityDet = entityDetails.stream()
                        .filter(entDet -> entDet.getEntityDTO().getId().equals(ent.getId())).findFirst();

                List<EntityRole> entityRolesCount = entityRoleService.findAllByEntityId(ent.getId());
                employeeManagementDTOS.add(EmployeeManagementDTO.builder()
                        .entityDTO(ent)
                        .employeeDetailDTO(empDet.isPresent() ? empDet.get() : null)
                        .profileURL(entityDet.isPresent() ? entityDet.get().getUri() : null)
                        .roleCount(entityRolesCount != null ? entityRolesCount.stream().count() : 0)
                        //.userDTO()
                        //.physicalLocationDTO(physicalLocationDTOS != null ? physicalLocationDTOS.stream()
                        //      .filter(phy -> phy.getEntityId().equals(ent.getId())).findFirst().get() : null)
                        .build());
            }
        }
        return employeeManagementDTOS;
    }

    @Override
    public List<EmployeeManagementDTO> findActiveEmployees(String entityType) {
        return toEmployeeManagementDTOs(EntityMapper.toEntityDTOSkimmedList(entityService.findActiveByEntityType(entityType)));
    }

    @Override
    public EntityDetailDTO uploadToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException {
        return EntityDetailMapper.toEntityDetailDTO(entityDetailService.uploadToStorage(file, entityId, compKey));
    }

    @Override
    public EntityDetailDTO updateToStorage(MultipartFile file, Long entityId, Long compKey) throws URISyntaxException, IOException, StorageException {
        return EntityDetailMapper.toEntityDetailDTO(entityDetailService.updateToStorage(file, entityId, compKey));
    }

    @Override
    public EntityDetailDTO findByEntityId(Long entityId) {
        return EntityDetailMapper.toEntityDetailDTO(entityDetailService.findByEntityId(entityId));
    }

    @Override
    public FunctionalRoles findFunctionalRolesById(Long id) {
        return functionalRolesService.findFunctionalRolesById(id);
    }

    @Override
    public Entity findByIdAndEntityType(Long id, String entityType) {
        return entityService.findByIdAndEntityType(id, entityType);
    }

    @Override
    public Entity findByEntity(Long id) {
        return entityService.findById(id);
    }

    @Override
    public EmployeeDetail findByEmployeeDetailByEntityId(Long id) {
        return employeeDetailService.findByEntityId(id);
    }

    /**
     * Description: checking the entity group already exist or not
     * Created By: Ibtehaj
     *
     * @param entityGroup
     * @return
     */
    private boolean ifExist(EntityGroup entityGroup) {
        EntityGroup entityGroupExist = entityGroupRepository.findByEntityRoleAndUserGroupAndIsDeleted(
                entityRoleService.findById(entityGroup.getEntityRole().getId()),
                userGroupService.findById(entityGroup.getUserGroup().getId()), false);
        return entityGroupExist != null;
    }

    /**
     * Description: Method to return employee details
     * Created By: Ibtehaj
     *
     * @param entityId
     * @param entityType
     * @param response
     * @return
     */
    @Override
    public Map findEmployeeDetails(Long entityId, String entityType, Map response) {
        try {
            EntityDTO entityDTO = EntityMapper.toEntityDTOSkimmed(entityService.findByIdAndEntityType(entityId, entityType));
            response = toEmployeeManagementDTOMap(entityDTO, response);
        } catch (Exception e) {
            response = generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null);
        }
        return response;
    }

    /**
     * Method to enable or disable employee
     * Created By: Ibtehaj
     *
     * @param response
     * @param entityId
     * @param disabled
     * @return
     */
    @Override
    public Map disableEmployee(Map response, Long entityId, boolean disabled) {
        String message = "";
        try {
            Entity entity = entityService.findById(entityId);
            EmployeeDetail employeeDetail = employeeDetailService.findByEntityId(entityId);
            User user = userService.findUserByEntityId(entityId);
            if (entity != null && employeeDetail != null && user != null) {
                if (entity.getIsDeleted() != null && entity.getIsDeleted() == false) {
                    if (disabled) {
                        response = disableEmployee(response, entity, user, employeeDetail, disabled, message);
                    } else {
                        response = enableEmployee(response, entity, user, employeeDetail, disabled, message);
                    }
                } else {
                    message = "Deleted employees cannot be enabled / disabled";
                    response = generateResponseMap(response, HttpStatus.CONFLICT.toString(), message, null);
                }
            } else {
                message = "Employees details not found";
                response = generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), message, null);
            }
        } catch (Exception e) {
            response = generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null);
        }
        return response;
    }

    /**
     * Description: method to delete employee
     * Created By: Ibtehaj
     *
     * @param response
     * @param entityId
     * @return
     */
    @Override
    public Map deleteEmployee(Map response, Long entityId) {
        String message = "";
        try {
            Entity entity = entityService.findById(entityId);
            EmployeeDetail employeeDetail = employeeDetailService.findByEntityId(entityId);
            User user = userService.findUserByEntityId(entityId);
            if (entity != null && employeeDetail != null && user != null) {
                if (entity.getIsDeleted() != null && entity.getIsDeleted() == false) {
                    if (isDisableCheck(entity, user, employeeDetail)) {
                        Integer userGroupCount = entityGroupService.userGroupCountByEntityId(entity.getId(), false, true);
                        if (userGroupCount == 0) {
                            response = softDeleteEmployee(response, entity, message);
                        } else {
                            message = "Can not delete employee. Employee is part of projects/work order";
                            response = generateResponseMap(response, HttpStatus.CONFLICT.toString(), message, null);
                        }
                    } else {
                        message = "Please disable employee before deletion";
                        response = generateResponseMap(response, HttpStatus.CONFLICT.toString(), message, null);
                    }
                } else {
                    message = "Employee is already deleted";
                    response = generateResponseMap(response, HttpStatus.CONFLICT.toString(), message, null);
                }
            } else {
                message = "Employees details not found";
                response = generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), message, null);
            }
        } catch (Exception e) {
            response = generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null);
        }
        return response;
    }

    /**
     * Description: Method to delete employee after checking
     * Created By: Ibtehaj
     *
     * @param response
     * @param entity
     * @param message
     * @return
     */
    private Map softDeleteEmployee(Map response, Entity entity, String message) {
        try {
            entity.setIsDeleted(true);
            entityService.save(entity);
            message = "Successfully deleted employee ";
            response = generateResponseMap(response, HttpStatus.OK.toString(), message, null);

        } catch (Exception e) {
            response = generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null);
        }

        return response;
    }

    /**
     * Method to disable employee
     * Created By: Ibtehaj
     *
     * @param response
     * @param entity
     * @param user
     * @param employeeDetail
     * @param disabled
     * @param message
     * @return
     */
    private Map disableEmployee(Map response, Entity entity, User user, EmployeeDetail employeeDetail, boolean disabled, String message) {
        if (isActiveCheck(entity, user, employeeDetail)) {
            Integer userGroupCount = entityGroupService.userGroupCountByEntityId(entity.getId(), false, true);
            if (userGroupCount == 0) {
                updateStatus(entity, user, employeeDetail, disabled);
                message = "Employee successfully disabled";
                response = generateResponseMap(response, HttpStatus.OK.toString(), message, null);
            } else {
                message = "Can not disable employee. Employee is part of projects/work order";
                response = generateResponseMap(response, HttpStatus.CONFLICT.toString(), message, null);
            }
        } else {
            message = "Employee is already disabled";
            response = generateResponseMap(response, HttpStatus.CONFLICT.toString(), message, null);
        }
        return response;
    }

    /**
     * Method to enable employee
     * Created By: Ibtehaj
     *
     * @param response
     * @param entity
     * @param user
     * @param employeeDetail
     * @param disabled
     * @param message
     * @return
     */
    private Map enableEmployee(Map response, Entity entity, User user, EmployeeDetail employeeDetail, boolean disabled, String message) {
        if (isDisableCheck(entity, user, employeeDetail)) {
            updateStatus(entity, user, employeeDetail, disabled);
            message = "Employee successfully enabled";
            response = generateResponseMap(response, HttpStatus.OK.toString(), message, null);
        } else {
            message = "Employee is already enabled";
            response = generateResponseMap(response, HttpStatus.CONFLICT.toString(), message, null);
        }
        return response;
    }

    /**
     * Method to check if the employee is active
     * Created By: Ibtehaj
     *
     * @param entity
     * @param user
     * @param employeeDetail
     * @return
     */
    private boolean isActiveCheck(Entity entity, User user, EmployeeDetail employeeDetail) {
        boolean result = false;
        if (entity.getStatus() != null && entity.getStatus().equalsIgnoreCase(EUserStatus.ACTIVE.getStatus())
                && employeeDetail.getIsActive() != null && employeeDetail.getIsActive() == true) {
            result = true;
        }
        return result;
    }

    /**
     * Method to check if employee is in active
     * Created By: Ibtehaj
     *
     * @param entity
     * @param user
     * @param employeeDetail
     * @return
     */
    private boolean isDisableCheck(Entity entity, User user, EmployeeDetail employeeDetail) {
        boolean result = false;
        if (entity.getStatus() != null && entity.getStatus().equalsIgnoreCase(EUserStatus.INACTIVE.getStatus())
                && employeeDetail.getIsActive() != null && employeeDetail.getIsActive() == false) {
            result = true;
        }
        return result;
    }

    /**
     * Method to update status after enabling or disabling employee
     * Created By: Ibtehaj
     *
     * @param entity
     * @param user
     * @param employeeDetail
     * @param disable
     */
    private void updateStatus(Entity entity, User user, EmployeeDetail employeeDetail, boolean disable) {
        String status = disable ? EUserStatus.INACTIVE.getStatus() : EUserStatus.ACTIVE.getStatus();
        boolean isActive = disable ? false : true;
        String authentication = disable ? EAuthenticationType.NA.getName() : EAuthenticationType.STANDARD.getName();
        entity.setStatus(status);
        if (!user.getAuthentication().equalsIgnoreCase(EAuthenticationType.NA.getName()) && user.getPassword() != null) {
            user.setStatus(status);
            user.setAuthentication(authentication);
            userService.saveUser(user);
        }
        employeeDetail.setHasLogin(isActive);
        employeeDetail.setMobileAllowed(isActive);
        employeeDetail.setIsActive(isActive);
        entityService.save(entity);
        employeeDetailService.save(employeeDetail);
    }

    /**
     * Description: Method to return Map of employee details
     * Created By: Ibtehaj
     *
     * @param entityDTO
     * @param response
     * @return
     */
    private Map toEmployeeManagementDTOMap(EntityDTO entityDTO, Map response) {
        try {
            EmployeeDetailDTO employeeDetails = EmployeeDetailMapper.toEmployeeDetailDTO(employeeDetailService.findByEntityId(entityDTO.getId()));
            PhysicalLocationDTO physicalLocationDTO = PhysicalLocationMapper.toPhysicalLocationDTO(physicalLocationService.findByEntityId(entityDTO.getId()));
            EntityDetail details = entityDetailService.findByEntityId(entityDTO.getId());
            User user = userService.findUserByEntityId(entityDTO.getId());
            List<EntityRoleDTO> entityRoleDTO = EntityRoleMapper.toEntityRoleDTOs(entityRoleService.findAllByEntityIdAndIsDeleted(entityDTO.getId(), false));
            EntityDetailDTO entityDetail = details != null ? EntityDetailMapper.toEntityDetailDTO(details) : null;
            UserDTO userDTO = UserMapper.toUserDTO(user);
            userDTO.setPassword(null);
            EmployeeManagementDTO employeeManagementDTO = buildEmployeeManagementDTO(entityDTO, employeeDetails, physicalLocationDTO, entityRoleDTO, entityDetail, userDTO);
            String message = employeeManagementDTO == null ? AppConstants.DATA_NOT_FOUND : AppConstants.DATA_FOUND_SUCCESSFULLY;
            response = generateResponseMap(response, HttpStatus.OK.toString(), message, employeeManagementDTO);
        } catch (Exception e) {
            response = generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), null);
        }

        return response;

    }

    /**
     * Description: Method to generate response Map
     * Created By: Ibtehaj
     *
     * @param response
     * @param code
     * @param message
     * @param data
     * @return
     */
    private Map generateResponseMap(Map response, String code, String message, Object data) {

        response.put("data", data);
        response.put("message", message);
        response.put("code", code);
        return response;
    }

    /**
     * Description: Method to build EmployeeManagementDTO
     * Created By: Ibtehaj
     *
     * @param entityDTO
     * @param employeeDetails
     * @param physicalLocationDTO
     * @param entityRoleDTO
     * @param entityDetail
     * @return
     */
    private EmployeeManagementDTO buildEmployeeManagementDTO(EntityDTO entityDTO, EmployeeDetailDTO employeeDetails,
                                                             PhysicalLocationDTO physicalLocationDTO, List<EntityRoleDTO> entityRoleDTO, EntityDetailDTO entityDetail, UserDTO userDTO) {
        return EmployeeManagementDTO.builder()
                .entityDTO(entityDTO)
                .employeeDetailDTO(employeeDetails != null ? employeeDetails : null)
                .userDTO(userDTO != null ? userDTO : null)
                .physicalLocationDTO(physicalLocationDTO != null ? physicalLocationDTO : null)
                .entityRoleDTOs(entityRoleDTO)
                .profileURL(entityDetail != null ? entityDetail.getUri() : null)
                .roleCount(entityRoleDTO != null ? entityRoleDTO.stream().count() : 0)
                .build();
    }

    /**
     * Description: Method for saving or updating entity
     * Created By: Ibtehaj
     *
     * @param employeeManagementDTO
     * @param ent
     * @param organization
     * @return
     */
    private Entity saveOrUpdateEntity(EmployeeManagementDTO employeeManagementDTO, Entity ent, Organization organization) {
        ent.setEntityName(employeeManagementDTO.getEmployeeDetailDTO().getFirstName()
                .concat(" ").concat(employeeManagementDTO.getEmployeeDetailDTO().getLastName()));
        ent.setOrganization(organization);
        if (Boolean.TRUE.equals(employeeManagementDTO.getEmployeeDetailDTO().getIsActive())) {
            ent.setIsActive(true);
            ent.setStatus(EUserStatus.ACTIVE.getStatus());
        } else {
            ent.setIsActive(false);
            ent.setStatus(EUserStatus.INACTIVE.getStatus());
        }
        if (employeeManagementDTO.getEntityDTO().getId() != null) {
            Entity entityDb = entityService.findById(employeeManagementDTO.getEntityDTO().getId());
            ent = EntityMapper.toUpdatedEntity(entityDb, ent);
        } else {
            ent.setEntityType("Employee");
            ent.setIsDeleted(false);
            if (ent.getContactPersonEmail() != null && entityService.findByEmailAddressAndEntityTypeAndIsDeleted(ent.getContactPersonEmail(), ent.getEntityType(), false) != null) {
                throw new AlreadyExistsException("Email " + ent.getContactPersonEmail() + " is already taken");
            }
        }
        Entity saveEntity = entityService.save(ent);
        if(saveEntity.getOrganization()==null){
            saveEntity.setOrganization(organization);
            entityService.save(saveEntity);
        }
        return saveEntity;
    }

    /**
     * Description: Method for saving or updating employee details
     * Created By: Ibtehaj
     *
     * @param employeeManagementDTO
     * @param entity
     * @return
     */
    private EmployeeDetail saveOrUpdateED(EmployeeManagementDTO employeeManagementDTO, Entity entity) {
        employeeManagementDTO.getEmployeeDetailDTO().setEntityId(entity.getId());
        EmployeeDetail employeeDetailUpdated = EmployeeDetailMapper.toEmployeeDetail(employeeManagementDTO.getEmployeeDetailDTO());
        EmployeeDetail employeeDetailUpdatedDb = employeeDetailService.findByEntityId(entity.getId());
        if (employeeDetailUpdatedDb != null) {
            //EmployeeDetail employeeDetailDb = employeeDetailService.findById(employeeManagementDTO.getEmployeeDetailDTO().getId());
            employeeDetailUpdated = EmployeeDetailMapper.toUpdatedEmployeeDetail(employeeDetailUpdatedDb, employeeDetailUpdated);
        }
        return employeeDetailService.save(employeeDetailUpdated);
    }

    /**
     * Description: Method for saving or updating user level privilege
     * Created By: Ibtehaj
     *
     * @param entity
     * @param savedUser
     * @param organization
     * @return
     */
    private UserLevelPrivilege saveOrUpdateULP(Entity entity, User savedUser, Organization organization) {
        UserLevelPrivilege userLevelPrivilege = UserLevelPrivilege.builder()
                .entity(entity)
                .user(savedUser)
                .organization(organization).build();
        UserLevelPrivilege resultUserLevelPrivilege = userLevelPrivilegeService.save(userLevelPrivilege);
        return resultUserLevelPrivilege;
    }

    /**
     * Description: Method for saving or updating physical locaiton
     * Created By: Ibtehaj
     *
     * @param employeeManagementDTO
     * @param entity
     * @return
     */
    private PhysicalLocation saveOrUpdatePL(EmployeeManagementDTO employeeManagementDTO, Entity entity) {
        employeeManagementDTO.getPhysicalLocationDTO().setEntityId(entity.getId());
        employeeManagementDTO.getPhysicalLocationDTO().setContactPerson(
                employeeManagementDTO.getPhysicalLocationDTO().getContactPerson() == null ?
                        entity.getEntityName() : employeeManagementDTO.getPhysicalLocationDTO().getContactPerson());
        employeeManagementDTO.getPhysicalLocationDTO().setEmail(
                employeeManagementDTO.getPhysicalLocationDTO().getEmail() == null ?
                        entity.getContactPersonEmail() : employeeManagementDTO.getPhysicalLocationDTO().getEmail());
        PhysicalLocation physicalLocationUpdated = PhysicalLocationMapper.toPhysicalLocation(employeeManagementDTO.getPhysicalLocationDTO());
        Organization masterOrganization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId(Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE, true, null);
        physicalLocationUpdated.setOrganization(masterOrganization);
        PhysicalLocation physicalLocationUpdatedDB = physicalLocationService.findByEntityId(entity.getId());
        if (physicalLocationUpdatedDB != null) {
            physicalLocationUpdatedDB.setOrganization(masterOrganization);
            //PhysicalLocation physicalLocationDb = physicalLocationService.findById(employeeManagementDTO.getPhysicalLocationDTO().getId());
            physicalLocationUpdated = PhysicalLocationMapper.toUpdatedPhysicalLocation(physicalLocationUpdatedDB, physicalLocationUpdated);
        }
        return physicalLocationService.saveOrUpdate(physicalLocationUpdated);
    }

    /**
     * Description: Method for checking if the required fields have been filled
     * Created By: Ibtehaj
     *
     * @param employeeManagementDTO
     * @return
     */
    private boolean fieldCheck(EmployeeManagementDTO employeeManagementDTO) {
        boolean result = false;
        if ((employeeManagementDTO.getEntityDTO() != null && employeeManagementDTO.getEmployeeDetailDTO() != null)
                && (StringUtils.hasText(employeeManagementDTO.getEntityDTO().getContactPersonEmail())
                && StringUtils.hasText(employeeManagementDTO.getEntityDTO().getContactPersonPhone())
                && StringUtils.hasText(employeeManagementDTO.getEmployeeDetailDTO().getFirstName())
                && StringUtils.hasText(employeeManagementDTO.getEmployeeDetailDTO().getLastName())
                && StringUtils.hasText(employeeManagementDTO.getEmployeeDetailDTO().getDepartment())
                && StringUtils.hasText(employeeManagementDTO.getEmployeeDetailDTO().getHierarchyLevel())
                && Objects.nonNull(employeeManagementDTO.getEmployeeDetailDTO().getDateOfJoining())
                && StringUtils.hasText(employeeManagementDTO.getEmployeeDetailDTO().getEthnicity())
                && StringUtils.hasText(employeeManagementDTO.getEmployeeDetailDTO().getGender())
                && Objects.nonNull(employeeManagementDTO.getEmployeeDetailDTO().getDateOfBirth())
                && StringUtils.hasText(employeeManagementDTO.getEmployeeDetailDTO().getPersonalEmail())
                && StringUtils.hasText(employeeManagementDTO.getPhysicalLocationDTO().getAdd1()))) {
            result = true;
        }
        return result;
    }

    /**
     * Description: Method for soft deleting the entity group and entity roles that have been removed while updating employee
     * Created By: Ibtehaj
     *
     * @param entityRoles
     * @param entity
     */
    private void deleteEntityRoles(List<EntityRole> entityRoles, Entity entity) {
        List<EntityRole> entityRoleList = entityRoleService.findAllByEntityIdAndIsDeleted(entity.getId(), false);
        entityRoleList.removeAll(entityRoles);
        List<Long> deleteRoleIds = entityRoleList.stream().map(EntityRole::getId).collect(Collectors.toList());
        entityRoleList.forEach(delRole -> {
            delRole.setIsDeleted(true);
            delRole.setStatus(false);
        });
        if (!entityRoleList.isEmpty() && !deleteRoleIds.isEmpty()) {
            List<EntityGroup> entityGroups = entityGroupService.findByEntityRolesAndUserGroup(deleteRoleIds, 1L);
            entityGroups.forEach(eg -> {
                eg.setIsDeleted(true);
                eg.setStatus(false);
            });
            entityGroupService.saveAll(entityGroups);
            entityRoleService.saveAll(entityRoleList);
        }
    }

    /**
     * Description: Method for finding the existing entity group
     * Created By: Ibtehaj
     *
     * @param entityGroup
     * @return
     */
    private EntityGroup findEntityGroup(EntityGroup entityGroup) {
        EntityGroup entityGroupExist = entityGroupRepository.findByEntityRoleAndUserGroupAndIsDeleted(
                entityRoleService.findById(entityGroup.getEntityRole().getId()),
                userGroupService.findById(entityGroup.getUserGroup().getId()),false);
        return entityGroupExist;
    }

    /**
     * Description: Method for saving and updating entity group
     * Created By: Ibtehaj
     *
     * @param entityGroup
     * @return
     */
    private EntityGroup saveOrUpdateEntityGroup(EntityGroup entityGroup) {
        if (!ifExist(entityGroup)) {
            entityGroupService.addOrUpdate(entityGroup);
        } else {
            entityGroup = findEntityGroup(entityGroup);
            if (entityGroup.getIsDeleted() != null && entityGroup.getIsDeleted() == true) {
                entityGroup.setIsDeleted(false);
                entityGroup.setStatus(true);
            }
            entityGroupService.addOrUpdate(entityGroup);
        }
        return entityGroup;
    }
}
