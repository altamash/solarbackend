package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.contract.ContractByEntityDTO;
import com.solar.api.tenant.mapper.contract.EntityDTO;
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

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAddress(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles where u.emailAddress = :email")
    User findByEmailAddressFetchRoles(String email);

    User findByUserName(String userName);

    List<User> findAllByUserName(String userName);

    //    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissionSets where u.userName =
    //    :userName")
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles where u.userName = :userName")
    User findByUserNameFetchRoles(String userName);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissionGroups g " +
            "LEFT JOIN FETCH g.permissionSets " +
            "LEFT JOIN FETCH r.permissionSets " +
            "LEFT JOIN FETCH u.permissionGroups gp " +
            "LEFT JOIN FETCH gp.permissionSets " +
            "LEFT JOIN FETCH u.permissionSets " +
            "where u.userName = :userName")
    User findByUserNameFetchPermissions(String userName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.customerSubscriptions where u.acctId = :userId")
    User findByIdFetchCustomerSubscription(Long userId);

    List<User> findAll(Specification<User> spec);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles where u.status <> 'INVALID' and u.acctId = :id")
    User findByIdFetchRoles(Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses where u.acctId = :id")
    User findByIdFetchAddresses(Long id);

    @Query("SELECT u FROM User u" +
            " LEFT JOIN FETCH u.roles" +
            " LEFT JOIN FETCH u.addresses" +
            " LEFT JOIN FETCH u.paymentInfos" +
            " LEFT JOIN FETCH u.customerSubscriptions" +
            " LEFT JOIN FETCH u.billingHeads" +
            " where u.acctId = :id")
    User findByIdFetchAll(Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles where u.status <> 'INVALID'")
    List<User> findAllFetchRoles();


    /**
     * Fetching customers with respect to entityType = Customers
     * Author: Shariq
     *
     * @return
     */
    @Query(value = "with entity as(select e.id from entity e" +
            "where e.entity_type = 'Customer'" +
            "and e.status = 'ACTIVE')" +
            "SELECT u.* FROM user u" +
            "inner join user_level_privilege ulp on u.acct_id = ulp.account_id" +
            "inner join entity e on  e.id = ulp.entity_id", nativeQuery = true)
    List<User> findAllCustomersFromEntity();

    @Query("SELECT distinct u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissionGroups g " +
            "LEFT JOIN FETCH g.permissionSets " +
            "LEFT JOIN FETCH r.permissionSets " +
            "LEFT JOIN FETCH u.permissionGroups gp " +
            "LEFT JOIN FETCH gp.permissionSets " +
            "LEFT JOIN FETCH u.permissionSets")
    List<User> findAllFetchPermissions();

    List<User> findByUserType(UserType userType);

    List<User> findByAcctIdIn(List<Long> ids);

    @Query("SELECT COUNT(u) FROM User u where u.status = :status")
    Long getCustomersCountByStatus(@Param("status") String status);

    @Query(value = "SELECT acct_id, first_name, clear_pass, enc_pass FROM temp_pass", nativeQuery = true)
    List<TempPass> passwordGenerator();

    @Query(value = "SELECT DISTINCT us.* from user us " +
            "inner join  customer_subscription as cs " +
            "on us.acct_id = cs.account_id " +
            "inner join subscription_rate_matrix_head as smh " +
            "on  cs.subscription_rate_matrix_id  = smh.id " +
            "where  smh.subscription_type_id in( :subscriptionTypes ) and us.status =  \"ACTIVE\" ", nativeQuery = true)
    List<User> findCustomerInverterSubs(@Param("subscriptionTypes") List<Long> subscriptionTypes);

    @Query("SELECT u.acctId from User u " +
            "JOIN  u.customerSubscriptions cs " +
            "on u.acctId = cs.userAccount.acctId " +
            "where  cs.id in( :subscriptionIds ) " +
            "group by u.acctId")
    List<Long> findUserBySubsId(@Param("subscriptionIds") List<Long> subscriptionList);

    @Query("from User user where :roles in elements(user.roles)")
    List<User> findByRolesContaining(List<Role> roles);

    //New query For User by customer type
    //Query start here
    @Query(value = "select distinct(u.acct_id) as accountId, e.id as entityId , u.first_name as firstName, u.last_name as lastName, " +
            "u.email_address as emailAddress,ploc.phone as phone, concat(ploc.add2,concat(',',ploc.add3)) as region,cd.customer_type as customerType , " +
            "ed.uri as profile_url " +
            "from user u " +
            "inner join user_level_privilege prv " +
            "on prv.account_id = u.acct_id " +
            "inner join entity e " +
            "on e.id =  prv.entity_id " +
            "left join customer_detail cd " +
            "on cd.entity_id = e.id " +
            "left join docu_library docu " +
            "on e.id = docu.entity_id " +
            "left join entity_detail ed " +
            "on ed.entity_id = e.id  " +
            "left join ca_referral_info rf  " +
            "on rf.entity_id=e.id  " +
            "left join location_mapping locmap " +
            "on locmap.source_id = u.acct_id " +
            "left join physical_locations ploc " +
            "on ploc.id = locmap.location_id " +
            "where (upper(cd.customer_type) = upper(:customerType) and upper(cd.status) = 'ACTIVE') " +
            "and (isnull(cd.states) or upper(cd.states) not in (\"LEAD\",\"PROSPPECT\",\"INTERIM-CUSTOMER\") " +
            "and locmap.primary_ind = 'Y')",
            nativeQuery = true)
    List<UserRoleTemplateDTO> findAllUsersByCustomerType(@Param("customerType") String customerType);
    //Query Ends here

    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, concat(ploc.add2,concat(',',ploc.add3)) as region,cud.customer_type as customerType,e.id as entityId," +
            " e.entity_name as entityName, ed.uri as profileUrl" +
            " from user u" +
            " inner join user_level_privilege prv" +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " where u.acct_id in (:accountIds)" +
            " and locmap.primary_ind = 'Y'",
            nativeQuery = true)
    List<UserSubscriptionTemplateWoDTO> findUsersAndLocationByAccountId(@Param("accountIds") List<Long> accountIds);

    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, cud.customer_type as customerType,e.id as entityId," +
            " e.entity_name as entityName, ed.uri as profileUrl" +
            " from user u" +
            " inner join user_level_privilege prv" +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " where u.acct_id in (:accountIds)" +
            " and locmap.primary_ind = 'Y'",
            nativeQuery = true)
    List<UserSubscriptionTemplateWoDTO> findUsersByAccountId(@Param("accountIds") List<Long> accountIds);


    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, u.email_address as emailAddress ,u.created_at as generatedAt, cd.customer_type as customerType, cd.states as status,e.contact_person_phone as phone, ploc.zip_code as zipCode,concat(ploc.add2,concat(',',ploc.add3)) as region, ploc.id as physicalLocId, e.id as entityId, docu.uri as signedDocument, " +
            "(CASE WHEN sc.is_checked IS true THEN '1' WHEN sc.is_checked IS false THEN '0' ELSE 'Null' END ) as isChecked, ed.uri as profile_url,rf.rep_id as referralId" +
            " from user u" +
            " inner join user_level_privilege prv" +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join customer_detail cd" +
            " on cd.entity_id = e.id" +
            " left join docu_library docu" +
            " on e.id = docu.entity_id" +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join ca_referral_info rf " +
            " on rf.entity_id=e.id " +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join ca_soft_credit_check sc" +
            " on sc.customer_no = e.id" +
            " where cd.states in (:states) and e.is_deleted is null ",
            nativeQuery = true)
    List<UserTemplate> findAllCaUsers(@Param("states") List<String> states);

    @Query(value = "SELECT SUM(CASE WHEN customer_type = ('Residential') THEN 1 ELSE 0 END) as residential,SUM(CASE WHEN customer_type =('Individual') THEN 1 ELSE 0 END) AS Individual,SUM(CASE WHEN customer_type =('Commercial') THEN 1 ELSE 0 END) AS Commercial,SUM(CASE WHEN customer_type = ('Non-Profit') THEN 1 ELSE 0 END) AS NonProfit FROM customer_detial where states in ('CUSTOMER')",
            nativeQuery = true)
    UserTemplate findAllCustomerCount(@Param("states") List<String> states);

    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, u.email_address as emailAddress ," +
            "u.created_at as generatedAt, cd.customer_type as customerType, cd.states as status,e.contact_person_phone as phone, " +
            "ploc.zip_code as zipCode,concat(ploc.add2,concat(',',ploc.add3)) as region, ploc.id as physicalLocId, e.id as " +
            "entityId, docu.uri as signedDocument, " +
            "(CASE WHEN sc.is_checked IS true THEN '1' WHEN sc.is_checked IS false THEN '0' ELSE 'Null' END ) as isChecked, ed.uri as profile_url,rf.rep_id as referralId" +
            " from user u" +
            " inner join user_level_privilege prv" +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join customer_detail cd" +
            " on cd.entity_id = e.id" +
            " left join docu_library docu" +
            " on e.id = docu.entity_id" +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join ca_referral_info rf " +
            " on rf.entity_id=e.id " +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join ca_soft_credit_check sc" +
            " on sc.customer_no = e.id" +
            " where upper(cd.states) in (:states) and e.is_deleted is null",
            nativeQuery = true)
    List<UserTemplate> findAllCaUsersByType(@Param("states") List<String> states);

    User findByAcctId(Long id);

    @Query(value = "select e.entity_name as userName, cud.customer_type as userType,e.contact_person_email as userEmail,e.contact_person_phone as userPhone,ed.uri as imageUri " +
            " from entity e " +
            " left join entity_detail ed on e.id = ed.entity_id " +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            "where e.id = :entityId", nativeQuery = true)
    UserDetailTemplateWoDTO findUserDetails(@Param("entityId") Long entityId);


    @Query(value = "select concat(u.first_name,concat(' ',u.last_name)) as userName , cud.customer_type as userType,e.contact_person_email as userEmail,e.contact_person_phone as userPhone," +
            " ed.uri as userImage" +
            " from user u" +
            " inner join user_level_privilege prv" +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " inner join entity_detail ed" +
            " on ed.entity_id = e.id" +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " where u.acct_id = :accountId"
            ,
            nativeQuery = true)
    UserDetailTemplateWoDTO findCustomerByAccountId(@Param("accountId") Long accountId);

    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, cud.customer_type as customerType,e.id as entityId," +
            " e.entity_name as entityName, ed.uri as profileUrl " +
            " from user u " +
            " left join user_level_privilege prv " +
            " on prv.account_id = u.acct_id" +
            " left join entity e " +
            " on e.id =  prv.entity_id " +
            " left join entity_detail ed " +
            " on ed.entity_id = e.id " +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " left join location_mapping locmap " +
            " on locmap.source_id = u.acct_id " +
            " where u.acct_id in (:accountIds) " +
            " and locmap.primary_ind = 'Y' ",
            nativeQuery = true)
    List<UserSubscriptionTemplateWoDTO> findCustomerListsByAccountId(@Param("accountIds") List<Long> accountIds);

    @Query(value = "SELECT u.acct_id AS accountId, u.first_name AS firstName, u.last_name AS lastName, " +
            "u.email_address AS emailAddress, u.created_at AS generatedAt, u.register_date AS registeredDate, " +
            "(SELECT COUNT(*) FROM customer_subscription cs WHERE cs.account_id = u.acct_id) AS subscriptionTotal, " +
            "cud.customer_type AS customerType, e.status AS status, cud.states AS stateCustomer, e.contact_person_phone AS phone, ploc.zip_code AS zipCode, " +
            "CONCAT_WS(',', ploc.ext1, ploc.ext2, ploc.add3) AS region, e.id AS entityId, " +
            "(CASE WHEN sc.is_checked IS TRUE THEN '1' WHEN sc.is_checked IS FALSE THEN '0' ELSE 'Null' END) AS isChecked, " +
            "ed.uri AS profileUrl, " +
            "cud.self_initiative AS selfInitiative, um.ref_id AS mongoProjectId, " +
            "u.password AS hasPassword, cud.has_Login AS hasLogin, cud.mobile_allowed AS mobileAllowed, " +
            "(SELECT COUNT(*) FROM ca_utility WHERE customer_entity_id = e.id) AS utilityInfoCount, " +
            "(SELECT COUNT(*) FROM conversation_head WHERE customer_id = ed.entity_id) AS supportTicketCount, " +
            "(SELECT COUNT(*) FROM payment_info WHERE portal_account_id = u.acct_id) AS paymentInfoCount, " +
            "(SELECT COUNT(*) FROM docu_library WHERE entity_id = cud.entity_id) AS contractCount, " +
            "(SELECT COUNT(*) FROM location_mapping WHERE source_id = u.acct_id) AS addressCount," +
            ":isLeaf as isLeaf,(SELECT CONCAT(u1.first_name, ' ', u1.last_name) FROM user_level_privilege prv1 " +
            " INNER JOIN user u1 ON u1.acct_id = prv1.account_id " +
            " WHERE prv1.entity_id = e.id " +
            " AND prv1.account_id IS NOT NULL " +
            " AND prv1.role_id IS NOT NULL " +
            " AND prv1.organization_id IS NOT NULL " +
            "LIMIT 1) AS agentName, (SELECT ed3.uri FROM user_level_privilege prv2 " +
            " INNER JOIN user u2 ON u2.acct_id = prv2.account_id " +
            " INNER JOIN user_level_privilege prv3 ON prv3.account_id = prv2.account_id " +
            " Left JOIN entity_detail ed3 ON ed3.entity_id = prv3.entity_id " +
            " WHERE prv2.entity_id = e.id " +
            " AND prv2.account_id IS NOT NULL " +
            " AND prv2.role_id IS NOT NULL " +
            " AND prv2.organization_id IS NOT NULL" +
            " AND prv3.role_id IS NULL " +
            "LIMIT 1) AS agentImage " +
            "FROM user u " +
            "LEFT JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
            "INNER JOIN entity e ON e.id = prv.entity_id " +
            "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
            "INNER JOIN customer_detail cud ON cud.entity_id = e.id " +
            "LEFT JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
            "LEFT JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
            "LEFT JOIN ca_soft_credit_check sc ON sc.customer_no = e.id " +
            "LEFT JOIN user_mapping um ON um.entity_id = prv.entity_id " +
            "WHERE cud.states NOT IN ( 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', " +
            "'PROSPECT', 'REQUEST PENDING' ) AND (locmap.primary_ind = 'Y' or locmap.primary_ind is null) " +
            " AND (ploc.is_deleted is null or ploc.is_deleted = false) " +
            "AND (e.is_deleted is null or e.is_deleted = false) " +
            "and prv.role_id is null "+
            "AND (" +
            "(:groupby = 'Customer Type' AND cud.customer_type = :groupbyName) OR " +
            "(:groupby = 'Source' AND u.acct_id in (select distinct cs.account_id from customer_subscription cs where cs.ext_subs_id IN (SELECT edsd.subs_id FROM ext_data_stage_definition edsd WHERE edsd.ref_type = :groupbyName)) ) OR " +
            "(:groupby = 'Region' AND ploc.ext2 = :groupbyName) OR " +
            "(:groupby IS NULL OR :groupby = '') " + // Handles the case where groupby is not set
            ") " +
            "ORDER BY u.created_at DESC",
            countQuery = "SELECT COUNT(DISTINCT u.acct_id) " +
                    "FROM user u " +
                    "LEFT JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
                    "INNER JOIN entity e ON e.id = prv.entity_id " +
                    "LEFT JOIN customer_detail cud ON cud.entity_id = e.id " +
                    "LEFT JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
                    "LEFT JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
                    "WHERE cud.states NOT IN ( 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING'" +
                    "'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING' ) AND (locmap.primary_ind = 'Y' or locmap.primary_ind is null) " +
                    "AND (ploc.is_deleted is null or ploc.is_deleted = false) " +
                    "AND (e.is_deleted is null or e.is_deleted = false) " +
                    "AND (" +
                    "(:groupby = 'Customer Type' AND cud.customer_type = :groupbyName) OR " +
                    "(:groupby = 'Source' AND u.acct_id in (select distinct cs.account_id from customer_subscription cs where cs.ext_subs_id IN (SELECT edsd.subs_id FROM ext_data_stage_definition edsd WHERE edsd.ref_type = :groupbyName)) ) OR " +
                    "(:groupby = 'Region' AND ploc.ext2 = :groupbyName) OR " +
                    "(:groupby IS NULL OR :groupby = '') " + // Handles the case where groupby is not set
                    ")",
            nativeQuery = true)
    Page<UserTemplate> getAllCustomerLists(@Param("isLeaf") Boolean isLeaf, @Param("groupby") String groupby, @Param("groupbyName") String groupbyName, Pageable pageable);

    @Query(value = "SELECT distinct cud.customer_type AS groupBy,:isLeaf as isLeaf " +
            "FROM user u " +
            "inner JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
            "inner JOIN customer_detail cud ON cud.entity_id = prv.entity_id " +
            "inner JOIN entity e ON e.id = prv.entity_id " +
            "WHERE cud.states NOT IN ( 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING' ) " +
            "AND (e.is_deleted is null or e.is_deleted = false) ",
            nativeQuery = true)
    Page<UserTemplate> getAllCustomerTypeList(@Param("isLeaf") Boolean isLeaf, Pageable pageable);

    @Query(value = "SELECT distinct ploc.ext2 AS groupBy,:isLeaf as isLeaf " +
            "FROM user u " +
            "inner JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
            "inner JOIN entity e ON e.id = prv.entity_id " +
            "inner JOIN customer_detail cud ON cud.entity_id = prv.entity_id " +
            "inner JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
            "inner JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
            "WHERE cud.states NOT IN ( 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING' ) " +
            "AND (locmap.primary_ind = 'Y' or locmap.primary_ind is null)" +
            "AND (e.is_deleted is null or e.is_deleted = false) " +
            "AND (ploc.is_deleted is null or ploc.is_deleted = false)" ,
            nativeQuery = true)
    Page<UserTemplate> getAllCustomerRegionList(@Param("isLeaf") Boolean isLeaf, Pageable pageable);

    @Query(value = "SELECT distinct edsd.ref_type AS groupBy,:isLeaf as isLeaf " +
            "FROM user u " +
            "inner JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
            "inner JOIN entity e ON e.id = prv.entity_id " +
            "inner JOIN customer_detail cud ON cud.entity_id = prv.entity_id " +
            "inner JOIN customer_subscription cs ON cs.account_id = u.acct_id " +
            "left JOIN ext_data_stage_definition edsd ON edsd.subs_id = cs.ext_subs_id " +
            "WHERE cud.states NOT IN ( 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING' ) " +
            "AND (e.is_deleted is null or e.is_deleted = false) " ,
            nativeQuery = true)
    Page<UserTemplate> getAllCustomerSourceList(@Param("isLeaf") Boolean isLeaf, Pageable pageable);

    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, u.email_address as emailAddress ,u.created_at as generatedAt,u.register_date as registeredDate,ploc.add3 as state,ploc.add2 as place,(select count(*) from customer_subscription where account_id=u.acct_id  group by account_id) as subscriptionTotal, cd.customer_type as customerType, cd.states as stateCustomer,e.status as status,e.contact_person_phone as phone, ploc.zip_code as zipCode,concat(ploc.add3,concat(',',ploc.add2)) as region, ploc.id as physicalLocId, e.id as entityId,(CASE WHEN sc.is_checked IS true THEN '1' WHEN sc.is_checked IS false THEN '0' ELSE 'Null' END ) as isChecked, docu.uri as signedDocument,ed.uri as profile_url" +
            " from user u " +
            " left join user_level_privilege prv " +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join customer_detail cd " +
            " on cd.entity_id = e.id " +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join docu_library docu" +
            " on e.id = docu.entity_id" +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join ca_soft_credit_check sc" +
            " on sc.customer_no = e.id" +
            " left join customer_subscription as cs using(account_id)" +
            " where cd.states in (\"CUSTOMER\") " +
            " and cd.customer_type in (:customerType) and locmap.primary_ind='Y' ",
            nativeQuery = true)
    List<UserTemplate> findAllCustomerByType(@Param("customerType") List<String> customerType);


    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, u.email_address as emailAddress ,u.created_at as generatedAt,u.register_date as registeredDate,ploc.add3 as state,ploc.add2 as place,(select count(*) from customer_subscription where account_id=u.acct_id  group by account_id) as subscriptionTotal, cud.customer_type as customerType, cud.states as stateCustomer,e.status as status,e.contact_person_phone as phone, ploc.zip_code as zipCode,concat(ploc.add3,concat(',',ploc.add2)) as region, ploc.id as physicalLocId, e.id as entityId,(CASE WHEN sc.is_checked IS true THEN '1' WHEN sc.is_checked IS false THEN '0' ELSE 'Null' END ) as isChecked, docu.uri as signedDocument,ed.uri as profile_url" +
            " from user u " +
            " left join user_level_privilege prv " +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join docu_library docu" +
            " on e.id = docu.entity_id" +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join ca_soft_credit_check sc" +
            " on sc.customer_no = e.id" +
            " left join customer_subscription as cs using(account_id)" +
            " where cud.states in (\"CUSTOMER\") and locmap.primary_ind='Y' " +
            " group by cud.customer_type,u.acct_id,u.first_name,u.last_name,u.email_address,u.created_at, " +
            " u.register_date,cud.states,e.status,e.contact_person_phone,ploc.zip_code,ploc.add2,ploc.add3,ploc.id,e.id,docu.uri,ed.uri,sc.is_checked " +
            " order by cud.customer_type ",
            nativeQuery = true)
    List<UserTemplate> findAllCustomerByTypeGroup();

    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, u.email_address as emailAddress ,u.created_at as generatedAt,u.register_date as registeredDate,ploc.add3 as state,ploc.add2 as place,(select count(*) from customer_subscription where account_id=u.acct_id  group by account_id) as subscriptionTotal, cud.customer_type as customerType, cud.states as stateCustomer,e.status as status,e.contact_person_phone as phone, ploc.zip_code as zipCode,concat(ploc.add3,concat(',',ploc.add2)) as region, ploc.id as physicalLocId, e.id as entityId,(CASE WHEN sc.is_checked IS true THEN '1' WHEN sc.is_checked IS false THEN '0' ELSE 'Null' END ) as isChecked, docu.uri as signedDocument,ed.uri as profile_url" +
            " from user u " +
            " left join user_level_privilege prv " +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join docu_library docu" +
            " on e.id = docu.entity_id" +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join ca_soft_credit_check sc" +
            " on sc.customer_no = e.id" +
            " left join customer_subscription as cs using(account_id)" +
            " where cud.states in (\"CUSTOMER\") and locmap.primary_ind='Y' " +
            " and upper(ploc.add3) in (:region) ",
            nativeQuery = true)
    List<UserTemplate> findAllCustomerByRegion(@Param("region") List<String> region);

    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, u.email_address as emailAddress ,u.created_at as generatedAt,u.register_date as registeredDate,ploc.add3 as state,ploc.add2 as place,(select count(*) from customer_subscription where account_id=u.acct_id  group by account_id) as subscriptionTotal, cud.customer_type as customerType, cud.states as stateCustomer,e.status as status,e.contact_person_phone as phone, ploc.zip_code as zipCode,concat(ploc.add3,concat(',',ploc.add2)) as region, ploc.id as physicalLocId, e.id as entityId,(CASE WHEN sc.is_checked IS true THEN '1' WHEN sc.is_checked IS false THEN '0' ELSE 'Null' END ) as isChecked, docu.uri as signedDocument,ed.uri as profile_url" +
            " from user u " +
            " left join user_level_privilege prv " +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join docu_library docu" +
            " on e.id = docu.entity_id" +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join ca_soft_credit_check sc" +
            " on sc.customer_no = e.id" +
            " left join customer_subscription as cs using(account_id)" +
            " where cud.states in (\"CUSTOMER\") and locmap.primary_ind='Y' " +
            " group by ploc.add3,ploc.add2,cud.customer_type,u.acct_id,u.first_name,u.last_name,u.email_address,u.created_at, " +
            " u.register_date,cud.states,e.status,e.contact_person_phone,ploc.zip_code,ploc.id,e.id,docu.uri,ed.uri,sc.is_checked " +
            " order by concat(ploc.add3,concat(',',ploc.add2)) ",
            nativeQuery = true)
    List<UserTemplate> findAllCustomerByRegionGroup();

    User findByEmailAddressAndUserName(String emailAddress, String userName);


    //TODO: Need to remove when revamping customer management apis
    @Query(value = "select u.acct_id as accountId, u.first_name as firstName, u.last_name as lastName, u.email_address as emailAddress ,u.created_at as generatedAt,u.register_date as registeredDate,ploc.add3 as state,ploc.add2 as place,(select count(*) from customer_subscription where account_id=u.acct_id  group by account_id) as subscriptionTotal, cud.customer_type as customerType, e.status as status,cud.states as stateCustomer,e.contact_person_phone as phone, ploc.zip_code as zipCode,concat(ploc.add3,concat(',',ploc.add2)) as region, ploc.id as physicalLocId, e.id as entityId,(CASE WHEN sc.is_checked IS true THEN '1' WHEN sc.is_checked IS false THEN '0' ELSE 'Null' END ) as isChecked, docu.uri as signedDocument,ed.uri as profile_url" +
            " from user u " +
            " left join user_level_privilege prv " +
            " on prv.account_id = u.acct_id" +
            " inner join entity e" +
            " on e.id =  prv.entity_id" +
            " left join docu_library docu" +
            " on e.id = docu.entity_id" +
            " left join entity_detail ed" +
            " on ed.entity_id = e.id " +
            " left join customer_detail cud " +
            " on cud.entity_id = e.id " +
            " left join location_mapping locmap" +
            " on locmap.source_id = u.acct_id" +
            " left join physical_locations ploc" +
            " on ploc.id = locmap.location_id" +
            " left join ca_soft_credit_check sc" +
            " on sc.customer_no = e.id" +
            " left join customer_subscription as cs using(account_id)" +
            " where cud.states in (:states) and locmap.primary_ind='Y' and u.acct_id =:acctId ",
            nativeQuery = true)
    List<UserTemplate> getAllCustomerListsByAcctId(@Param("states") List<String> states, @Param("acctId") Long acctId);

    @Query("SELECT new com.solar.api.tenant.mapper.user.UserCountDTO(count(u), month(u.createdAt)) " +
            "FROM User u " +
            "where u.createdAt between :startDate and :endDate group by month(u.createdAt)")
    List<UserCountDTO> countByYear(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query(value = "select new com.solar.api.tenant.mapper.ca.CaUserTemplateDTO(prv.entity.id,prv.user.acctId,prv.user.firstName,prv.user.lastName,prv.user.emailAddress,date_format(prv.user.createdAt, '%b %d, %Y')," +
            "cd.customerType,cd.states,prv.entity.contactPersonPhone,ploc.zipCode,concat(ploc.add2,concat(',',ploc.add3)),ploc.id,ed.uri, " +
            "CASE WHEN sc.isChecked IS true THEN '1' WHEN sc.isChecked IS false THEN '0' ELSE 'Null' END,um.ref_id,date_format(cd.updatedAt, '%b %d, %Y'), COALESCE(Count(ch.id), 0) , cd.selfInitiative, cd.leadSource)" +
            " from UserLevelPrivilege prv" +
            " inner join CustomerDetail cd" +
            " on cd.entityId = prv.entity.id" +
            " left join EntityDetail ed" +
            " on ed.entity.id = prv.entity.id " +
            " left join LocationMapping locmap" +
            " on locmap.sourceId = prv.user.acctId" +
            " left join PhysicalLocation ploc" +
            " on ploc.id = locmap.locationId" +
            " left join CaSoftCreditCheck sc" +
            " on sc.entity.id = prv.entity.id" +
            " left join UserMapping um " +
            " on um.entityId = prv.entity.id " +
            " LEFT JOIN ConversationHead ch " +
            " ON ch.sourceId  = CAST(prv.entity.id AS string)  AND ch.category = 'Customer Acquisition' " +
            " WHERE (" +
            "( :type = 'completed' AND cd.states NOT IN ( " +
            " 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING' )" +
            ") OR ( " +
            ":type != 'COMPLETED' AND cd.states IN (:leadType) " +
            " ) " +
            " and prv.entity.id in (:entityIds) " +
            " and (prv.entity.isDeleted is null or prv.entity.isDeleted = false) " +
            " and (:statusListSize = 0 or cd.states in (:statusList)) " + // conditional clause for statuses
            " and (:zipCodeListSize = 0 or ploc.zipCode in (:zipCodeList)) " + // conditional clause for zipCodes
            " and function('STR_TO_DATE', prv.user.createdAt, '%Y-%m-%d %H:%i:%s') " +
            " between coalesce(CASE WHEN :startDate = '' OR :startDate IS NULL THEN '1900-01-01 00:00:00' ELSE concat(function('STR_TO_DATE', :startDate, '%b %d, %Y'), ' 00:00:00') " +
            "END, '1900-01-01 00:00:00') " + // default to a very old date if startDate is null
            "and coalesce(CASE WHEN :endDate = '' OR :endDate IS NULL THEN '9999-12-31 23:59:59' ELSE concat(function('STR_TO_DATE', :endDate, '%b %d, %Y'), ' 23:59:59') " +
            "END,'9999-12-31 23:59:59') )" +
            "AND (locmap.primaryInd = 'Y' OR locmap.primaryInd IS NULL) " +
            "GROUP BY " +
            " prv.entity.id, prv.user.acctId, prv.user.firstName, " +
            " prv.user.lastName, prv.user.emailAddress, prv.user.createdAt, " +
            " cd.customerType, cd.states, prv.entity.contactPersonPhone, " +
            " ploc.zipCode, ploc.add2, ploc.add3, ploc.id, " +
            " ed.uri, sc.isChecked, um.ref_id, cd.updatedAt , cd.selfInitiative, cd.leadSource " +
            " order by prv.user.createdAt desc")
    Page<CaUserTemplateDTO> findAllCaUsersByEntityIdsAndLeadType(@Param("leadType") List<String> leadType,
                                                                 @Param("type") String type,
                                                                 @Param("entityIds") List<Long> entityIds,
                                                                 @Param("startDate") String startDate,
                                                                 @Param("endDate") String endDate,
                                                                 @Param("statusList") List<String> statusList,
                                                                 @Param("statusListSize") int statusListSize,
                                                                 @Param("zipCodeList") List<String> zipCodeList,
                                                                 @Param("zipCodeListSize") int zipCodeListSize,
                                                                 Pageable pageable);


    @Query(value = "select new com.solar.api.tenant.mapper.ca.CaUserTemplateDTO(prv.entity.id,prv.user.acctId,prv.user.firstName,prv.user.lastName,prv.user.emailAddress,date_format(prv.user.createdAt, '%b %d, %Y')," +
            "cd.customerType,cd.states,prv.entity.contactPersonPhone,ploc.zipCode,concat(ploc.add2,concat(',',ploc.add3)),ploc.id,ed.uri, " +
            "CASE WHEN sc.isChecked IS true THEN '1' WHEN sc.isChecked IS false THEN '0' ELSE 'Null' END,um.ref_id,date_format(cd.updatedAt, '%b %d, %Y'), COALESCE(Count(ch.id), 0) , cd.selfInitiative, cd.leadSource)" +
            " from UserLevelPrivilege prv" +
            " inner join CustomerDetail cd" +
            " on cd.entityId = prv.entity.id" +
            " left join EntityDetail ed" +
            " on ed.entity.id = prv.entity.id " +
            " left join LocationMapping locmap" +
            " on locmap.sourceId = prv.user.acctId" +
            " left join PhysicalLocation ploc" +
            " on ploc.id = locmap.locationId" +
            " left join CaSoftCreditCheck sc" +
            " on sc.entity.id = prv.entity.id" +
            " left join UserMapping um " +
            " on um.entityId = prv.entity.id " +
            " LEFT JOIN ConversationHead ch " +
            " ON ch.sourceId  = CAST(prv.entity.id AS string)  AND ch.category = 'Customer Acquisition' " +
            " WHERE " +
            "(prv.user.firstName LIKE %:searchWord% " +
            " or prv.user.lastName LIKE %:searchWord% " +
            " or prv.user.emailAddress LIKE %:searchWord% " +
            " or ploc.zipCode LIKE %:searchWord% " +
            " or prv.entity.contactPersonPhone LIKE %:searchWord% " +
            " or cd.customerType LIKE %:searchWord% ) " +
            "AND (locmap.primaryInd = 'Y' OR locmap.primaryInd IS NULL) " +
            "GROUP BY " +
            "prv.entity.id, prv.user.acctId, prv.user.firstName," +
            " prv.user.lastName, prv.user.emailAddress, prv.user.createdAt," +
            " cd.customerType, cd.states, prv.entity.contactPersonPhone, " +
            "ploc.zipCode, ploc.add2, ploc.add3, ploc.id, " +
            "ed.uri, sc.isChecked, um.ref_id, cd.updatedAt , cd.selfInitiative, cd.leadSource " +
            "HAVING ( " +
            ":type = 'completed' AND cd.states NOT IN ( " +
            "'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING' " +
            " ) " +
            ") OR ( " +
            ":type != 'COMPLETED' AND cd.states IN (:leadType) " +
            ") " +
            "order by prv.user.createdAt desc ")
    Page<CaUserTemplateDTO> findAllBySearchWord(@Param("searchWord") String searchWord,
                                                @Param("leadType") List<String> leadType,
                                                @Param("type") String type,
                                                Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT max(u.acctId)+1 FROM User u ")
    Long getNextMaxAcctId();
}
