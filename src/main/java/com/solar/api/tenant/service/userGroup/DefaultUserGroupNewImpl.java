package com.solar.api.tenant.service.userGroup;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroup;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupDTO;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupMapper;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.repository.FunctionalRolesRepository;
import com.solar.api.tenant.repository.UserGroup.DefaultUserGroupRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultUserGroupNewImpl implements DefaultUserGroupNewService {

    @Autowired
    private DefaultUserGroupRepository defaultUserGroupRepository;
    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private FunctionalRolesRepository functionalRolesRepository;

    @Override
    public ResponseEntity<Object> saveOrUpdate(List<DefaultUserGroupDTO> defaultUserGroupDTOList) {
        List<DefaultUserGroup> defaultUserGroupsToSave = new ArrayList<>();
        List<String> entityIds = new ArrayList<>();
        String entitiesAdded = null;
        if (defaultUserGroupDTOList.size() > 0) {
            List<DefaultUserGroup> defaultUserGroupList = DefaultUserGroupMapper.toDefaultUserGroups(defaultUserGroupDTOList);
            for (DefaultUserGroup defaultUserGroup : defaultUserGroupList) {
                try {
                    if (defaultUserGroup.getId() == null) {
                        if (checkForValidEntityAndRole(defaultUserGroup) != null) {
                            defaultUserGroupsToSave.add(defaultUserGroup);
                            entityIds.add(String.valueOf(defaultUserGroup.getEntity().getId()));
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            if (defaultUserGroupsToSave.size() > 0) {
                defaultUserGroupRepository.saveAll(defaultUserGroupsToSave);
                entitiesAdded = String.join(",", entityIds);
            }
        }
        return new ResponseEntity<>(APIResponse.builder().message("Entities " +entitiesAdded+ " added in default user group successfully.")
                .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
    }

    private DefaultUserGroup checkForValidEntityAndRole(DefaultUserGroup defaultUserGroup) {
        Optional<Entity> entity = entityRepository.findById(defaultUserGroup.getFkEntityId());
        if (entity.isPresent()) {
            defaultUserGroup.setEntity(entity.get());
                Optional<FunctionalRoles> functionalRoles = functionalRolesRepository.findById(defaultUserGroup.getFkFunctionRoleId());
                if (!functionalRoles.isPresent()) {
                    throw new NotFoundException(FunctionalRoles.class, "Functional Roles Id: " +defaultUserGroup.getFkFunctionRoleId()+ " not found.");
                }
            defaultUserGroup.setFunctionalRoles(functionalRoles.get());
        } else {
            throw new NotFoundException(Entity.class, "Entity Id: " +defaultUserGroup.getFkEntityId()+ " not found.");
        }
        return defaultUserGroup;
    }
}
