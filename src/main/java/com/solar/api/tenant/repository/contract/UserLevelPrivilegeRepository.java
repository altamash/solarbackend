package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO;
import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserLevelPrivilegeRepository extends JpaRepository<UserLevelPrivilege, Long> {
    List<UserLevelPrivilege> findByUser(User user);

    List<UserLevelPrivilege> findByContract(Contract contract);

    List<UserLevelPrivilege> findByEntity(Entity entity);

    @Query("select prv from UserLevelPrivilege prv where prv.entity.id=:entityId and prv.role is null")
    UserLevelPrivilege findByEntityId(Long entityId);

    @Query("select prv from UserLevelPrivilege prv where prv.user.acctId=:acctId and prv.role is null")
    UserLevelPrivilege findByAccountId(@Param("acctId") Long acctId);

    @Query("select prv from UserLevelPrivilege prv where prv.entity.id=:entityId and prv.user.acctId=:acctId")
    UserLevelPrivilege findByEntityIdAndAcctId(@Param("entityId") Long entityId, @Param("acctId") Long acctId);

    @Query("select user from UserLevelPrivilege user where user.entity.entityName=:entityName")
    List<UserLevelPrivilege> findByEntityName(String entityName);

    @Query("select new com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO(prv.id,prv.user.acctId,prv.contract.id," +
            "prv.entity.id,prv.organization.id,prv.role.id) " +
            "from UserLevelPrivilege prv where prv.user=:user and prv.role in (:roles) " +
            "and prv.organization=:organization and prv.role.id is not null")
    List<UserLevelPrivilegeDTO> getByUserAndRolesAndOrganization(@Param("user") User user, @Param("roles") Set<Role> roles,
                                                                 @Param("organization") Organization organization);
    @Query("select new com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO(prv.id,prv.user.acctId,prv.contract.id," +
            "prv.entity.id,prv.organization.id,prv.role.id) " +
            "from UserLevelPrivilege prv where prv.role in (:roles) " +
            "and prv.organization=:organization and prv.role.id is not null and prv.user is null")
    List<UserLevelPrivilegeDTO> getByRolesAndOrganization(@Param("roles") Set<Role> roles,
                                            @Param("organization") Organization organization);
    @Query("Select prv from UserLevelPrivilege prv where prv.entity.id =:customerEntityId and prv.user is not null and prv.role is not null and prv.organization is not null ")
    UserLevelPrivilege findSalesAgentByCustomerEntityId(@Param("customerEntityId") Long customerEntityId);
    @Query("Select prv.entity.id from UserLevelPrivilege prv where prv.user.acctId =:agentAcctIds and prv.entity is not null and prv.role is not null and prv.organization is not null ")
    List<Long> getCustomerEntityIdsByAgentAcctId(@Param("agentAcctIds") List<Long> agentAcctIds);

    @Query("select prv from UserLevelPrivilege prv where prv.user.acctId in(:acctIds) and prv.role is null")
    List<UserLevelPrivilege> findByAccountIds(@Param("acctIds") List<Long> acctIds);

}
