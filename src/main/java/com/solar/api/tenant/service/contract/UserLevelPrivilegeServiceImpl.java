package com.solar.api.tenant.service.contract;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeWrapperDTO;
import com.solar.api.tenant.model.contract.*;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.repository.contract.ContractRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import com.solar.api.tenant.repository.contract.UserLevelPrivilegeRepository;
import com.solar.api.tenant.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserLevelPrivilegeServiceImpl implements UserLevelPrivilegeService {
    @Autowired
    private UserLevelPrivilegeRepository userLevelPrivilegeRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository customerSubscriptionMappingRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private EntityService entityService;

    @Override
    public List<UserLevelPrivilege> add(String authorization, UserLevelPrivilegeWrapperDTO userLevelPrivilegeWrapperDTO) {
        List<UserLevelPrivilege> userLevelPrivileges = new ArrayList<>();
        User user = findUserById(userLevelPrivilegeWrapperDTO.getAccountId());
        if (!CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getOrganizationIds())
                && CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getEntityIds())
                && CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getContractIds())) {
            List<Organization> organizations = organizationRepository.findAllById(userLevelPrivilegeWrapperDTO.getOrganizationIds());
            organizations.forEach(organization -> {
                UserLevelPrivilege userLevelPrivilege = new UserLevelPrivilege();
                userLevelPrivilege.setUser(user);
                userLevelPrivilege.setOrganization(organization);
                userLevelPrivileges.add(userLevelPrivilege);
            });
        }
        if (CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getOrganizationIds())
                && !CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getEntityIds())
                && CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getContractIds())) {
            List<Entity> entities = entityRepository.findAllById(userLevelPrivilegeWrapperDTO.getEntityIds());
            entities.forEach(entity -> {
                UserLevelPrivilege userLevelPrivilege = new UserLevelPrivilege();
                userLevelPrivilege.setUser(user);
                userLevelPrivilege.setEntity(entity);
                userLevelPrivilege.setOrganization(entity.getOrganization());
                userLevelPrivileges.add(userLevelPrivilege);
            });
        }
        if (CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getOrganizationIds())
                && CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getEntityIds())
                && !CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getContractIds())) {
            List<Contract> contracts = contractRepository.findAllById(userLevelPrivilegeWrapperDTO.getContractIds());
            contracts.forEach(contract -> {
                UserLevelPrivilege userLevelPrivilege = new UserLevelPrivilege();
                userLevelPrivilege.setUser(user);
                userLevelPrivilege.setContract(contract);
                userLevelPrivilege.setEntity(contract.getEntity());
                userLevelPrivilege.setOrganization(contract.getEntity().getOrganization());
                userLevelPrivileges.add(userLevelPrivilege);
            });
        }
        // for employeeManagement entity
        if (!CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getOrganizationIds())
                && !CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getEntityIds())
                && CollectionUtils.isEmpty(userLevelPrivilegeWrapperDTO.getContractIds())) {
            Optional<Organization> organization = organizationRepository.findById(userLevelPrivilegeWrapperDTO.getOrganizationIds().get(0));
            Optional<Entity> entity = entityRepository.findById(userLevelPrivilegeWrapperDTO.getEntityIds().get(0));
            if (organization.isPresent() && entity.isPresent()) {
                UserLevelPrivilege userLevelPrivilege = new UserLevelPrivilege();
                userLevelPrivilege.setUser(user);
                userLevelPrivilege.setOrganization(organization.get());
                userLevelPrivilege.setEntity(entity.get());
                userLevelPrivileges.add(userLevelPrivilege);
            }
        }
        return userLevelPrivilegeRepository.saveAll(userLevelPrivileges);
    }

    private User findUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new NotFoundException(User.class, id);
        }
        List<CustomerSubscription> customerSubscriptions =
                subscriptionRepository.findCustomerSubscriptionByUserAccount(userOptional.get());
        customerSubscriptions.forEach(customerSubscription -> {
            List<CustomerSubscriptionMapping> customerSubscriptionMappings =
                    customerSubscriptionMappingRepo.findCustomerSubscriptionMappingBySubscription(customerSubscription);
            System.out.println(customerSubscriptionMappings);
        });
        User user = userOptional.get();
        user.setCustomerSubscriptions(customerSubscriptions);
        return user;

    }

    @Override
    public List<UserLevelPrivilege> getUserSubscriptions(Account account) {
        // List<UserLevelPrivilege> userLevelPrivileges = userLevelPrivilegeRepository.findByUser(account);
        //  1-  Use stream function
        //  2-  flat map on result (organization)
        //  3-  flat map on result (entity)
        //  4-  flat map on result (contract)
        //  5-  filter on result (contract = master contract)
        //  6-  query contract mapping with filtered contracts (contract mapping - model)
        return null;
    }

    @Override
    public List<UserLevelPrivilege> UserLevelPrivilegeByContract(Contract contract) {
        return userLevelPrivilegeRepository.findByContract(contract);
    }

    @Override
    public List<UserLevelPrivilege> UserLevelPrivilegeByEntity(Entity entity) {
        return userLevelPrivilegeRepository.findByEntity(entity);
    }

    @Override
    public UserLevelPrivilege save(UserLevelPrivilege userLevelPrivilege) {

        return userLevelPrivilegeRepository.save(userLevelPrivilege);
    }

    @Override
    public List<UserLevelPrivilege> UserLevelPrivilegeByUser(User user) {
        return userLevelPrivilegeRepository.findByUser(user);
    }

    @Override
    public UserLevelPrivilege UserLevelPrivilegeByEntityId(Long entityId) {
        return userLevelPrivilegeRepository.findByEntityId(entityId);
    }

    @Override
    public UserLevelPrivilege userLevelPrivilegeByAccountId(Long acctId) {
        return userLevelPrivilegeRepository.findByAccountId(acctId);
    }

    /**
     * Description: Method for finding user level privilege by entity id and acct id
     * Created By: Ibtehaj
     *
     * @param entityId
     * @param acctId
     * @return
     */
    @Override
    public UserLevelPrivilege findByEntityIdAndAcctId(Long entityId, Long acctId) {
        return userLevelPrivilegeRepository.findByEntityIdAndAcctId(entityId, acctId);
    }

    @Override
    public List<UserLevelPrivilegeDTO> expandUserScope() {
        User currentUser = userService.getLoggedInUser();
        Set<Role> userRoles = currentUser.getRoles();
        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeByAccountId(currentUser.getAcctId());
        Organization organization = userLevelPrivilege.getOrganization();
        List<UserLevelPrivilegeDTO> result = new ArrayList<>();
        result = userLevelPrivilegeRepository.getByUserAndRolesAndOrganization(currentUser, userRoles, organization);
        if (result.isEmpty()) {
            result = userLevelPrivilegeRepository.getByRolesAndOrganization(userRoles, organization);
        }

        return result;
    }

    @Override
    public List<Long> getEntityIdListByScope() {
        List<Long> result = new ArrayList<>();
        List<UserLevelPrivilegeDTO> userLevelPrivilegeDTOList = expandUserScope();
        result = userLevelPrivilegeDTOList.stream()
                .filter(userLevelPrivilegeDTO -> (userLevelPrivilegeDTO.getAcctId() != null && userLevelPrivilegeDTO.getEntityId() != null && userLevelPrivilegeDTO.getOrganizationId() != null && userLevelPrivilegeDTO.getRoleId() != null) ||
                        (userLevelPrivilegeDTO.getEntityId() != null && userLevelPrivilegeDTO.getOrganizationId() != null && userLevelPrivilegeDTO.getRoleId() != null))
                .map(UserLevelPrivilegeDTO::getEntityId)
                .distinct()
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            List<Long> orgIds = userLevelPrivilegeDTOList.stream().map(UserLevelPrivilegeDTO::getOrganizationId).distinct().collect(Collectors.toList());
            result = entityService.findAllIdsByOrgId(orgIds, EUserType.CUSTOMER.getName(), false);
        }
        return result;
    }

    @Override
    public UserLevelPrivilege findSalesAgentByCustomerEntityId(Long customerEntityId) {
        return userLevelPrivilegeRepository.findSalesAgentByCustomerEntityId(customerEntityId);
    }

    @Override
    public List<Long> getCustomerEntityIdsByAgentAcctId(List<Long> acctIds) {
        return userLevelPrivilegeRepository.getCustomerEntityIdsByAgentAcctId(acctIds);
    }

    @Override
    public List<UserLevelPrivilege> findByAcctIds(List<Long> acctIds) {
        return userLevelPrivilegeRepository.findByAccountIds(acctIds);
    }
}
