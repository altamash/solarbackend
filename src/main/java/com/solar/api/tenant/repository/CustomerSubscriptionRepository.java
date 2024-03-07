package com.solar.api.tenant.repository;

import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionDetailDTO;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionDetailTemplate;
import com.solar.api.tenant.mapper.subscription.SubscriptionInfoTemplate;
import com.solar.api.tenant.mapper.subscription.SubscriptionTemplate;
import com.solar.api.tenant.mapper.user.CustomerProfileDTO;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.SubscriptionTerminationTemplate;
import com.solar.api.tenant.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface CustomerSubscriptionRepository extends JpaRepository<CustomerSubscription, Long> {

    @Transactional
    @Procedure(procedureName = "sp_aging_report_daily")
    void callAgingReport();

    @Query("SELECT new com.solar.api.tenant.mapper.subscription.SubscriptionTemplate(cs.id, cs" +
            ".subscriptionRateMatrixId, crm.subscriptionTemplate) " +
            "from CustomerSubscription cs, SubscriptionRateMatrixHead crm " +
            "WHERE cs.userAccount = :userAccount AND crm.id = cs.subscriptionRateMatrixId")
    List<SubscriptionTemplate> listCustomerSubscriptionByUserAccount(@Param("userAccount") User userAccount);

    @Query("SELECT new com.solar.api.tenant.mapper.subscription.SubscriptionTemplate(cs.id, cs" +
            ".subscriptionRateMatrixId, crm.subscriptionTemplate) " +
            "from CustomerSubscription cs, SubscriptionRateMatrixHead crm " +
            "WHERE crm.id = cs.subscriptionRateMatrixId")
    List<SubscriptionTemplate> listCustomerSubscription();

    List<CustomerSubscription> findCustomerSubscriptionByUserAccount(User userAccount);

    List<CustomerSubscription> findAllBySubscriptionRateMatrixId(Long subscriptionRateMatrixId);

    @Query("SELECT cs.id from CustomerSubscription cs WHERE subscriptionRateMatrixId=:subscriptionRateMatrixId AND id" +
            " not in(1002,1309,1452,1664,1665,1666,1669,1670,1673)")
    List<Long> findForTrueUp(Long subscriptionRateMatrixId);

    /**
     * Find by multiple
     * subscription Types
     *
     * @param subscriptionType
     * @return
     */
    @Query("SELECT cs.id from CustomerSubscription cs WHERE subscriptionType in (:subscriptionType) and subscriptionStatus = 'ACTIVE'")
    List<Long> findBySubscriptionTypesIn(List<String> subscriptionType);

    @Query("SELECT cs from CustomerSubscription cs WHERE subscriptionRateMatrixId=:subscriptionRateMatrixId AND " +
            "subscriptionStatus='ACTIVE' AND id not in(1002,1309,1452,1664,1665,1666,1669,1670,1673)")
    List<CustomerSubscription> findForTrueUpCustomerSubscriptionObject(Long subscriptionRateMatrixId);

    @Query("SELECT cs from CustomerSubscription cs WHERE subscriptionRateMatrixId=:subscriptionRateMatrixId AND " +
            "subscriptionStatus='Active'")
    List<CustomerSubscription> findActiveBySubscriptionRateMatrixId(Long subscriptionRateMatrixId);

    @Query("SELECT cs from CustomerSubscription cs WHERE cs.subscriptionRateMatrixId in (:subscriptionRateMatrixIds) " +
            "AND cs.subscriptionStatus='Active' AND cs.userAccount.status='Active'")
    List<CustomerSubscription> findActiveBySubscriptionRateMatrixIds(List<Long> subscriptionRateMatrixIds);

    @Query("SELECT cs.id from CustomerSubscription cs WHERE cs.subscriptionRateMatrixId in " +
            "(:subscriptionRateMatrixIds) AND cs.subscriptionStatus='Active' AND cs.userAccount.status='Active'")
    List<Long> findActiveIdsBySubscriptionRateMatrixIds(List<Long> subscriptionRateMatrixIds);

    List<CustomerSubscription> findBySubscriptionStatusAndSubscriptionType(String subscriptionStatus,
                                                                           String subscriptionType);

    @Query("select cs from CustomerSubscription cs where cs.userAccount=:userAccount")
    List<CustomerSubscription> findSubscriptionsByUser(@Param("userAccount") User userAccount);

    @Query("select cs from CustomerSubscription cs LEFT JOIN FETCH cs.customerSubscriptionMappings where cs.id = :id")
    CustomerSubscription findByIdFetchCustomerSubscriptionMappings(@Param("id") Long id);

    @Query("select new com.solar.api.tenant.mapper.subscription.SubscriptionInfoTemplate(cs.id, cs" +
            ".subscriptionStatus, " +
            "cs.subscriptionTemplate, cs.userAccount.acctId, ccm.value, bh.amount," +
            " bh.billingMonthYear, bh.id, bh.invoice.id, bh.dueDate, bh.billStatus)" +
            " from CustomerSubscription cs, CustomerSubscriptionMapping ccm, BillingHead bh" +
            " where cs.userAccount=:userAccount and cs.subscriptionStatus='ACTIVE'" +
            " and ccm.subscription.id=cs.id and ccm.rateCode='PN'" +
            " and bh.subscriptionId=cs.id and bh.id in (select max(bHd.id) from BillingHead bHd where bHd.billStatus " +
            "in ('INVOICED', 'PAID', 'DEFERRED') group by bHd.userAccount, bHd.subscriptionId)")
    List<SubscriptionInfoTemplate> findActiveSubscriptionInfos(@Param("userAccount") User userAccount);

//    select * from customer_subscription subs, customer_subscription_mapping mapping
//    where subs.subscription_status='INACTIVE' AND mapping.customer_subscription_id=subs.id and rate_code='SSDT' and
//    str_to_date(value, '%Y-%m-%d') <= DATE("2020-05-01")
//    order by str_to_date(value, "%Y-%m-%d");

    //    select * from customer_subscription subs, customer_subscription_mapping mapping
//    where subs.subscription_status='INACTIVE' AND mapping.customer_subscription_id=subs.id and rate_code='SSDT' and
//    str_to_date(value, "%Y-%m-%d") <= DATE("2020-07-01");
    @Query("SELECT subs, mapping FROM CustomerSubscription subs, CustomerSubscriptionMapping mapping " +
            "WHERE " +
            "subs.subscriptionStatus='INACTIVE' AND " +
            "mapping.subscription.id=subs.id AND " +
            "mapping.rateCode='SSDT' AND " +
            "str_to_date(mapping.value, '%Y-%m-%d') <= :toDate " +
            "ORDER BY str_to_date(mapping.value, '%Y-%m-%d')")
    List<CustomerSubscription> findInactiveSubscriptionsTillToday(@Param("toDate") Date toDate);

    @Query("SELECT subs, mapping FROM CustomerSubscription subs, CustomerSubscriptionMapping mapping " +
            "WHERE " +
            "subs.subscriptionRateMatrixId = :subscriptionRateMatrixId AND " +
            "subs.subscriptionStatus='INACTIVE' AND " +
            "mapping.subscription.id=subs.id AND " +
            "mapping.rateCode='SSDT' AND " +
            "str_to_date(mapping.value, '%Y-%m-%d') <= :toDate " +
            "ORDER BY str_to_date(mapping.value, '%Y-%m-%d')")
    List<CustomerSubscription> findInactiveSubscriptionsTillTodayForGarden(@Param("toDate") Date toDate, @Param("subscriptionRateMatrixId") Long subscriptionRateMatrixId);

    @Query("select cs from CustomerSubscription cs where cs.subscriptionStatus = :status")
    List<CustomerSubscription> findBySubscriptionStatus(String status);

    @Query("select cs.id from CustomerSubscription cs where cs.subscriptionStatus = :status")
    List<Long> findIdsBySubscriptionStatus(String status);

//    @Query("select cs from CustomerSubscription cs LEFT JOIN FETCH cs.billingHeads where cs.id = :id")
//    CustomerSubscription findCustomerSubscriptionByIdFetchBillingHeads(Long id);

    /**
     * SUBSCRIPTION TERMINATION QUERIES
     */

    /**
     * If termination date is less than roll over date
     * Gets the Subscription ready for termination
     * STEP: 1
     *
     * @param subsStatus
     * @param rateCode
     * @return
     */
/*    @Query("SELECT subs FROM CustomerSubscription subs, CustomerSubscriptionMapping mapping " +
            " WHERE subs.id= mapping.subscription.id " +
            " AND subs.subscriptionStatus=:subsStatus " +
            " AND mapping.rateCode =:rateCode " +
            " AND (subs.terminationDate is not null AND subs.terminationDate <=CURRENT_DATE and subs.terminationDate < " +
            "date(mapping.value))")
    List<CustomerSubscription> adhocSubscriptionTermination(@Params("subsStatus") String subsStatus,
                                                            @Params("rateCode") String rateCode);*/

    /**
     * If there's no Auto roll over,
     * then subscription will me terminated on respective date
     * <p>
     * TO BE SCHEDULED REGULAR
     *
     * @param rateCode
     * @param value
     * @return
     */
    @Query(value = "select subs.id , subs.subscription_status as subscriptionStatus,subs.end_date as endDate, subs.start_date as startDate, " +
            " subs.updated_at as updateDate, cast(DATE_ADD(subs.end_date, INTERVAL 1 DAY) AS DATE) as terminationDate,subs.closed_date as closedDate, " +
            " subs.termination_reason as terminationReason,subs.account_id as accountId, " +
            " DATE_SUB(subs.end_date, INTERVAL 15 DAY) as notifyDate " +
            " from customer_subscription subs , " +
            " customer_subscription_mapping mapping " +
            " where subs.id= mapping.customer_subscription_id " +
            " and subs.subscription_status ='ACTIVE' " +
            " and subs.termination_date is null " +
            " and mapping.rate_code = :rateCode " +
            " and mapping.value = :value " +
            " and coalesce(subs.termination_notification_sent,null,0)<>1 " +
            " and CURRENT_DATE = DATE_SUB(subs.end_date, INTERVAL 15 DAY)", nativeQuery = true)
//    @Query(value = "select subs.id as id, subs.subscription_status as subscriptionStatus,subs.end_date as endDate, " +
//            "subs.start_date as startDate," +
//            "     subs.updated_at as updateDate,cast(DATE_ADD(subs.end_date, INTERVAL 1 DAY) AS DATE) as " +
//            "terminationDate,subs.closed_date as closedDate," +
//            "     subs.termination_reason as terminationReason,subs.account_id as accountId," +
//            "     DATE_SUB(subs.end_date, INTERVAL 15 DAY) as notifyDate " +
//            "     from customer_subscription subs , " +
//            "     customer_subscription_mapping mapping " +
//            "     where subs.id= mapping.customer_subscription_id" +
//            "     and subs.subscription_status ='ACTIVE'" +
//            "     and subs.termination_date is null " +
//            "     and mapping.rate_code =:rateCode and mapping.value =:value" +
//            "     and coalesce(subs.termination_notification_sent,null,0)<>1 " +
//            "     and CURRENT_DATE = DATE_SUB(subs.end_date, INTERVAL 15 DAY)", nativeQuery = true)
    List<SubscriptionTerminationTemplate> getAllAutoTerminationNotification(@Param("rateCode") String rateCode,
                                                                            @Param("value") String value);

/*
    @Query(value = "select subs.* " +
            "     from customer_subscription subs , " +
            "     customer_subscription_mapping mapping " +
            "     where subs.id= mapping.customer_subscription_id " +
            "     and subs.subscription_status ='ACTIVE' " +
            "     and mapping.rate_code =:rateCode and mapping.value =:value " +
            "     and subs.termination_date is null  " +
            "     and cast(DATE_ADD(subs.end_date, INTERVAL 1 DAY) AS DATE) <= current_date() ", nativeQuery = true)
    List<CustomerSubscription> getAllAutoSubscriptionTerminationOnEndDate(@Params("rateCode") String rateCode, @Params(
            "value") String value);*/


    @Query(" SELECT MAX(bh.id) as maxBillId " +
            "    FROM BillingHead bh, CustomerSubscription subs " +
            "    where bh.subscriptionId = subs.id " +
            "    and subs.subscriptionStatus=:subsStatus " +
            "    and bh.billStatus in (:billStatuses) " +
            "    and bh.invoiceDate < :terminationDate " +
            "    order by bh.id")
    List<Long> getAdhocTerminationFutureInvoicingDate(@Param("subsStatus") String subsStatus,
                                                      @Param("billStatuses") List<String> billStatuses, @Param(
            "terminationDate") Date terminationDate);

    @Query("SELECT subs FROM CustomerSubscription subs, CustomerSubscriptionMapping mapping " +
            " WHERE subs.id= mapping.subscription.id " +
            " AND subs.subscriptionStatus=:subsStatus " +
            " AND mapping.rateCode =:rateCode " +
            " AND (CURRENT_DATE < str_to_date(:terminationDate, '%Y-%m-%d') and date(mapping.value) > str_to_date" +
            "(:terminationDate, '%Y-%m-%d') )" +
            " AND subs.id=:subsId")
    CustomerSubscription checkAdhocSubscriptionTerminationDate(@Param("subsStatus") String subsStatus, @Param(
            "rateCode") String rateCode, @Param("subsId") Long subsId,
                                                               @Param("terminationDate") String terminationDate);

    @Transactional
    @Modifying
    @Query("update CustomerSubscription cs set cs.terminationDate = :terminationDate , cs" +
            ".terminationReason=:terminationReason where cs.id=:subscriptionId")
    void updateTerminationDateAndReason(@Param("terminationDate") Date terminationDate,
                                        @Param("terminationReason") String terminationReason,
                                        @Param("subscriptionId") Long subscriptionId);

    @Query(value = "select subs.id , cast(DATE_ADD(subs.end_date, INTERVAL 1 DAY) AS DATE) as terminationDate, mapping.value " +
            " from customer_subscription subs , " +
            " customer_subscription_mapping mapping " +
            " where subs.id= mapping.customer_subscription_id " +
            " and (subs.subscription_status ='ACTIVE' OR subs.subscription_status ='ENDED')" +
            " and subs.termination_date is null " +
            " and mapping.rate_code =:rateCode and mapping.value =:value " +
            " and subs.id=:subsId ", nativeQuery = true)
    SubscriptionTerminationTemplate getAutoTerminationDate(@Param("rateCode") String rateCode,
                                                           @Param("value") String value, @Param("subsId") Long subsId);

    /**
     * SubscriptionTermination
     * Query
     *
     * @return
     */
    @Query(value = "SELECT subs.id, subs.termination_date as autoDate  FROM customer_subscription subs,  " +
            " customer_subscription_mapping mapping " +
            " WHERE subs.id= mapping.customer_subscription_id " +
            " AND subs.subscription_status= 'ACTIVE' " +
            " AND mapping.rate_code ='ROLLDT' " +
            " AND (subs.termination_date is not null AND subs.termination_date <=current_date() " +
            " and subs.termination_date < date(mapping.value)) " +
            " union            " +
            " select subs.id, cast(DATE_ADD(subs.end_date, INTERVAL 1 DAY) AS DATE) as autoDate " +
            " from customer_subscription subs , customer_subscription_mapping mapping " +
            " where subs.id= mapping.customer_subscription_id " +
            " and subs.subscription_status ='ACTIVE' " +
            " and mapping.rate_code = 'ROLL' " +
            " and mapping.value = 'NO' " +
            " and subs.termination_date is null " +
            " and cast(DATE_ADD(subs.end_date, INTERVAL 1 DAY) AS DATE) <= current_date() ", nativeQuery = true)
    List<SubscriptionTerminationTemplate> terminationBatchQuery();

    // Delete subsription
    @Query("SELECT subs FROM CustomerSubscription subs inner join CustomerSubscriptionMapping mapping" +
            "   on mapping.subscription = subs" +
            "       where" +
            "           subs.userAccount in (:userAccounts) AND" +
            "           subs.subscriptionStatus = 'INACTIVE' AND" +
            "           mapping.rateCode = 'PN' AND" +
            "           mapping.value = '-1'")
    List<CustomerSubscription> getMarkedForDeletionByAccounts(@Param("userAccounts") List<User> userAccounts);

    @Query("SELECT subs FROM CustomerSubscription subs inner join CustomerSubscriptionMapping mapping" +
            "   on mapping.subscription = subs" +
            "       where" +
            "           subs.subscriptionType in (:subscriptionTypes) AND" +
            "           subs.subscriptionStatus = 'INACTIVE' AND" +
            "           mapping.rateCode = 'PN' AND" +
            "           mapping.value = '-1'")
    List<CustomerSubscription> getMarkedForDeletionBySubscriptionType(@Param("subscriptionTypes") List<String> subscriptionTypes);

    @Query("SELECT subs FROM CustomerSubscription subs inner join CustomerSubscriptionMapping mapping" +
            "   on mapping.subscription = subs" +
            "       where" +
            "           subs.id in (:subscriptionIds) AND" +
            "           subs.subscriptionStatus = 'INACTIVE' AND" +
            "           mapping.rateCode = 'PN' AND" +
            "           mapping.value = '-1'")
    List<CustomerSubscription> getMarkedForDeletionBySubscriptionIds(@Param("subscriptionIds") List<Long> subscriptionIds);

    @Query("SELECT subs FROM CustomerSubscription subs inner join CustomerSubscriptionMapping mapping" +
            "   on mapping.subscription = subs" +
            " inner join SubscriptionRateMatrixDetail detail" +
            "    on detail.subscriptionRateMatrixHead = subs.subscriptionRateMatrixId" +
            "       where" +
            "           detail.defaultValue in (:gardenSRCs) AND" +
            "           subs.subscriptionStatus = 'INACTIVE' AND" +
            "           mapping.rateCode = 'PN' AND" +
            "           mapping.value = '-1'")
    List<CustomerSubscription> getMarkedForDeletionByGardenSRCs(@Param("gardenSRCs") List<String> gardenSRCs);

    @Query(value = "SELECT cs.* from  customer_subscription as cs " +
            "inner join subscription_rate_matrix_head as smh " +
            "on  cs.subscription_rate_matrix_id  = smh.id " +
            "where cs.account_id = :userId and smh.subscription_type_id in( :subscriptionTypes ) ", nativeQuery = true)
    List<CustomerSubscription> findCustomerInverterSubscription(@Param("subscriptionTypes") List<Long> subscriptionTypes, Long userId);

    @Query("select distinct(cs) from CustomerSubscription cs LEFT JOIN FETCH cs.customerSubscriptionMappings where cs.id in (:ids)")
    List<CustomerSubscription> findAllByIdInFetchCustomerSubscriptionMappings(@Param("ids") List<Long> ids);

    @Query("select distinct(cs) from CustomerSubscription cs LEFT JOIN FETCH cs.customerSubscriptionMappings where cs.userAccount.id in (:ids)")
    List<CustomerSubscription> findAllByUserIdInFetchCustomerSubscriptionMappings(@Param("ids") List<Long> ids);

    @Query("select distinct(cs) from CustomerSubscription cs LEFT JOIN FETCH cs.customerSubscriptionMappings where cs.userAccount.id = (:id)")
    List<CustomerSubscription> findAllByUserIdInFetchCustomerSubscriptionMappings(@Param("id") Long id);

    @Query("Select distinct(cs.extSubsId) from CustomerSubscription  cs where cs.id in (:ids)")
    List<String> findExtSubsIdListById(@Param("ids") List<Long> ids);

    @Query("select cs from CustomerSubscription cs where cs.extSubsId=:subsId")
    CustomerSubscription findByExtSubsId(@Param("subsId") String subsId);

    @Query(value = "select premiseNo,variantName,userAcctId,activeSince,subId,status,variantId,subName,siteLocationId " +
            "from(with customer_subscriptions as(select * from customer_subscription cs " +
            "where cs.account_id in (:userId) and cs.ext_subs_id is not null) " +
            "SELECT Json_unquote(Json_extract(def.mp_json, '$.S_PN')) AS premiseNo, " +
            "def.ref_type AS variantName,cs.account_id As userAcctId,  str_to_date(cs.start_date,'%Y-%m-%d')  AS activeSince, cs.ext_subs_id AS subId, " +
            "def.subs_status AS status , def.ref_id as variantId,def.subscription_name as subName, def.site_location_id as siteLocationId " +
            "FROM customer_subscriptions cs " +
            "       INNER JOIN ext_data_temp_stage temp " +
            "               ON temp.subs_id = cs.ext_subs_id " +
            "       INNER JOIN ext_data_stage_definition def " +
            "               ON def.subs_id = temp.subs_id) as temp ", nativeQuery = true)
    List<SubscriptionDetailTemplate> findSubsByUserId(@Param("userId") Long userId);
    @Query(value="select cs.ext_subs_id from customer_subscription cs INNER JOIN ext_data_stage_definition def on def.subs_id = cs.ext_subs_id " +
            " where cs.garden_src=:gardenSRC and Json_unquote(Json_extract(def.mp_json, '$.S_PN')) = :premiseNo",nativeQuery = true)
    String findSubsByGardenSRCAndPremiseNo(@Param("gardenSRC") String gardenSRC,@Param("premiseNo") String premiseNo);

    @Query("select cs from CustomerSubscription cs where cs.extSubsId in (:projectionIds) and cs.subscriptionTemplate in (:variantIds)")
    List<CustomerSubscription> findProjectionByProjectionIdsAndVariantIds(@Param("projectionIds") List<String> projectionIds,@Param("variantIds") List<String> variantIds);

    @Query("SELECT c FROM CustomerSubscription c WHERE c.extSubsId = :subscriptionIds ")
    CustomerSubscription findBySubscriptionIds(@Param("subscriptionIds") String subscriptionIds);

    @Query(value = "Select "
            + "Json_unquote(Json_extract(def.mp_json, '$.S_PN')) AS premiseNo, "
            + "def.ref_type AS variantName, "
            + "ulp.account_id AS userAcctId, "
            + "str_to_date(cs.start_date, '%Y-%m-%d') AS active, "
            + "cs.ext_subs_id AS subId, "
            + "cs.subscription_status AS status, "
            + "def.ref_id AS variantId, "
            + "def.subscription_name AS subName, "
            + "def.site_location_id AS siteLocationId, "
            + "def.cust_add AS customerAddress, "
            + "ploc.add1, "
            + "ploc.add2, "
            + "ploc.ext1, "
            + "ploc.ext2, "
            + "ploc.zip_code as zipCode "
            + "FROM user_level_privilege ulp "
            + "INNER JOIN entity e ON ulp.entity_id = e.id "
            + "INNER JOIN customer_subscription cs ON cs.account_id = ulp.account_id "
            + "INNER JOIN ext_data_temp_stage temp ON temp.subs_id = cs.ext_subs_id "
            + "INNER JOIN ext_data_stage_definition def ON def.subs_id = temp.subs_id "
            + "INNER JOIN physical_locations ploc ON def.cust_add = ploc.id "
            + "WHERE e.id = :entityId AND ulp.role_id IS NULL", nativeQuery = true)
    List<SubscriptionDetailTemplate> findSubsByEntityId(Long entityId);

}
