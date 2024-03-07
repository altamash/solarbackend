//package com.solar.api.tenant.service.userGroup;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.solar.api.exception.NotFoundException;
//import com.solar.api.tenant.mapper.contract.EntityDTO;
//import com.solar.api.tenant.mapper.extended.FunctionalRolesDTO;
//import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
//import com.solar.api.tenant.mapper.user.userGroup.UserGroupMapper;
//import com.solar.api.tenant.model.contract.Entity;
//import com.solar.api.tenant.model.extended.project.EmployeeDetail;
//import com.solar.api.tenant.model.userGroup.EntityRole;
//import com.solar.api.tenant.repository.UserGroup.DefaultUserGroupRepository;
//import com.solar.api.tenant.service.extended.project.EmployeeManagementService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.solar.api.tenant.mapper.contract.EntityMapper.*;
//import static com.solar.api.tenant.mapper.extended.FunctionalRolesMapper.*;
//
//@Service
//public class DefaultUserGroupImpl implements DefaultUserGroupService {
//
//    @Autowired
//    DefaultUserGroupRepository defaultUserGroupRepository;
//
//    @Autowired
//    EmployeeManagementService employeeManagementService;
//
////    @Autowired
////    EntityService entityService;
////
////    @Autowired
////    EmployeeDetailService employeeDetailService;
////
////
////    @Autowired
////    FunctionalRolesService functionalRolesService;
//
//    @Override
//    public ObjectNode addOrUpdate(EntityRole entityRole) {
//        ObjectNode response = new ObjectMapper().createObjectNode();
//        if (entityRole.getId() != null) {
//            EntityRole entityRoleData = findById(entityRole.getId());
//            if (entityRoleData == null) {
//                throw new NotFoundException(EntityRole.class, entityRole.getId());
//            }
//            entityRoleData = UserGroupMapper.toUpdateEntityRole(entityRoleData,
//                    entityRole);
//
//            defaultUserGroupRepository.save(entityRoleData);
//            response.put("message", "Updated");
//            return response;
//        }
//        defaultUserGroupRepository.save(checkForValidEntityAndRole(entityRole));
//        response.put("message", "Saved");
//        return response;
//    }
//
//    @Override
//    public List<DefaultUserGroupResponseDTO> updateProjectId(List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList, String projectId) {
//        List<EntityRole> entityRoles =
//                defaultUserGroupRepository.findAllByIdIn(defaultUserGroupResponseDTOList.stream()
//                        .map(DefaultUserGroupResponseDTO::getDefaultUserGroupId)
//                        .collect(Collectors.toList()));
//        entityRoles.forEach(dug -> {
////            dug.setProjectId(projectId);
////            dug.setLevelId(projectId);
//            addOrUpdate(dug);
//        });
//
//        return defaultUserGroupResponseDTOList;
//    }
//
//    @Override
//    public List<DefaultUserGroupResponseDTO> updateTaskId(List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList, String taskId) {
//        List<EntityRole> entityRoles =
//                defaultUserGroupRepository.findAllByIdIn(defaultUserGroupResponseDTOList.stream()
//                        .map(DefaultUserGroupResponseDTO::getDefaultUserGroupId)
//                        .collect(Collectors.toList()));
//        entityRoles.forEach(dug -> {
////            dug.setLevel("task");
////            dug.setLevelId(taskId);
////            dug.setStatus("Assigned");
//            addOrUpdate(dug);
//        });
//
//        return defaultUserGroupResponseDTOList;
//    }
//
//    @Override
//    public EntityRole checkForValidEntityAndRole(EntityRole entityRole) {
//        Entity entity = employeeManagementService.findByIdAndEntityType(entityRole.getEntityId(), "Employee");
//        if (entity != null) {
//            entityRole.setEntity(entity);
//            entityRole.setFunctionalRoles(employeeManagementService.findFunctionalRolesById(entityRole.getFunctionalRoleId()));
//            entityRole.getEntity().setOrganization(null);
//            entityRole.getEntity().setUserLevelPrivileges(null);
//            return entityRole;
//        } else {
//            throw new NotFoundException(Entity.class, entityRole.getEntityId());
//        }
//    }
//
//    @Override
//    public EntityRole findById(Long id) {
//        return defaultUserGroupRepository.findById(id).orElseThrow(() -> new NotFoundException(EntityRole.class, id));
//    }
//
//    @Override
//    public List<DefaultUserGroupResponseDTO> findByLevelAndLevelId(String level, String levelId) {
//        List<EntityRole> entityRoles = defaultUserGroupRepository.findByLevelAndLevelId(level, levelId);
//        return getResponseDTO(entityRoles);
//    }
//
//    @Override
//    public List<DefaultUserGroupResponseDTO> findByLevelAndLevelIdAndStatus(String level, String levelId, String status) {
//        List<EntityRole> entityRoles = defaultUserGroupRepository.findByLevelAndLevelIdAndStatus(level, levelId, status);
//        return getResponseDTO(entityRoles);
//    }
//
//    @Override
//    public EntityDTO getEntity(EntityRole entityRole) {
//        return toEntityDTO(employeeManagementService.findByIdAndEntityType(entityRole.getEntityId() == null ?
//                entityRole.getEntity().getId() : entityRole.getEntityId(), "Employee"));
//    }
//
//    @Override
//    public EmployeeDetail findEmployeeDetailByEntityId(Long id) {
//        return employeeManagementService.findByEntityId(id);
//    }
//
//    @Override
//    public FunctionalRolesDTO getRoles(EntityRole entityRole) {
//        return toFunctionalRolesDTO(
//                employeeManagementService.findFunctionalRolesById(entityRole.getFunctionalRoleId() == null ?
//                        entityRole.getFunctionalRoles().getId() : entityRole.getFunctionalRoleId()));
//    }
//
//    @Override
//    public List<EntityRole> findAll() {
//        List<EntityRole> entityRoles = defaultUserGroupRepository.findAll();
//        for (EntityRole entityRole : entityRoles) {
//            if (entityRole.getEntityId() != null) {
//                entityRole.setEntity(toEntity(getEntity(entityRole)));
//                entityRole.getEntity().setOrganization(null);
//                entityRole.getEntity().setUserLevelPrivileges(null);
//            }
//            if (entityRole.getFunctionalRoleId() != null) {
//                entityRole.setFunctionalRoles(toFunctionalRoles(getRoles(entityRole)));
//            }
//        }
//
//        return entityRoles;
//    }
//
//    @Override
//    public List<DefaultUserGroupResponseDTO> findExistingResourcesByGroups() {
//        List<EntityRole> entityRoles = defaultUserGroupRepository.findAll();
//        return getResponseDTO(entityRoles);
//    }
//
//    private List<DefaultUserGroupResponseDTO> getResponseDTO(List<EntityRole> entityRoles) {
//        List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList = new ArrayList<>();
//        entityRoles.forEach(dug -> {
//            EmployeeDetail employeeDetail = findEmployeeDetailByEntityId(
//                    dug.getEntityId() == null ? dug.getEntity().getId() : dug.getEntityId());
////            if (dug.getProjectId() != null) {
////                defaultUserGroupResponseDTOList.add(getValues(dug, employeeDetail));
////            } else {
////                defaultUserGroupResponseDTOList.add(getValues(dug, employeeDetail));
////            }
//        });
//        return defaultUserGroupResponseDTOList;
//    }
//
//    private DefaultUserGroupResponseDTO getValues(EntityRole dug, EmployeeDetail employeeDetail) {
//        return DefaultUserGroupResponseDTO.builder()
//                .defaultUserGroupId(dug.getId())
//                .projectRoleId(dug.getFunctionalRoleId() == null ? dug.getFunctionalRoles().getId() : dug.getFunctionalRoleId())
//                .entityId(dug.getEntityId() == null ? dug.getEntity().getId() : dug.getEntityId())
//                .employeeName(dug.getEntity().getEntityName() == null ? null : dug.getEntity().getEntityName())
//                .employeeEmail(dug.getEntity().getContactPersonEmail() == null ? null : dug.getEntity().getContactPersonEmail())
//                .employeePhone(dug.getEntity().getContactPersonPhone() == null ? null : dug.getEntity().getContactPersonPhone())
//                .employeeDesignation(employeeDetail == null ? null :
//                        employeeDetail.getDesignation() == null ? null : employeeDetail.getDesignation())
////                .employmentType(dug.getStatus())
////                .status(dug.getStatus())
//                .employeeJoiningDate(employeeDetail == null ? null :
//                        employeeDetail.getDateOfJoining() == null ? null : employeeDetail.getDateOfJoining())
////                .taskId(dug.getLevel() == null ? null : dug.getLevel().equals("task") ? dug.getLevelId() : null)
//                .build();
//    }
//
//    @Override
//    public List<DefaultUserGroupResponseDTO> getDefaultUserGroupListByProject(String projectId) {
//        List<EntityRole> entityRoles = defaultUserGroupRepository.findByProjectId(projectId);
//        return getResponseDTO(entityRoles);
//    }
//
//    @Override
//    public List<EntityRole> findAllByIdIn(List<Long> ids) {
//        return defaultUserGroupRepository.findAllByIdIn(ids);
//    }
//}
