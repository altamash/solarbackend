package com.solar.api.tenant.service.projectListing;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.projectListing.ManagerDTO;
import com.solar.api.tenant.mapper.projectListing.ProjectListingMapper;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import com.solar.api.tenant.service.RoleService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.ca.CaSoftCreditCheckMapper.toCaSoftCreditCheck;

@Service
public class ProjectListingServiceImpl implements ProjectListingService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private RoleService roleService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    @Autowired
    private  Utility utility;
    @Autowired
    private EntityDetailRepository entityDetailRepository;

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<?> getAllManagers() {
        try {
            Role roleManager = roleService.findByName(ERole.ROLE_MANAGER.name());
            List<ManagerDTO> managerDTOList = null;
            Set<User> users  =  new HashSet<>();
            if (roleManager != null) {
                List<User> dbUsers = userService.findByRoleName(roleManager.getName());
                if(dbUsers!= null && dbUsers.size() > 0) {
                    users.addAll(dbUsers);
                }
            } else {
                LOGGER.error("no role found");
                return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "no role found");
            }
            if (users != null && users.size() > 0) {
                managerDTOList = ProjectListingMapper.toManagerDTOList(users, roleManager);
                managerDTOList = setProfileImage(users, managerDTOList);
                return utility.buildSuccessResponse(HttpStatus.OK, "data returned successfully", managerDTOList);
            }
            LOGGER.error("no users found");
            return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "no data found");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return utility.buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private   List<ManagerDTO> setProfileImage(Set<User> users, List<ManagerDTO> managerDTOList){
        for(User user : users) {
            UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(user.getAcctId());
            if (userLevelPrivilege != null) {
                Entity entity = entityService.findById(userLevelPrivilege.getEntity().getId());
                if (entity != null) {
                    EntityDetail entityDetail = entityDetailRepository.findByEntityId(entity.getId());
                    if (entityDetail != null) {
                        managerDTOList = managerDTOList.stream().filter(sRep -> sRep.getAcctId().equals(user.getAcctId()))
                                .map(sRep -> {
                                    sRep.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
                                    return sRep;
                                }).collect(Collectors.toList());
                    }
                }
            }
        }
        return  managerDTOList;
    }
}
