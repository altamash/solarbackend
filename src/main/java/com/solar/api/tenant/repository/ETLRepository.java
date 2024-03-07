package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.user.UserCountDTO;
import com.solar.api.tenant.mapper.user.UserRoleTemplateDTO;
import com.solar.api.tenant.mapper.workOrder.UserDetailTemplateWoDTO;
import com.solar.api.tenant.mapper.workOrder.UserSubscriptionTemplateWoDTO;
import com.solar.api.tenant.model.user.TempPass;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.UserTemplate;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ETLRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT NEW com.solar.api.tenant.mapper.ca.CaUserTemplateDTO(prv.entity.id,prv.user.acctId,prv.user.firstName," +
            "prv.user.lastName,prv.user.emailAddress, " +
            "cd.customerType,cd.states,prv.entity.contactPersonPhone,um.ref_id) " +
            " FROM UserLevelPrivilege prv" +
            " INNER JOIN CustomerDetail cd" +
            " ON cd.entityId = prv.entity.id" +
            " LEFT JOIN UserMapping um " +
            " ON um.entityId = prv.entity.id " +
            " Where um.id is NULL " +
            " GROUP BY " +
            " prv.entity.id, prv.user.acctId, prv.user.firstName, " +
            " prv.user.lastName, prv.user.emailAddress, prv.user.createdAt, " +
            " cd.customerType, cd.states, prv.entity.contactPersonPhone, um.ref_id " +
            " ORDER BY prv.user.createdAt DESC" )
    List<CaUserTemplateDTO> findAllCaUsersETL();
}
