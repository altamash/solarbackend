package com.solar.api.tenant.service.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.EntityCountDTO;
import com.solar.api.tenant.mapper.contract.EntityMapper;
import com.solar.api.tenant.mapper.contract.EntityResponseDTO;
import com.solar.api.tenant.mapper.user.CustomerProfileDTO;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import com.solar.api.tenant.service.CustomerDetailService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.lookup.AttachmentFactory;
import com.solar.api.tenant.service.contract.lookup.ERefCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntityServiceImpl implements EntityService {
    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AttachmentFactory attachmentFactory;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityDetailRepository entityDetailRepository;

    @Autowired
    private CustomerDetailService customerDetailService;

    @Override
    public Entity add(Entity entity, Long organizationId, String refCode, List<MultipartFile> multipartFiles) throws URISyntaxException, IOException, StorageException {
        entity.setOrganization(organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException(Organization.class, organizationId)));
        entity = entityRepository.save(entity);
        if (!CollectionUtils.isEmpty(multipartFiles)) {
            attachmentFactory.doPostAttachment(refCode, multipartFiles, "tenant/", AppConstants.CONTRACT_FILE_PATH,
                    ERefCode.ENTITY.getRefCode(), String.valueOf(entity.getId()), entity);
        }
        return entity;
    }

    @Override
    public Entity update(Entity entity) {
        Entity entityData = findById(entity.getId());
        if (entityData == null) {
            throw new NotFoundException(Entity.class, entity.getId());
        }
        return entityRepository.save(EntityMapper.toUpdatedEntity(entityData,
                entity));
    }

    @Override
    public Entity findById(Long id) {
        return entityRepository.findById(id).orElseThrow(() -> new NotFoundException(Entity.class, id));
    }

    @Override
    public List<Entity> findAll() {
        return entityRepository.findAll();
    }

    @Override
    public Entity save(Entity entity) {
        //TODO Business email address never given and move to entity form userDTO object into entity - this check/test stopped. dtd: 2022-11-20 by Altamash.
        /**
         @Developer  : Altamash
         @Modified Dated : 2022-11-20 / 2:00 PK
         @Purpose : Business email address never given and move to entity form userDTO object into entity - this check/test stopped.
         */
        /*
        if (entity.getContactPersonEmail() == null) {
            throw new NotFoundException(Entity.class, "email", entity.getContactPersonEmail());
        } else if (isValidateEmail(entity.getContactPersonEmail())) {
            throw new AlreadyExistsException(Entity.class, "email", entity.getContactPersonEmail());
        }*/
        return entityRepository.save(entity);
    }

    @Override
    public Entity findEntityByUserId(Long userId) {
        User userData = userRepository.findById(userId).orElse(null);
        List<UserLevelPrivilege> privileges = userLevelPrivilegeService.UserLevelPrivilegeByUser(userData);
        // in the case of self registration we will have one entry only
        if (privileges != null && privileges.size() > 0) {
            UserLevelPrivilege userLevelPrivilege = privileges.stream().findFirst().get();
            return userLevelPrivilege.getEntity();
        }
        return null;
    }

    @Override
    public List<Entity> findByEntityName(String entityName) {
        return entityRepository.findByEntityName(entityName);
    }

    @Override
    public boolean isValidateEmail(String emailId) {
        if (emailId != null && entityRepository.findByContactPersonEmail(emailId) != null) {
            return true;
        }
        return false;
    }

    @Override
    public List<EntityResponseDTO> findAllByEntityType(String entityType) {
        return entityRepository.findAllByEntityTypeAndStatus(entityType, EUserStatus.ACTIVE.getStatus());
    }

    @Override
    public List<Entity> findActiveByEntityType(String entityType) {

        /**
         * False = Not Deleted
         * True = Not Deleted
         */
        return entityRepository.findAllByIsDeletedAndEntityType(false, entityType);
    }

    @Override
    public Entity findByIdAndEntityType(Long id, String entityType) {
        return entityRepository.findByIdAndEntityType(id, entityType);
    }

    @Override
    public Entity findByEmailAddressAndEntityType(String emailAddress, String entityType) {
        return entityRepository.findByContactPersonEmailAndEntityType(emailAddress, entityType);
    }

    /**
     * Description: Method to find Entity by using email address and entity type
     * Created By: Ibtehaj
     *
     * @param emailAddress
     * @param entityType
     * @param isDeleted
     * @return
     */
    @Override
    public Entity findByEmailAddressAndEntityTypeAndIsDeleted(String emailAddress, String entityType, Boolean isDeleted) {
        return entityRepository.findByContactPersonEmailAndEntityTypeAndIsDeleted(emailAddress, entityType, isDeleted);
    }

    //findAllByIsDeletedAndEntityTypeAndStatus
    public List<Entity> findByEntityTypeAndStatus(String entityType) {
        return entityRepository.findAllByIsDeletedAndEntityTypeAndStatus(false, entityType, EUserStatus.ACTIVE.getStatus());
    }

    @Override
    public boolean isValidateEmployeeEmail(String emailId) {
        if (emailId != null && entityRepository.findByContactPersonEmailAndEntityTypeAndIsDeleted(emailId, "Employee", false) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> isValidateCustomerEmail(String emailId) {
        Map<String, String> response = new HashMap<>();
        if (emailId != null && entityRepository.findByContactPersonEmailAndEntityTypeAndIsDeleted(emailId, "Customer", false) != null) {
            response.put("code", HttpStatus.OK.toString());
            response.put("data", "true");
            response.put("message", "email already exist");
            return response;
        }
        response.put("code", HttpStatus.NO_CONTENT.toString());
        response.put("data", "false");
        response.put("message", "email does not exist");
        return response;
    }

    @Override
    public Map<String, Object> getCustomerProfileByUserId(Long userId) {
        Map<String, Object> response = new HashMap<>();
        CustomerProfileDTO customerProfileDTO = null;
        String profileImage = null;
        String customerType = null;
        String customerState = null;
        EntityDetail entityDetail = null;
        User currentUser = userService.getLoggedInUser();
        if (currentUser.getUserType().getId() == 1) {
            User user = userService.findById(userId);
            Entity entity = findEntityByUserId(userId);
            entityDetail = entityDetailRepository.findByEntityId(entity.getId());
            CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
            if (customerDetail != null) {
                customerType = customerDetail.getCustomerType();//individual / commercial
                customerState = customerDetail.getStates(); //lead/ prospect
            }
            if (entityDetail != null) {
                profileImage = entityDetail.getUri() != null ? entityDetail.getUri() : null;
            }
            if (user != null && entity != null) {
                customerProfileDTO = CustomerProfileDTO.builder().entityId(entity.getId())
                        .acctId(user.getAcctId()).userName(user.getFirstName() + " " + user.getLastName()).firstName(user.getFirstName())
                        .lastName(user.getLastName()).customerType(customerType).customerState(customerState)
                        .dataOfBirth(user.getDataOfBirth()).phone(entity.getContactPersonPhone()).emailAddress(entity.getContactPersonEmail())
                        .generatedAt(user.getCreatedAt()).status(entity.getStatus()).profileUrl(profileImage).build();
                response.put("code", HttpStatus.OK.toString());
                response.put("data", customerProfileDTO);
                response.put("message", "user returned successfully");
                return response;
            } else {
                response.put("code", HttpStatus.NOT_FOUND);
                response.put("data", null);
                response.put("message", "no data found");
                return response;
            }
        }
        response.put("code", HttpStatus.NO_CONTENT);
        response.put("data", null);
        response.put("message", "not a customer user");
        return response;
    }

    @Override
    public EntityCountDTO countByEntityType() {
        return EntityCountDTO.builder()
                .residential(entityRepository.countByCustomerType("Individual"))
                .commercial(entityRepository.countByCustomerType("Commercial"))
                .build();
    }

    @Override
    public List<Long> findAllIdsByOrgId(List<Long> orgIds, String entityType, Boolean isDeleted) {
        return entityRepository.findAllIdsByOrgId(orgIds, entityType, isDeleted);
    }

    @Override
    public List<Entity> findByEmailAddressAndEntityTypeIn(String emailAddress, String entityType) {
        return entityRepository.findByContactPersonEmailAndEntityTypeIn(emailAddress, entityType);
    }

}
