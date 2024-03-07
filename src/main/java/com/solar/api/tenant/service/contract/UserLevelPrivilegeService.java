package com.solar.api.tenant.service.contract;

import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeWrapperDTO;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.user.User;

import java.util.List;

public interface UserLevelPrivilegeService {
    List<UserLevelPrivilege> add(String authorization, UserLevelPrivilegeWrapperDTO userLevelPrivilegeWrapperDTO);

    List<UserLevelPrivilege> getUserSubscriptions(Account account);

    List<UserLevelPrivilege> UserLevelPrivilegeByContract(Contract contract);

    List<UserLevelPrivilege> UserLevelPrivilegeByEntity(Entity entity);

    UserLevelPrivilege save(UserLevelPrivilege userLevelPrivilege);

    List<UserLevelPrivilege> UserLevelPrivilegeByUser(User user);

    UserLevelPrivilege UserLevelPrivilegeByEntityId(Long entityId);
    UserLevelPrivilege userLevelPrivilegeByAccountId(Long acctId);
    UserLevelPrivilege findByEntityIdAndAcctId(Long entityId,Long acctId);
    List<UserLevelPrivilegeDTO> expandUserScope();
    List<Long> getEntityIdListByScope();
    UserLevelPrivilege findSalesAgentByCustomerEntityId(Long customerEntityId);
    List<Long> getCustomerEntityIdsByAgentAcctId(List<Long> acctIds);

    List<UserLevelPrivilege> findByAcctIds(List<Long> acctIds);
}
