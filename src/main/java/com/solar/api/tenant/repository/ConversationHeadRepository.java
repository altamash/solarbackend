package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.customerSupport.ConversationHeadCustomersTemplateDTO;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadTemplateDTO;
import com.solar.api.tenant.mapper.customerSupport.ConversationHeadTemplateWoDTO;
import com.solar.api.tenant.mapper.tiles.customersupportmanagement.CustomerSupportFiltersTemplate;
import com.solar.api.tenant.mapper.tiles.workorder.*;
import com.solar.api.tenant.mapper.tiles.workorder.WorkOrderCustomerDetailTile;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationHeadRepository extends JpaRepository<ConversationHead, Long> {

    List<ConversationHead> findAllByRaisedBy(Long id);

    ConversationHead findBySourceId(String sourceId);

    ConversationHead findBySourceIdAndCategory(String sourceId, String category);

    List<ConversationHead> findBySourceTypeAndSourceId(String sourceType, String sourceId);

    @Query(value = "SELECT c.id,c.summary,c.message,c.category,c.sub_category as subCategory,c.priority,c.status,c.subscription_id as subscriptionId,c.source_id as sourceId,c.internal,c.variant_id as variantId, " +
            "e.entity_name as createdBy,c.customer_id as customerId, e1.entity_name as customerName,ed.uri as createdByUri,ed1.uri as customerUri, " +
            " e1.entity_type as customerType,e1.contact_person_email as customerEmail,e1.contact_person_phone as customerContact, " +
            "e.entity_type as createdByType,e.contact_person_email as createdByEmail,e.contact_person_phone as createdByContact " +
            "FROM conversation_head c " +
            "Left Join entity e on c.raised_by = e.id " +
            "Left Join entity e1 on c.customer_id = e1.id " +
            "Left Join entity_detail ed on e.id =ed.entity_id " +
            "Left Join entity_detail ed1 on e1.id =ed1.entity_id", nativeQuery = true)
    List<ConversationHeadTemplateWoDTO> findAllTickets();

    @Query(value = "SELECT c.id,c.summary,c.message,c.category,c.sub_category as subCategory,c.priority,c.status,c.subscription_id as subscriptionId,c.source_id as sourceId,c.internal,c.variant_id as variantId, " +
            "e.entity_name as createdBy,c.customer_id as customerId, e1.entity_name as customerName,ed.uri as createdByUri,ed1.uri as customerUri, " +
            " e1.entity_type as customerType,e1.contact_person_email as customerEmail,e1.contact_person_phone as customerContact, " +
            "e.entity_type as createdByType,e.contact_person_email as createdByEmail,e.contact_person_phone as createdByContact " +
            "FROM conversation_head c " +
            "Left Join entity e on c.raised_by = e.id " +
            "Left Join entity e1 on c.customer_id = e1.id " +
            "Left Join entity_detail ed on e.id =ed.entity_id " +
            "Left Join entity_detail ed1 on e1.id =ed1.entity_id " +
            "WHERE c.raised_by= :raisedBy", nativeQuery = true)
    List<ConversationHeadTemplateWoDTO> findAllTicketsByRaisedBy(Long raisedBy);

    @Query("select new com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO( c.id, c.summary, c.message, c.category, c.subCategory, c.priority, " +
            " c.sourceId, c.status, c.raisedBy, c.firstName, c.lastName ,c.role, " +
            "FUNCTION('DATE_FORMAT', c.createdAt, '%b %d, %Y %h:%i %p') , " +
            "ed.uri,dl.uri)  " +
            "FROM ConversationHead c " +
            "LEFT JOIN UserLevelPrivilege  ulp ON ulp.user.acctId = c.raisedBy AND ulp.role.id IS NULL " +
            "LEFT JOIN EntityDetail  ed ON ed.entity.id = ulp.entity.id " +
            "LEFT JOIN ConversationReference cr ON cr.conversationHead.id = c.id " +
            "LEFT JOIN DocuLibrary dl ON dl.docuId = cr.referenceId " +
            "WHERE c.sourceId = :sourceId AND c.category = :category")
    List<ConversationHeadDTO> findAllBySourceIdAndCategory(String sourceId, String category);

    @Query("select new com.solar.api.tenant.mapper.customerSupport.ConversationHeadDTO( c.id, c.message, c.category, c.subCategory, c.priority, " +
            " c.sourceId)  " +
            "FROM ConversationHead c " +
            "WHERE c.sourceId = :sourceId AND c.category = :category")
    Page<ConversationHeadDTO> findAllBySourceIdAndCategoryV2(String sourceId, String category, Pageable pageable);



    @Query(value = "SELECT " +
            "c.id, c.summary, c.message, c.category, c.sub_category as subCategory, c.priority, " +
            "c.source_id as sourceId, c.status as status, c.raised_by as raisedBy, c.first_name as firstName, c.last_name as lastName, c.role as role, " +
            "DATE_FORMAT(c.created_at, '%b %d, %Y %h:%i %p') as formattedCreatedAt, " +
            "ed.uri as raisedByImgUri, c.assignee as assigneeEntityRoleId, " +
            "COALESCE(enAssignee.entity_name, 'unassigned') as assigneeEntityName, " +
            "COALESCE(erdAssignee.uri, '') as assigneeImgUri, " +
            "COALESCE(enSource.entity_name, '') as requesterName, " +
            "COALESCE(erdSource.uri, '') as requesterImgUri, " +
            "COALESCE(enSource.contact_person_email, '') as requesterEmail, " +
            "COALESCE(enSource.contact_person_phone, '') as requesterPhone, " +
            "COALESCE(cd.customer_type, '') as requesterType, " +
            "COALESCE(c.source_type, '') as sourceType, COALESCE(c.variant_id, '') as variantId, " +
            "(SELECT MAX(ext.ref_type) FROM ext_data_stage_definition ext WHERE ext.ref_id = c.variant_id) as gardenName, " +
            "COALESCE(c.subscription_id, '') as subscriptionId, " +
            "(SELECT MAX(ext.subscription_name) FROM ext_data_stage_definition ext WHERE ext.subs_id = c.subscription_id) as subscriptionName, " +
            "COALESCE((SELECT docu.uri FROM docu_library docu WHERE docu.code_ref_id = c.variant_id AND docu.code_ref_type = 'VAR_THMB' AND docu.visibility_key = true), '') as gardenImgUri " +
            "FROM conversation_head c " +
            "LEFT JOIN user_level_privilege ulp ON ulp.account_id = c.raised_by AND ulp.role_id IS NULL " +
            "LEFT JOIN entity_detail ed ON ed.entity_id = ulp.entity_id " +
            "LEFT JOIN entity_role er ON er.id = c.assignee " +
            "LEFT JOIN entity enAssignee ON enAssignee.id = er.entity_id " +
            "LEFT JOIN entity_detail erdAssignee ON erdAssignee.entity_id = er.entity_id " +
            "LEFT JOIN entity enSource ON enSource.id = CAST(c.source_id AS UNSIGNED) " +
            "LEFT JOIN entity_detail erdSource ON erdSource.entity_id = CAST(c.source_id AS UNSIGNED) " +
            "LEFT JOIN customer_detail cd ON cd.entity_id = CAST(c.source_id AS UNSIGNED) " +
            "WHERE (c.source_id = :sourceId OR :sourceId IS NULL) " +
            "AND (c.module = :moduleName OR :moduleName IS NULL) " +
            "AND (:searchWord IS NULL OR " +
            "c.source_type LIKE %:searchWord% OR " +
            "c.id IN (SELECT DISTINCT cSub.id FROM conversation_head cSub " +
            "LEFT JOIN entity_role erSub ON erSub.id = cSub.assignee " +
            "LEFT JOIN entity enAssigneeSub ON enAssigneeSub.id = erSub.entity_id " +
            "LEFT JOIN entity enSourceSub ON enSourceSub.id = CAST(cSub.source_id AS UNSIGNED) " +
            "WHERE enSourceSub.entity_name LIKE %:searchWord% OR " +
            "enAssigneeSub.entity_name LIKE %:searchWord%)) " +
            "AND (:groupByName IS NULL OR " +
            "(:groupBy = 'STATUS' AND COALESCE(c.status, '') = :groupByName) OR " +
            "(:groupBy = 'TICKET TYPE' AND COALESCE(c.source_Type , '') = :groupByName) OR " +
            "(:groupBy = 'REQUESTER' AND c.source_id IN (SELECT DISTINCT enSourceSub.id FROM entity enSourceSub WHERE enSourceSub.entity_name = :groupByName)) OR " +
            "(:groupBy = 'PRIORITY' AND COALESCE(c.priority, '') = :groupByName) OR " +
            "(:groupBy = 'CREATED BY' AND COALESCE(CONCAT(c.first_name, ' ', c.last_name), '') = :groupByName)) " +
            "AND (:priority IS NULL OR c.priority = :priority) " +
            "AND (:category IS NULL OR c.category = :category) " +
            "AND (:subCategory IS NULL OR c.sub_category = :subCategory) " +
            "AND (:ticketType IS NULL OR c.source_type = :ticketType) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "GROUP BY c.id, c.summary, c.message, c.category, c.sub_category, c.priority, " +
            "c.source_id, c.status, c.raised_by, c.first_name, c.last_name, c.role, " +
            "formattedCreatedAt, raisedByImgUri, assigneeEntityRoleId, assigneeEntityName, " +
            "assigneeImgUri, requesterName, requesterImgUri, requesterEmail, requesterPhone, " +
            "requesterType, sourceType, variantId, subscriptionId, subscriptionName, gardenImgUri " +
            "ORDER BY c.created_at DESC",  // This line is added for sorting
            countQuery = "SELECT COUNT(DISTINCT c.id) " +
                    "FROM conversation_head c " +
                    "LEFT JOIN entity_role er ON er.id = c.assignee " +
                    "LEFT JOIN entity enSource ON enSource.id = CAST(c.source_id AS UNSIGNED) " +
                    "WHERE (c.source_id = :sourceId OR :sourceId IS NULL) " +
                    "AND (c.module = :moduleName OR :moduleName IS NULL) " +
                    "AND (:searchWord IS NULL OR " +
                    "c.source_type LIKE %:searchWord% OR " +
                    "c.id IN (SELECT DISTINCT cSub.id FROM conversation_head cSub " +
                    "LEFT JOIN entity_role erSub ON erSub.id = cSub.assignee " +
                    "LEFT JOIN entity enAssigneeSub ON enAssigneeSub.id = erSub.entity_id " +
                    "LEFT JOIN entity enSourceSub ON enSourceSub.id = CAST(cSub.source_id AS UNSIGNED) " +
                    "WHERE enSourceSub.entity_name LIKE %:searchWord% OR " +
                    "enAssigneeSub.entity_name LIKE %:searchWord%)) " +
                    "AND (:groupByName IS NULL OR " +
                    "(:groupBy = 'STATUS' AND COALESCE(c.status, '') = :groupByName) OR " +
                    "(:groupBy = 'TICKET TYPE' AND COALESCE(c.source_Type , '') = :groupByName) OR " +
                    "(:groupBy = 'REQUESTER' AND c.source_id IN (SELECT DISTINCT enSourceSub.id FROM entity enSourceSub WHERE enSourceSub.entity_name = :groupByName)) OR " +
                    "(:groupBy = 'PRIORITY' AND COALESCE(c.priority, '') = :groupByName) OR " +
                    "(:groupBy = 'CREATED BY' AND COALESCE(CONCAT(c.first_name, ' ', c.last_name), '') = :groupByName)) " +
                    "AND (:priority IS NULL OR c.priority = :priority) " +
                    "AND (:category IS NULL OR c.category = :category) " +
                    "AND (:subCategory IS NULL OR c.sub_category = :subCategory) " +
                    "AND (:ticketType IS NULL OR c.source_type = :ticketType) " +
                    "AND (:status IS NULL OR c.status = :status)",
            nativeQuery = true)
    Page<ConversationHeadTemplateDTO> findAllByModuleAndSourceId(@Param("moduleName") String moduleName,
                                                                 @Param("sourceId") String sourceId,
                                                                 @Param("groupBy") String groupBy,
                                                                 @Param("groupByName") String groupByName,
                                                                 @Param("searchWord") String searchWord,
                                                                 @Param("ticketType") String ticketType,
                                                                 @Param("priority") String priority,
                                                                 @Param("category") String category,
                                                                 @Param("subCategory") String subCategory,
                                                                 @Param("status") String status,
                                                                 Pageable pageable);


    @Query(value = "SELECT DISTINCT " +
            "CAST(" +
            "CASE " +
            "WHEN :groupByType = 'STATUS' THEN COALESCE(ch.status, '') " +
            "WHEN :groupByType = 'TICKET TYPE' THEN COALESCE(ch.source_type, '') " +
            "WHEN :groupByType = 'REQUESTER' THEN COALESCE(en.entity_name, '') " +
            "WHEN :groupByType = 'PRIORITY' THEN COALESCE(ch.priority, '') " +
            "WHEN :groupByType = 'CREATED BY' THEN COALESCE(CONCAT(u.first_name, ' ', u.last_name), '') " +
            "ELSE '' END AS CHAR) " +
            " AS groupBy " +
            "FROM conversation_head as ch " +
            "LEFT JOIN entity en ON en.id = CAST(ch.source_id AS UNSIGNED) " +
            "LEFT JOIN user u ON u.acct_id = ch.raised_by " +
            "WHERE " +
            "(ch.source_id = :sourceId OR NULL IS NULL) AND " +
            "(ch.module = :moduleName) " +
            "AND CASE " +
            "WHEN :groupByType = 'STATUS' THEN (ch.status IS NOT NULL AND ch.status <> '') " +
            "WHEN :groupByType = 'TICKET TYPE' THEN (ch.source_type IS NOT NULL AND ch.source_type <> '') " +
            "WHEN :groupByType = 'REQUESTER' THEN (en.entity_name IS NOT NULL AND en.entity_name <> '') " +
            "WHEN :groupByType = 'PRIORITY' THEN (ch.priority IS NOT NULL AND ch.priority <> '') " +
            "WHEN :groupByType = 'CREATED BY' THEN (CONCAT(u.first_name, ' ', u.last_name) IS NOT NULL AND CONCAT(u.first_name, ' ', u.last_name) <> '') " +
            "ELSE FALSE END = TRUE"
            , nativeQuery = true)
    Page<ConversationHeadTemplateDTO> findAllByModuleAndSourceIdWithGrouping(@Param("groupByType") String groupBy,
                                                                             @Param("moduleName") String moduleName,
                                                                             @Param("sourceId") String sourceId,
                                                                             Pageable pageable);


    @Query(value = "SELECT DISTINCT  c.source_id as sourceId, group_concat(c.id) as headIds, count(c.id) as ticketCount,group_concat(c.source_type) as sourceType,group_concat(c.status) as status, " +
            " en.entity_name as requesterName,ed.uri as requesterImgUri, en.contact_person_email as requesterEmail, " +
            " en.contact_person_phone as requesterPhone, cd.customer_type as requesterType " +
            " FROM conversation_head c " +
            " LEFT JOIN entity en ON en.id = CAST(c.source_id AS UNSIGNED)" +
            " LEFT JOIN entity_detail ed ON ed.entity_id = en.id " +
            " LEFT JOIN customer_detail cd ON cd.entity_id = en.id " +
            " WHERE (c.source_id = :sourceId OR NULL IS NULL)" +
            " AND ((c.module = :moduleName)" +
            " AND (:searchWord IS NULL OR en.entity_name LIKE %:searchWord%))" +
            " GROUP BY c.source_id,en.entity_name,en.contact_person_email,en.contact_person_phone,ed.uri,cd.customer_type " +
            " having count(c.id) > 0 and not isnull(c.source_id) ", nativeQuery = true)
    List<ConversationHeadCustomersTemplateDTO> findAllUniqueRequesterByModule(@Param("moduleName") String moduleName,
                                                                              @Param("sourceId") String sourceId,
                                                                              @Param("searchWord") String searchWord);

    @Query(value = "SELECT " +
            "    GROUP_CONCAT(DISTINCT CASE WHEN c.source_type IS NOT NULL AND c.source_type <> '' THEN c.source_type END SEPARATOR ', ') AS sourceTypes, " +
            "    GROUP_CONCAT(DISTINCT CASE WHEN c.status IS NOT NULL AND c.status <> '' THEN c.status END SEPARATOR ', ') AS statuses, " +
            "    GROUP_CONCAT(DISTINCT CASE WHEN c.priority IS NOT NULL AND c.priority <> '' THEN c.priority END SEPARATOR ', ') AS priorities," +
            "    GROUP_CONCAT(DISTINCT CASE WHEN c.category IS NOT NULL AND c.category <> '' THEN c.category END SEPARATOR ', ') AS categories," +
            "    GROUP_CONCAT(DISTINCT CASE WHEN c.sub_category IS NOT NULL AND c.sub_category <> '' THEN c.sub_category END SEPARATOR ', ') AS subCategories " +
            "FROM" +
            "    conversation_head c ", nativeQuery = true)
    CustomerSupportFiltersTemplate findAllFiltersData();

    ConversationHead findBySubscriptionIdAndProductId(String subscriptionId, String productId);

    @Query(value = "select distinct ch.customer_id as accountId , ed.uri as  uri ,  " +
            "cd.customer_type as customerType ,e.contact_person_email as email, e.contact_person_phone as phoneNumber, " +
            "e.entity_name as name, e.id as entityId,group_concat(ch.id) as headIds, count(ch.id) as workOrderCount," +
            "group_concat(ch.source_type) as sourceType,group_concat(ch.status) as status," +
            "group_concat(ch.subscription_id) as workOrderIds," +
            "GROUP_CONCAT(DISTINCT o.organization_name) AS organizationNames  " +
            "from conversation_head ch " +
            "left join user_level_privilege ulp  " +
            "on ulp.account_id = ch.customer_id  and ulp.role_id is null " +
            "left join entity e  " +
            "on e.id = ulp.entity_id " +
            "left join entity_detail ed " +
            "on ed.entity_id = ulp.entity_id " +
            "left join customer_detail cd " +
            "on cd.entity_id = ulp.entity_id " +
            "LEFT JOIN  organization_detail od ON od.mongo_ref_id = ch.product_id " +
            "LEFT JOIN  organization o ON o.id = od.business_unit_id " +
            "where (ch.source_type = 'WorkOrder' or ch.source_type = 'Work Order') " +
            "and (ch.category = 'Customer Request' or ch.category = 'Customer Service') " +
            "GROUP BY ch.customer_id, ed.uri, cd.customer_type, e.contact_person_email, e.contact_person_phone, e.entity_name, e.id", nativeQuery = true)
    List<WorkOrderCustomerDetailTemplate> getCustomerListBySourceType();

    @Query(value = "select distinct ch.customer_id as customerId , edsd.ref_id as refId , edsd.ref_type as refType, edsd.product_name as productName, edsd.subs_status as subsStatus, " +
            " dl.uri as uri , group_concat(ch.id) as headIds, count(ch.id) as workOrderCount," +
            " group_concat(ch.source_type) as sourceType,group_concat(ch.status) as status," +
            "group_concat(ch.subscription_id) as workOrderIds," +
            " GROUP_CONCAT(DISTINCT o.organization_name) AS organizationNames " +
            "from conversation_head ch  " +
            "LEFT JOIN  organization_detail od ON od.mongo_ref_id = ch.product_id " +
            "LEFT JOIN  organization o ON o.id = od.business_unit_id " +
            "inner join ext_data_stage_definition edsd  " +
            "on edsd.ref_id = ch.variant_id  " +
            "left join docu_library dl  " +
            "on dl.code_ref_id = edsd.ref_id  " +
            "and dl.code_ref_type = 'VAR_THMB' " +
            "where (ch.source_type = 'WorkOrder' or ch.source_type = 'Work Order') " +
            "GROUP BY ch.customer_id, edsd.ref_id, edsd.ref_type, edsd.product_name, edsd.subs_status, dl.uri", nativeQuery = true)
    List<WorkOrderGardenDetailTemplate> getGardenListBySourceType();

    @Query(value = "select new com.solar.api.tenant.mapper.tiles.workorder.BusinessUnitInfoTile(e.entityName, e.contactPersonPhone, e.contactPersonEmail, ed.uri, org.organizationName  ) " +
            "from ConversationHead ch   " +
            "left join OrganizationDetail od   " +
            "on od.mongoRefId = ch.productId   " +
            "left join Organization org   " +
            "on org.id = od.businessUnitId   " +
            "left join Entity e   " +
            "on e.id = org.contactPerson.id   " +
            "left join EntityDetail ed   " +
            "on ed.entity.id = e.id   " +
            "where ch.subscriptionId = :subsId " +
            "and ( ch.sourceType = 'WorkOrder' or ch.sourceType = 'Work Order') ")
    BusinessUnitInfoTile findBusinessUnitInformationBySubsId(@Param("subsId") String subsId);

    @Query(value = "select distinct e.entity_name as entityName, edsd.ref_type as refType, edsd.subs_status as subsStatus, dl.uri as uri, ch.subscription_id as subscriptionId, (select Json_unquote(Json_extract(edsd.mp_json, '$.S_PSRC'))) as inverterNumber , pi.payment_source as paymentSource " +
            "from conversation_head ch " +
            "left join entity e " +
            "on e.id = ch.source_id " +
            "left join ext_data_stage_definition edsd " +
            "on edsd.subs_id = ch.subscription_id " +
            "left join docu_library dl " +
            "on dl.code_ref_id = edsd.subs_id " +
            "left join payment_info pi " +
            "on pi.id = Json_unquote(Json_extract(edsd.mp_json, '$.S_PSRC')) " +
            "where ch.subscription_id =  " +
            "(select subscription_id from conversation_head  where id =  " +
            "(select parent_request_id from conversation_head where subscription_id = :workOrderId and ( source_type = 'WorkOrder' or source_type = 'Work Order')));"
            , nativeQuery = true)
    SubscriptionInformation findSubscriptionsInformation(@Param("workOrderId") String workOrderId);


    //    customer requester
    @Query(value = "select distinct new com.solar.api.tenant.mapper.tiles.workorder.WorkOrderCustomerDetailTile(e.id,e.entityName, " +
            "e.contactPersonEmail,e.contactPersonPhone, cd.customerType,ed.uri )" +
            "from ConversationHead ch " +
            "left join Entity e ON ch.sourceId =Cast(e.id as string)  " +
            "left join CustomerDetail cd ON cd.entityId = e.id " +
            "left join EntityDetail ed ON ed.entity.id = e.id " +
            "where ch.sourceType = 'WorkOrder' " +
            "and (ch.category='Customer Request'or ch.category = 'customer support')" +
            "and ch.subscriptionId = :workOrderId")
    List<WorkOrderCustomerDetailTile> getRequesterInfoListBySourceType(@Param("workOrderId") String workOrderId);


//    service request

    @Query(value = "select distinct new com.solar.api.tenant.mapper.tiles.workorder.WorkOrderCustomerDetailTile(e.id,e.entityName, " +
            "e.contactPersonEmail,e.contactPersonPhone, e.entityType,ed.uri )" +
            "from ConversationHead ch " +
            "left join Entity e ON ch.sourceId =Cast(e.id as string)  " +
            "left join CustomerDetail cd ON cd.entityId = e.id " +
            "left join EntityDetail ed ON ed.entity.id = e.id " +
            "where e.entityType='Employee'" +
            "and ch.sourceType = 'WorkOrder' " +
            "and ch.category='Service Request'" +
            "and ch.subscriptionId = :workOrderId")
    List<WorkOrderCustomerDetailTile> getSourceManagerListBySourceType(@Param("workOrderId") String workOrderId);

    @Query(value = "SELECT ch.product_id as projectId, ch.subscription_id as workOrderId, ch.id as conversationHeadId, " +
            "ch.message as workOrderTitle, ch.category as workOrderType, ch.parent_request_id as ticketId, " +
            "o.organization_name as businessUnitName, ch.status as status, requesterULP.account_id as requesterAcctId, " +
            "requesterEntity.id as requesterEntityId, requesterEntity.entity_name as requesterName, " +
            "requesterEntityDetail.uri as requesterImage, " +
            "COALESCE(cusDetail.customer_type, requesterEntity.entity_type) as requesterType, " +
            "agentUlp.account_id as supportAgentAcctId, agentEntity.id as supportAgentEntityId, " +
            "agentEntity.entity_name as supportAgentName, agentEntityDetail.uri as supportAgentImage, " +
            "DATE_FORMAT(ch.planned_date, '%b %d, %Y') as plannedDate, ch.estimated_hours as timeRequired, " +
            "(SELECT COUNT(eg.id) FROM user_group ug LEFT JOIN entity_group eg ON eg.user_group_id = ug.id " +
            "AND eg.is_deleted = false WHERE ug.ref_id = ch.subscription_id AND ug.ref_type = 'workOrder') as assignedResources, " +
            "CASE WHEN ch.coverage IN ('Chargeable', 'Billable') THEN 'Yes' ELSE 'No' END as billable,:isLeaf as isLeaf " +
            "FROM conversation_head ch " +
            "LEFT JOIN organization_detail orgDetail ON orgDetail.mongo_ref_id = ch.product_id " +
            "LEFT JOIN organization o ON o.id = orgDetail.business_unit_id " +
            "LEFT JOIN user_level_privilege requesterULP ON requesterULP.entity_id = ch.source_id AND requesterULP.role_id IS NULL " +
            "LEFT JOIN entity requesterEntity ON requesterEntity.id = ch.source_id " +
            "LEFT JOIN entity_detail requesterEntityDetail ON requesterEntityDetail.entity_id = ch.source_id " +
            "LEFT JOIN customer_detail cusDetail ON cusDetail.entity_id = ch.source_id " +
            "LEFT JOIN entity_role agentRole ON agentRole.id = ch.assignee " +
            "LEFT JOIN entity agentEntity ON agentEntity.id = agentRole.entity_id " +
            "LEFT JOIN user_level_privilege agentUlp ON agentUlp.entity_id = agentRole.entity_id AND agentUlp.role_id IS NULL " +
            "LEFT JOIN entity_detail agentEntityDetail ON agentEntityDetail.entity_id = agentRole.entity_id " +
            "where ch.source_type = 'WorkOrder' and " +
            "(:status IS NULL OR ch.status = :status) AND " +
            "(:type IS NULL OR ch.category = :type) AND " +
            "(:requesterType IS NULL OR requesterEntity.entity_type = :requesterType) AND " +
            "(ch.source_id in (:requesterIds) or (:requesterIdPresent = false )) AND" +
            "(ch.assignee in (:agentIds) or (:agentIdPresent = false )) AND" +
            "(:billable IS NULL OR (CASE WHEN 'Yes' = :billable  THEN ch.coverage IN ('Chargeable', 'Billable') ELSE ch.coverage NOT IN ('Chargeable', 'Billable') END )) " +
            "AND (:groupBy IS NULL OR " +
            "(:groupBy = 'STATUS' AND ch.status = :groupByName) OR " +
            "(:groupBy = 'BOARD' AND o.organization_name = :groupByName) OR " +
            "(:groupBy = 'REQUESTER' AND requesterEntity.entity_name = :groupByName) OR " +
            "(:groupBy = 'SUPPORT AGENT' AND agentEntity.entity_name = :groupByName) OR " +
            "(:groupBy = 'BILLABLE' AND (CASE WHEN 'Yes' = :groupByName  THEN ch.coverage IN ('Chargeable', 'Billable') ELSE ch.coverage NOT IN ('Chargeable', 'Billable') END )) OR " +
            "(:groupBy = 'Type' AND ch.category = :groupByName) OR " +
            "(:groupBy = 'REQUESTER TYPE' AND requesterEntity.entity_type = :groupByName))",
            countQuery = "SELECT COUNT(ch.id) FROM conversation_head ch " +
                    "LEFT JOIN organization_detail orgDetail ON orgDetail.mongo_ref_id = ch.product_id " +
                    "LEFT JOIN organization o ON o.id = orgDetail.business_unit_id " +
                    "LEFT JOIN user_level_privilege requesterULP ON requesterULP.entity_id = ch.source_id AND requesterULP.role_id IS NULL " +
                    "LEFT JOIN entity requesterEntity ON requesterEntity.id = ch.source_id " +
                    "LEFT JOIN entity_role agentRole ON agentRole.id = ch.assignee " +
                    "LEFT JOIN entity agentEntity ON agentEntity.id = agentRole.entity_id " +
                    "WHERE ch.source_type = 'WorkOrder' " +
                    "AND (:status IS NULL OR ch.status = :status) " +
                    "AND (:type IS NULL OR ch.category = :type) " +
                    "AND (:requesterType IS NULL OR requesterEntity.entity_type = :requesterType) " +
                    "AND (ch.source_id in (:requesterIds) or (:requesterIdPresent = false)) " +
                    "AND (ch.assignee in (:agentIds) or (:agentIdPresent = false)) " +
                    "AND (:billable IS NULL OR (CASE WHEN 'Yes' = :billable THEN ch.coverage IN ('Chargeable', 'Billable') ELSE ch.coverage NOT IN ('Chargeable', 'Billable') END))",
            nativeQuery = true)
    Page<WorkOrderManagementTemplate> findAllWorkOrderManagementTemplate(@Param("groupBy") String groupBy,
                                                                         @Param("groupByName") String groupByName,
                                                                         @Param("status") String status,
                                                                         @Param("type") String type,
                                                                         @Param("requesterType") String requesterType,
                                                                         @Param("requesterIds") List<Long> requesterIds,
                                                                         @Param("requesterIdPresent") Boolean requesterIdPresent,
                                                                         @Param("agentIds") List<Long> agentIds,
                                                                         @Param("agentIdPresent") Boolean agentIdPresent,
                                                                         @Param("billable") String billable,
                                                                         @Param("isLeaf") Boolean isLeaf,
                                                                         Pageable pageable);

    @Query(value = "SELECT ch.subscription_id as workOrderId, ch.message As title, ch.category as type," +
            " ch.parent_request_id as serviceReferenceId, ch.status, org.organization_name as board, " +
            "date_format(ch.planned_date, '%b %d, %Y') as plannedDate, ch.estimated_hours as timeRequired, " +
            "(SELECT COUNT(eg.id) " +
            "FROM user_group ug " +
            "LEFT JOIN entity_group eg ON eg.user_group_id = ug.id " +
            "AND eg.is_deleted = false WHERE ug.ref_id = ch.subscription_id AND ug.ref_type = 'workOrder') AS assignedResource, " +
            "(CASE WHEN ch.coverage IN ('Chargeable', 'Billable') THEN 'Yes' ELSE 'No' END) AS billable, " +
            "date_format( ch.updated_at, '%b %d, %Y') as updatedAt, e.entity_name as name, ed.uri as url " +
            "FROM conversation_head ch " +
            "LEFT JOIN organization_detail od ON ch.product_id = od.mongo_ref_id " +
            "LEFT JOIN organization org ON od.business_unit_id = org.id " +
            "LEFT JOIN user_level_privilege ulp ON ulp.account_id = ch.raised_by and ulp.role_id is null " +
            "LEFT JOIN entity e ON e.id = ulp.entity_id " +
            "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
            "WHERE ch.subscription_id = :workOrderId AND (ch.source_type = 'WorkOrder' OR ch.source_type = 'Work Order')",
            nativeQuery = true)
    WorkOrderInformationTemplate getAllWorkOrderInformation(@Param("workOrderId") String workOrderId);

    @Query(value = "SELECT DISTINCT " +
            "(CASE " +
            "WHEN :groupByType = 'STATUS' THEN ch.status " +
            "WHEN :groupByType = 'BOARD' THEN o.organization_name " +
            "WHEN :groupByType = 'REQUESTER' THEN requesterEntity.entity_name " +
            "WHEN :groupByType = 'SUPPORT AGENT' THEN agentEntity.entity_name " +
            "WHEN :groupByType = 'BILLABLE' THEN CASE WHEN ch.coverage IN ('Chargeable', 'Billable') THEN 'Yes' ELSE 'No' END " +
            "WHEN :groupByType = 'Type' THEN ch.category " +
            "WHEN :groupByType = 'REQUESTER TYPE' THEN requesterEntity.entity_type " +
            "ELSE '' END )AS groupBy,:isLeaf as isLeaf " +
            "FROM conversation_head ch " +
            "LEFT JOIN organization_detail orgDetail ON orgDetail.mongo_ref_id = ch.product_id " +
            "LEFT JOIN organization o ON o.id = orgDetail.business_unit_id " +
            "LEFT JOIN user_level_privilege requesterULP ON requesterULP.entity_id = ch.source_id AND requesterULP.role_id IS NULL " +
            "LEFT JOIN entity requesterEntity ON requesterEntity.id = ch.source_id " +
            "LEFT JOIN customer_detail cusDetail ON cusDetail.entity_id = ch.source_id " +
            "LEFT JOIN entity_role agentRole ON agentRole.id = ch.assignee " +
            "LEFT JOIN entity agentEntity ON agentEntity.id = agentRole.entity_id " +
            "WHERE ch.source_type = 'WorkOrder' " +
            "AND (CASE " +
            "WHEN :groupByType = 'STATUS' THEN (ch.status IS NOT NULL AND ch.status <> '') " +
            "WHEN :groupByType = 'BOARD' THEN (o.organization_name IS NOT NULL AND o.organization_name <> '') " +
            "WHEN :groupByType = 'REQUESTER' THEN  (requesterEntity.entity_name IS NOT NULL AND requesterEntity.entity_name <> '') " +
            "WHEN :groupByType = 'SUPPORT AGENT' THEN (agentEntity.entity_name IS NOT NULL AND agentEntity.entity_name <> '') " +
            "WHEN :groupByType = 'BILLABLE' THEN (ch.coverage IS NOT NULL AND ch.coverage <> '') " +
            "WHEN :groupByType = 'Type' THEN (ch.category IS NOT NULL AND ch.category <> '') " +
            "WHEN :groupByType = 'REQUESTER TYPE' THEN (requesterEntity.entity_type IS NOT NULL AND  requesterEntity.entity_type <> '')" +
            "ELSE FALSE END = TRUE )",
            countQuery = "SELECT COUNT(DISTINCT " +
                    "CASE " +
                    "WHEN :groupByType = 'STATUS' THEN ch.status " +
                    "WHEN :groupByType = 'BOARD' THEN o.organization_name " +
                    "WHEN :groupByType = 'REQUESTER' THEN requesterEntity.entity_name " +
                    "WHEN :groupByType = 'SUPPORT AGENT' THEN agentEntity.entity_name " +
                    "WHEN :groupByType = 'BILLABLE' THEN CASE WHEN ch.coverage IN ('Chargeable', 'Billable') THEN 'Yes' ELSE 'No' END " +
                    "WHEN :groupByType = 'Type' THEN ch.category " +
                    "WHEN :groupByType = 'REQUESTER TYPE' THEN requesterEntity.entity_type " +
                    "ELSE '' END) " +
                    "FROM conversation_head ch " +
                    "LEFT JOIN organization_detail orgDetail ON orgDetail.mongo_ref_id = ch.product_id " +
                    "LEFT JOIN organization o ON o.id = orgDetail.business_unit_id " +
                    "LEFT JOIN user_level_privilege requesterULP ON requesterULP.entity_id = ch.source_id AND requesterULP.role_id IS NULL " +
                    "LEFT JOIN entity requesterEntity ON requesterEntity.id = ch.source_id " +
                    "LEFT JOIN customer_detail cusDetail ON cusDetail.entity_id = ch.source_id " +
                    "LEFT JOIN entity_role agentRole ON agentRole.id = ch.assignee " +
                    "LEFT JOIN entity agentEntity ON agentEntity.id = agentRole.entity_id " +
                    "WHERE ch.source_type = 'WorkOrder' " +
                    "AND (CASE " +
                    "WHEN :groupByType = 'STATUS' THEN (ch.status IS NOT NULL AND ch.status <> '') " +
                    "WHEN :groupByType = 'BOARD' THEN (o.organization_name IS NOT NULL AND o.organization_name <> '') " +
                    "WHEN :groupByType = 'REQUESTER' THEN  (requesterEntity.entity_name IS NOT NULL AND requesterEntity.entity_name <> '') " +
                    "WHEN :groupByType = 'SUPPORT AGENT' THEN (agentEntity.entity_name IS NOT NULL AND agentEntity.entity_name <> '') " +
                    "WHEN :groupByType = 'BILLABLE' THEN (ch.coverage IS NOT NULL AND ch.coverage <> '') " +
                    "WHEN :groupByType = 'Type' THEN (ch.category IS NOT NULL AND ch.category <> '') " +
                    "WHEN :groupByType = 'REQUESTER TYPE' THEN (requesterEntity.entity_type IS NOT NULL AND  requesterEntity.entity_type <> '')" +
                    "ELSE FALSE END = TRUE )",
            nativeQuery = true)
    Page<WorkOrderManagementTemplate> findAllWorkOrderManagementTemplateGroupByList(@Param("groupByType") String groupByType,
                                                                                    @Param("isLeaf") Boolean isLeaf,
                                                                                    Pageable pageable);
    @Query(value = "SELECT " +
            "GROUP_CONCAT(DISTINCT ch.status) AS status, " +
            "GROUP_CONCAT(DISTINCT ch.category) AS workOrderType, " +
            "GROUP_CONCAT(DISTINCT requesterEntity.entity_type) AS requesterType, " +
            "GROUP_CONCAT(DISTINCT CONCAT(requesterEntity.entity_name, '-', requesterEntity.id)) AS requesterName, " +
            "GROUP_CONCAT(DISTINCT CONCAT(agentEntity.entity_name, '-', agentEntity.id)) AS supportAgentName, " +
            "GROUP_CONCAT(DISTINCT CASE WHEN ch.coverage IN ('Chargeable', 'Billable') THEN 'Yes' ELSE 'No' END) AS billable " +
            "FROM conversation_head ch " +
            "LEFT JOIN organization_detail orgDetail ON orgDetail.mongo_ref_id = ch.product_id " +
            "LEFT JOIN organization o ON o.id = orgDetail.business_unit_id " +
            "LEFT JOIN user_level_privilege requesterULP ON requesterULP.entity_id = ch.source_id AND requesterULP.role_id IS NULL " +
            "LEFT JOIN entity requesterEntity ON requesterEntity.id = ch.source_id " +
            "LEFT JOIN customer_detail cusDetail ON cusDetail.entity_id = ch.source_id " +
            "LEFT JOIN entity_role agentRole ON agentRole.id = ch.assignee " +
            "LEFT JOIN entity agentEntity ON agentEntity.id = agentRole.entity_id " +
            "WHERE ch.source_type = 'WorkOrder'",
            nativeQuery = true)
    WorkOrderManagementTemplate findWorkOrderManagementFilterDropDown();
@Query(value="select requesterEntity.id as getRequesterEntityId, ch.customer_id as requesterAcctId," +
        "requesterEntity.entity_name as reuesterName,requesterEntityDetail.uri as requesterImage," +
        "subsExt.subscription_name as subsName,ch.subscription_id as subsId, " +
        "CONCAT_WS(',', subsPloc.ext1, subsPloc.ext2, subsPloc.add3) as subsAddress," +
        "ch.variant_id as variantId,ch.source_type as workOrderType, " +
        "(SELECT MAX(ext.ref_type) FROM ext_data_stage_definition ext WHERE ext.ref_id = ch.variant_id) as variantName," +
        "(select CONCAT_WS(',', varPloc.ext1, varPloc.ext2, varPloc.add3) from physical_locations varPloc where varPloc.id in " +
        "(SELECT MAX(ext1.site_location_id) FROM ext_data_stage_definition ext1 WHERE ext1.ref_id = ch.variant_id )) as variantAddress," +
        "(SELECT docu.uri FROM docu_library docu WHERE docu.code_ref_id = ch.variant_id AND docu.code_ref_type = 'VAR_THMB' AND docu.visibility_key = true) as variantImage  " +
        "from conversation_head ch " +
        "left join ext_data_stage_definition subsExt on subsExt.subs_id = ch.subscription_id " +
        "left join entity requesterEntity on requesterEntity.id = ch.source_id " +
        "left join entity_detail requesterEntityDetail on requesterEntityDetail.entity_id = ch.source_id " +
        "left join physical_locations subsPloc on subsPloc.id = subsExt.cust_add " +
        "where ch.id =:headId",nativeQuery = true)
    WorkOrderManagementTemplate getTicketInformation(@Param("headId") Long headId);
}
