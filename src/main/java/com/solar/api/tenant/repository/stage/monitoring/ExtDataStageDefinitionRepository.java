package com.solar.api.tenant.repository.stage.monitoring;

import com.solar.api.tenant.mapper.customerSupport.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.projection.projectrevenue.ProjectProjectionRevenue;
import com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExtDataStageDefinitionRepository extends JpaRepository<ExtDataStageDefinition, Long> {
    boolean existsBySubsId(String subsId);

    ExtDataStageDefinition findBySubsId(String subId);

    List<ExtDataStageDefinition> findAllBySubsStatus(String subsStatus);

    List<ExtDataStageDefinition> findAllBySubsStatusAndMonPlatform(String subsStatus, String monPlatform);
    List<ExtDataStageDefinition> findAllBySubsStatusAndMonPlatformAndSubsIdIn(String subsStatus, String monPlatform,List<String> subsId);

    List<ExtDataStageDefinition> findAllBySubsIdInAndSubsStatus(List<String> subIds,String subsStatus);

    @Query("select edsd from ExtDataStageDefinition edsd " +
            "join CustomerSubscription cs on cs.extSubsId = edsd.subsId " +
            "where edsd.subsStatus = :subsStatus and  cs.userAccount.acctId IS NOT NULL and edsd.refId in (:variantIds) ")
    List<ExtDataStageDefinition> findAllBySubsStatusAndRefIdIn(String subsStatus, List<String> variantIds);

    @Query("SELECT DISTINCT new com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO(cs.userAccount.acctId, ulp.entity.id, " +
            "concat(concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName), concat(' - ', edsd.refType)), edsd.refId) " +
            " FROM ExtDataStageDefinition edsd " +
            "JOIN CustomerSubscription cs ON cs.extSubsId = edsd.subsId " +
            "JOIN UserLevelPrivilege ulp ON ulp.user.acctId = cs.userAccount.acctId " +
            "WHERE edsd.refId IN (:variantIds) and cs.userAccount.acctId = :acctId")
    List<InverterSubscriptionDTO> findAllInverterSubscriptionDTOForCurrentUsers(@Param("variantIds") List<String> variantIds, @Param("acctId") Long acctId);

    @Query("SELECT DISTINCT new com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO(cs.userAccount.acctId, ulp.entity.id, " +
            "concat(concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName), concat(' - ', edsd.refType)), edsd.refId) " +
            " FROM ExtDataStageDefinition edsd " +
            "JOIN CustomerSubscription cs ON cs.extSubsId = edsd.subsId " +
            "JOIN UserLevelPrivilege ulp ON ulp.user.acctId = cs.userAccount.acctId " +
            "WHERE edsd.refId IN (:variantIds)")
    List<InverterSubscriptionDTO> findAllInverterSubscriptionDTO(@Param("variantIds") List<String> variantIds);


    @Query("select edsd from ExtDataStageDefinition edsd " +
            "join CustomerSubscription cs on cs.extSubsId = edsd.subsId " +
            "where cs.userAccount.acctId = :acctId " +
            "and edsd.id IN (SELECT MIN(edsd2.id)  FROM ExtDataStageDefinition edsd2 " +
            "GROUP BY edsd2.refId)")
    List<ExtDataStageDefinition> findAllForCurrentUsers(@Param("acctId") Long acctId);

    @Query("select edsd from ExtDataStageDefinition edsd " +
            "join CustomerSubscription cs on cs.extSubsId = edsd.subsId " +
            "where edsd.subsStatus = :subsStatus and cs.userAccount.acctId = :acctId and edsd.refId in (:variantIds)")
    List<ExtDataStageDefinition> findAllBySubsStatusAndRefIdAndAcctIdIn(@Param("subsStatus") String subsStatus,
                                                                        @Param("variantIds") List<String> variantIds, @Param("acctId") Long acctId);

    @Query("SELECT DISTINCT edsd.monPlatform FROM ExtDataStageDefinition edsd")
    List<String> getDistinctByMonPlatform();

    List<ExtDataStageDefinition> getAllByMonPlatformIn(List<String> mpPlatforms);


    @Query("select new com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO(cs.userAccount.acctId, ulp.entity.id, edsd.groupId, edsd.refId," +
            "concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName), cs.extSubsId, edsd.monPlatform, edsd.refType, entd.uri,edsd.subscriptionName,edsd.systemSize,edsd.custAdd) " +
            "from ExtDataStageDefinition edsd " +
            "join CustomerSubscription cs on cs.extSubsId = edsd.subsId " +
            "join UserLevelPrivilege ulp on ulp.user.acctId = cs.userAccount.acctId " +
            "join EntityDetail  entd on entd.entity.id = ulp.entity.id " +
            "group by cs.userAccount.acctId, ulp.entity.id, concat(concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName), concat(' - ', edsd.refType)), cs.extSubsId, edsd.monPlatform,edsd.refType, entd.uri,edsd.subscriptionName,edsd.systemSize,edsd.custAdd,edsd.refId,edsd.groupId " +
            "having edsd.monPlatform in(:mps) ")
    List<InverterSubscriptionDTO> findAllCustomerSubscriptionDTO(List<String> mps);

    @Query("select edsd from ExtDataStageDefinition edsd " )
    List<ExtDataStageDefinition> findAllUniqueProjects();

    @Query("select new com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO(edsd,cs.userAccount.acctId) from ExtDataStageDefinition edsd " +
            "join CustomerSubscription cs on cs.extSubsId = edsd.subsId " +
            "where edsd.subsId in (:subIds)")
    List<ExtDataStageDefinitionDTO> findAllSubsAndCustomer(List<String> subIds);


    @Query(value = "SELECT GROUP_CONCAT(cs.ext_subs_id SEPARATOR ',') FROM ext_data_stage_definition edsd " +
            " inner join customer_subscription cs on cs.ext_subs_id = edsd.subs_id" +
            " WHERE cs.account_id = :acctId and edsd.ref_id = :variantId ", nativeQuery = true)
    String findAllSubscriptionsByAcctIdAndVariantId(@Param("variantId") String variantId,@Param("acctId") Long acctId);
    @Query("select new com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO(edsd.refId,edsd.refType) " +
            "from ExtDataStageDefinition edsd " +
            "where edsd.id IN (SELECT MIN(edsd2.id)  FROM ExtDataStageDefinition edsd2 " +
            "GROUP BY edsd2.refId)")
    List<DataDTO> findAllProjectsForFilters();

    @Query("select new com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO(edsd.subsId,concat(concat(edsd.subscriptionName, ' - '), edsd.refType)) " +
            "from ExtDataStageDefinition edsd " +
            "where edsd.refId in (:variantIds)")
    List<DataDTO> findAllSubscriptionsByVariantIdForFilters(@Param("variantIds") List<String> variantIds);

    @Query("select new com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO(cs.userAccount.acctId, concat(concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName), concat(' - ', edsd.refType)),ed.uri,cs.subscriptionTemplate ) " +
            "from CustomerSubscription cs " +
            "join UserLevelPrivilege ulp on ulp.user.acctId = cs.userAccount.acctId " +
            "left join EntityDetail ed on ed.entity.id = ulp.entity.id " +
            "left join ExtDataStageDefinition edsd on edsd.refId = cs.subscriptionTemplate " +
            "where cs.subscriptionTemplate in (:variantIds) and cs.id in (select MIN(cs2.id) from CustomerSubscription cs2 where cs2.subscriptionTemplate = cs.subscriptionTemplate GROUP BY cs2.userAccount.acctId )")
    List<DataDTO> findAllCustomersByVariantIdForFilters(@Param("variantIds") List<String> variantIds);

    @Query(value =
            "SELECT edsd.* " +
                    "FROM ext_data_stage_definition edsd " +
                    "WHERE Json_unquote(Json_extract(edsd.mp_json, '$.PRJIND')) = 'true' " +
                    "AND Json_unquote(Json_extract(edsd.mp_json, '$.PRJPRD')) = 'Monthly' " +
                    "AND Json_unquote(Json_extract(edsd.mp_json, '$.MNTSUBIN')) = 'true' "
//                    "AND edsd.id = (" +
//                    "SELECT ed.id " +
//                    "FROM ext_data_stage_definition ed " +
//                    "WHERE ed.subs_id = edsd.subs_id " +
//                    "ORDER BY ed.id DESC " +
//                    "LIMIT 1 )"
            , nativeQuery = true)
    List<ExtDataStageDefinition> findMonthlyProjectionsForAllGardens();

    @Query("select edsd from ExtDataStageDefinition edsd inner join CustomerSubscription cs " +
            "on cs.extSubsId=edsd.subsId where cs.id=:subsId ")
    ExtDataStageDefinition findByCustomerSubscriptionId(@Param("subsId") Long subsId);

    @Query(value = "SELECT Json_unquote(Json_extract(edsd.mp_json, '$.PRCTNGEFF')) AS efficiency, bh.amount AS totalAmount," +
            " DATE_FORMAT(STR_TO_DATE(CONCAT('01-', bh.billing_month_year), '%d-%m-%Y'), '%M %Y') AS billingMonth " +
            "FROM  ext_data_stage_definition edsd INNER JOIN customer_subscription cs ON cs.ext_subs_id = edsd.subs_id " +
            "LEFT JOIN billing_head bh ON bh.subscription_id = cs.id WHERE cs.subscription_template =:variantId " +
            "AND Json_unquote(Json_extract(edsd.mp_json, '$.PRJIND')) = 'true' " +
            "AND Json_unquote(Json_extract(edsd.mp_json, '$.PRJPRD')) = 'Monthly' " +
            "AND Json_unquote(Json_extract(edsd.mp_json, '$.MNTSUBIN')) = 'true' " +
            "AND bh.amount IS NOT NULL " +
            "ORDER BY efficiency, STR_TO_DATE(CONCAT('01-', bh.billing_month_year), '%d-%m-%Y'), totalAmount ASC", nativeQuery = true)
    List<ProjectProjectionRevenue> getProjectProjectionRevenueDetails(@Param("variantId") String variantId);

    @Query("select new com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO(edsd.refId, count(edsd.subsId) ) from ExtDataStageDefinition edsd " +
            "join CustomerSubscription cs on cs.extSubsId = edsd.subsId " +
            "where edsd.subsStatus = :subsStatus and  cs.userAccount.acctId IS NOT NULL and edsd.refId in (:variantIds) " +
            "group by edsd.refId")
    List<DataDTO> findSubCountBySubsStatusAndRefIdIn(String subsStatus, List<String> variantIds);

    @Query("SELECT e FROM ExtDataStageDefinition e WHERE e.subsId = :subscriptionIds")
    ExtDataStageDefinition findBySubscriptionIds(@Param("subscriptionIds") String subscriptionIds);
    @Modifying
    @Transactional
    @Query("UPDATE ExtDataStageDefinition SET mpJson = :modifiedMpJson WHERE id = :extDataStageDefinitionId")
    void updateMpJson(@Param("extDataStageDefinitionId") Long extDataStageDefinitionId, @Param("modifiedMpJson") String modifiedMpJson);

    @Query("select ext from ExtDataStageDefinition ext where ext.mpJson like '%\"PRJ_STATUS\"%'")
    List<ExtDataStageDefinition> findByProjectStatusInMpJson();

    @Query("select new  com.solar.api.tenant.mapper.customerSupport.CustomerSubscriptionDTO(cs.userAccount.acctId, ulp.entity.id, edsd.groupId, edsd.refId," +
            "concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName), cs.extSubsId, edsd.monPlatform, edsd.refType, entd.uri,edsd.subscriptionName,edsd.custAdd, custDtl.customerType, docu.uri) " +
            "from ExtDataStageDefinition edsd " +
            "join CustomerSubscription cs on cs.extSubsId = edsd.subsId " +
            "join UserLevelPrivilege ulp on ulp.user.acctId = cs.userAccount.acctId " +
            "left join DocuLibrary docu on docu.codeRefId = edsd.refId " +
            "and docu.codeRefType = 'VAR_THMB' " +
            "and docu.visibilityKey = true " +
            "left join EntityDetail  entd on entd.entity.id = ulp.entity.id " +
            "left join CustomerDetail  custDtl on custDtl.entityId = ulp.entity.id " +
            "group by cs.userAccount.acctId, ulp.entity.id, concat(concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName), concat(' - ', edsd.refType)), cs.extSubsId, edsd.monPlatform,edsd.refType, entd.uri,edsd.subscriptionName,edsd.custAdd,edsd.refId,edsd.groupId,custDtl.customerType, docu.uri " +
            "having cs.userAccount.acctId IS NOT NULL"
    )
    List<CustomerSubscriptionDTO> findAllCustomersWithSubscriptions();

    @Query("select new com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO(edsd.refId,edsd.monPlatform," +
            "edsd.refType,edsd.subsStatus,edsd.id,edsd.brand,edsd.extJson, edsd.mpJson,docu.uri) " +
            "from ExtDataStageDefinition edsd " +
            "left join DocuLibrary docu on docu.codeRefId = edsd.refId " +
            "and docu.codeRefType = 'VAR_THMB' " +
            "and docu.visibilityKey = true " +
            "where edsd.groupId = :productId")
    List<ExtDataStageDefinitionDTO> findAllUniqueGardenByProductId(@Param("productId") String productId);
    ExtDataStageDefinition findTopByRefId(String refId);
}
