package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.mapper.contract.OrgDetailDTO;
import com.solar.api.tenant.mapper.contract.OrganizationDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.LinkedSitesTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.LinkedSitesFiltersTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.filter.PhysicalLocationOMFilterTemplate;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.mapper.workOrder.OrgnazationTemplateWoDTO;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.user.UserTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findAllByStatus(String status);

    Organization findByStatusAndPrimaryIndicator(String status, Boolean indicator);

    Optional<Organization> findByOrganizationName(String organizationName);

    @Deprecated
    @Query(value = "select o.organization_name as unitName, o.sub_type as unitTypeId, o.id as unitId, e.entity_name as unitManager, " +
            "od.id as orgDetailId,er.id  as entityRoleId, org.organization_name as parentName, " +
            "org.id as parentId, o.business_description as details, " +
            "pl.id as locId, pl.add1 as add1, pl.add2 as add2, pl.zip_code as zipCode," +
            "pl.geo_lat as geoLat, pl.geo_long as geoLong,  " +
            "pl.ext1 as ext1, pl.ext2 as ext2, pl.contact_person as contactPerson, " +
            "pl.email as email from organization o " +
            "inner Join organization org on org.id = o.parent_org_id " +
            "inner Join organization_detail od on od.org_id = o.id " +
            "inner Join location_mapping lm on lm.source_id = o.id " +
            "inner Join physical_locations pl on pl.id = lm.location_id " +
            "inner Join entity_role er on er.id = od.ref_id " +
            "inner Join entity e on e.id = er.entity_id " +
            "Where o.id in (:orgId) and lm.source_type = 'Business Unit' and pl.location_type = 'Business Unit' " +
            "and od.ref_id is not null",
            nativeQuery = true)
    OrgDetailDTO findSubOrgDetails(@Param("orgId") Long orgId);

    @Deprecated
    @Query(value = "Select o.organization_name as unitName,o.sub_type as unitTypeId,o.id as unitId, e.entity_name as unitManager, " +
            "od.id as orgDetailId, er.id as entityRoleId, o.business_description as details, " +
            "pl.id as locId, pl.add1 as add1, pl.add2 as add2, pl.zip_code as zipCode, " +
            "pl.geo_lat as geoLat, pl.geo_long as geoLong,  " +
            "pl.ext1 as ext1, pl.ext2 as ext2, pl.contact_person as contactPerson," +
            "pl.email as email " +
            "from organization o " +
            "Inner Join organization_detail od on od.org_id = o.id " +
            "Inner Join location_mapping lm on lm.source_id = o.id " +
            "Inner Join physical_locations pl on pl.id = lm.location_id " +
            "Inner Join entity_role er on er.id = od.ref_id " +
            "Inner Join entity e on e.id = er.entity_id " +
            "Where o.id in (:orgId) and lm.source_type = 'Business Unit' and pl.location_type = 'Business Unit' " +
            "and od.ref_id is not null",
            nativeQuery = true)
    OrgDetailDTO findMasterOrgDetails(@Param("orgId") Long orgId);

    @Query(value = "select org.id from organization org where org.parent_org_id is null limit 1", nativeQuery = true)
    Long getMasterOrgId();


    @Query(value = "Select  o.id as orgId, o.organization_name as unitName,e.entity_name as unitManager, o.organization_type as unitType," +
            "pl.add1 as address,pl.add2 as state,pl.add3 as country, pl.geo_lat as geoLat, pl.geo_long as geoLong " +
            "from organization o Inner Join location_mapping lm on lm.source_id = o.id Inner Join physical_locations pl on pl.id = lm.location_id " +
            " inner Join organization_detail od on od.org_id = o.id " +
            " inner Join entity e on e.id = od.ref_id " +
            " Where o.id in (:masterOrgId) and pl.location_type = 'Business Unit'",
            nativeQuery = true)
    OrgnazationTemplateWoDTO findMasterOrg(Long masterOrgId);


    @Query(value = "Select  o.id as orgId, o.organization_name as unitName, e.entity_name as unitManager, o.organization_type as unitType, " +
            "pl.add1 as address,pl.add2 as state,pl.add3 as country, pl.geo_lat as geoLat, pl.geo_long as geoLong " +
            "from organization o Inner Join location_mapping lm on lm.source_id = o.id Inner Join physical_locations pl on pl.id = lm.location_id " +
            " inner Join organization_detail od on od.org_id = o.id " +
            " inner Join entity e on e.id = od.ref_id " +
            " Where o.parent_org_id in (:masterOrgId) and pl.location_type = 'Business Unit'",
            nativeQuery = true)
    List<OrgnazationTemplateWoDTO> findAllSubOrg(@Param("masterOrgId") Long masterOrgId);

    Organization findByStatusAndPrimaryIndicatorAndParentOrgId(String status, Boolean indicator, Long parentOrgId);

    List<Organization> findByOrganizationNameIn(List<String> organizationNames);

    @Query("SELECT new com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTile(org.id, cp.companyName, cp.logo, org.status, org.organizationType, COUNT(subOrg),cp.websiteURL) " +
            "FROM Organization org " +
            "LEFT JOIN CompanyPreference cp ON cp.id = org.companyPreference.id " +
            "LEFT JOIN Organization subOrg ON org.id = subOrg.parentOrgId " +
            "WHERE org.parentOrgId is null " +
            "GROUP BY org.id, cp.companyName, cp.logo, org.status, org.organizationType")
    Page<OrganizationManagementTile> getAllOrganizationMainLists(Pageable pageable);

//    TODO:
//    @Query(value = "SELECT o.id as orgId,o.organization_name as unitName,o.logo_image as unitImg,o.unit_category_type as unitType," +
//            "o.unit_category as unitCategory, e.entity_name as unitManagerName ,ed.uri as unitManagerImg,o.status as status ,date_format(o.created_at, '%b %d, %Y') as createdAt," +
//            "date_format(o.updated_at, '%b %d, %Y') as updatedAt, " +
//            "(select count(lm.id) from location_mapping lm where lm.source_type ='ORGANIZATION' and lm.source_id =o.id) as officeCount, " +
//            "( select count(cud.id) from location_mapping lm1 " +
//            "INNER JOIN ext_data_stage_definition edsd ON JSON_UNQUOTE(JSON_EXTRACT(edsd.ext_json, '$.maging_physical_loc_id')) = lm1.location_id " +
//            "INNER JOIN customer_subscription sc ON sc.subscription_template = edsd.ref_id " +
//            "INNER JOIN user u ON u.acct_id = sc.account_id " +
//            "INNER JOIN user_level_privilege prv ON prv.account_id = u.acct_id  and prv.role_id is null " +
//            "INNER JOIN entity e ON e.id = prv.entity_id " +
//            "INNER JOIN customer_detail cud ON cud.entity_id = e.id " +
//            "WHERE cud.states NOT IN ('APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING') " +
//            "and lm1.source_id = o.id AND lm1.source_type = 'Organization') as customerCount, " +
//            "(select count(eg.id) from user_group ug inner join entity_group eg on eg.user_group_id = ug.id where ug.ref_id = o.id and ug.ref_type='Organization') as assetCount " +
//            "FROM organization o " +
//            "LEFT JOIN  entity e " +
//            "ON  o.contact_person_id = e.id " +
//            "LEFT JOIN entity_detail ed " +
//            "ON e.id = ed.entity_id " +
//            "WHERE o.parent_org_id =:parentOrgId " +
//            "ORDER BY o.created_at DESC",
//            countQuery = "SELECT count(o.id) FROM organization o WHERE o.parent_org_Id =:parentOrgId", nativeQuery = true)
//    Page<OrganizationManagementTemplate> getAllOrganizationInnerLists(@Param("parentOrgId") Long parentOrgId, Pageable pageable);



    @Query(value = "SELECT o.id as orgId,o.organization_name as unitName,o.logo_image as unitImg,o.unit_category_type as unitType," +
            "o.unit_category as unitCategory, e.entity_name as unitManagerName ,ed.uri as unitManagerImg,o.status as status ,date_format(o.created_at, '%b %d, %Y') as createdAt," +
            "date_format(o.updated_at, '%b %d, %Y') as updatedAt, " +
            "(select count(lm.id) from location_mapping lm where lm.source_type ='ORGANIZATION' and lm.source_id =o.id) as officeCount, " +
            "(select count(DISTINCT edsd1.ref_id) from location_mapping lm2 " +
            "left join ext_data_stage_definition edsd1 on JSON_UNQUOTE(JSON_EXTRACT(edsd1.ext_json, '$.maging_physical_loc_id')) = lm2.location_id " +
            "where lm2.source_type ='ORGANIZATION' and lm2.source_id = o.id ) as linkedSiteCount, " +
            "(select count(DISTINCT cud.id) from location_mapping lm1 " +
            "INNER JOIN ext_data_stage_definition edsd ON JSON_UNQUOTE(JSON_EXTRACT(edsd.ext_json, '$.maging_physical_loc_id')) = lm1.location_id " +
            "INNER JOIN customer_subscription sc ON sc.ext_subs_id = edsd.subs_id " +
            "INNER JOIN user u ON u.acct_id = sc.account_id " +
            "INNER JOIN user_level_privilege prv ON prv.account_id = u.acct_id  and prv.role_id is null " +
            "INNER JOIN customer_detail cud ON cud.entity_id = prv.entity_id " +
            "WHERE cud.states NOT IN ('APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING') " +
            "and lm1.source_id = o.id AND lm1.source_type = 'Organization' and sc.account_id is not null) as customerCount, " +
            "(select count(empDetail.id) from location_mapping lm inner join employee_detail empDetail on empDetail.primary_office = lm.location_id " +
            "inner join entity e on e.id = empDetail.entity_id and e.entity_type = 'Employee' " +
            "where lm.source_id = o.id AND lm.source_type = 'Organization' ) as assetCount " +
            "FROM organization o " +
            "LEFT JOIN  entity e " +
            "ON  o.contact_person_id = e.id " +
            "LEFT JOIN entity_detail ed " +
            "ON e.id = ed.entity_id " +
            "WHERE o.parent_org_id =:parentOrgId " +
            "AND (:searchWord IS NULL OR :searchWord = '' OR o.organization_name LIKE %:searchWord% OR o.unit_category_type LIKE %:searchWord% OR o.unit_category LIKE %:searchWord% " +
            "OR e.entity_name LIKE %:searchWord% ) " +
            "ORDER BY o.created_at DESC",
            countQuery = "SELECT count(o.id) FROM organization o WHERE o.parent_org_Id =:parentOrgId", nativeQuery = true)
    Page<OrganizationManagementTemplate> getAllOrganizationInnerLists(@Param("parentOrgId") Long parentOrgId, @Param("searchWord") String searchWord, Pageable pageable);

    /* For detail tab */
    @Query("SELECT new com.solar.api.tenant.mapper.contract.OrganizationDTO(o.id,o.organizationName,o.logoImage ,o.businessDescription,o.status,COUNT(subOrg)," +
            "ed.entity.id, ed.uri,e.entityName ,e.contactPersonEmail, e.contactPersonPhone,empd.designation," +
            "cp.id, cp.websiteURL,cp.youtubeURL ,cp.twitterURL ,cp.linkedInURL ,cp.facebookURL,cp.companyTerms,cp.companyPolicy) " +
            "FROM Organization o " +
            "left join CompanyPreference cp " +
            "ON o.companyPreference.id = cp.id " +
            "LEFT JOIN Organization subOrg " +
            "ON o.id = subOrg.parentOrgId " +
            "left join  Entity e " +
            "ON o.contactPerson.id = e.id " +
            "left join EntityDetail ed " +
            "ON e.id = ed.entity.id " +
            "left join EmployeeDetail empd " +
            "ON e.id = empd.entityId " +
            "WHERE o.parentOrgId IS NULL " +
            "GROUP BY o.id,o.organizationName,o.logoImage ,o.businessDescription,o.status," +
            "ed.entity.id, ed.uri,e.entityName ,e.contactPersonEmail, e.contactPersonPhone,empd.designation," +
            "cp.id, cp.websiteURL,cp.youtubeURL ,cp.twitterURL ,cp.linkedInURL ,cp.facebookURL")
    OrganizationDTO getMasterOrganizationDetails(@Param("parentOrgId") Long parentOrgId);

    @Query(value = "SELECT u.acct_id AS accountId, u.first_name AS firstName, u.last_name AS lastName, " +
            "u.email_address AS emailAddress,date_format(u.created_at, '%b %d, %Y') AS createdAt, " +
            "cud.customer_type AS customerType, e.status AS status, cud.states AS stateCustomer, e.contact_person_phone AS phone, " +
            "CONCAT_WS(',', ploc.ext1, ploc.ext2, ploc.add3) AS region, e.id AS entityId, " +
            "(CASE WHEN sc.is_checked IS TRUE THEN '1' WHEN sc.is_checked IS FALSE THEN '0' ELSE 'Null' END) AS isChecked, " +
            "ed.uri AS profileUrl, " +
            "cud.self_initiative AS selfInitiative, " +
            "cud.has_Login AS hasLogin, cud.mobile_allowed AS mobileAllowed " +
            "FROM user u " +
            "LEFT JOIN user_level_privilege prv ON prv.account_id = u.acct_id AND prv.role_id is null  " +
            "LEFT JOIN entity e ON e.id = prv.entity_id " +
            "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
            "LEFT JOIN customer_detail cud ON cud.entity_id = e.id " +
            "LEFT JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
            "LEFT JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
            "LEFT JOIN ca_soft_credit_check sc ON sc.customer_no = e.id " +
            "WHERE cud.states NOT IN ( 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', " +
            "'PROSPECT', 'REQUEST PENDING' ) AND (locmap.primary_ind = 'Y' or locmap.primary_ind is null) " +
            " AND (ploc.is_deleted is null or ploc.is_deleted = false) " +
            "AND (e.is_deleted is null or e.is_deleted = false) " +
            "and prv.role_id is null " +
            "and e.organization_id =:orgId " +
            "AND (:searchWord IS NULL OR " +
            "u.first_name LIKE %:searchWord% " +
            "or u.last_name LIKE %:searchWord% " +
            "or u.email_address LIKE %:searchWord% " +
            "or ploc.zip_code LIKE %:searchWord% " +
            "or e.contact_person_phone LIKE %:searchWord% " +
            "or cud.customer_type LIKE %:searchWord% ) " +
            "ORDER BY u.created_at DESC",
            countQuery = "SELECT COUNT(DISTINCT u.acct_id) " +
                    "FROM user u " +
                    "LEFT JOIN user_level_privilege prv ON prv.account_id = u.acct_id  AND prv.role_id is null " +
                    "LEFT JOIN entity e ON e.id = prv.entity_id " +
                    "LEFT JOIN customer_detail cud ON cud.entity_id = e.id " +
                    "LEFT JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
                    "LEFT JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
                    "WHERE cud.states NOT IN ( 'APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING'" +
                    "'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING' ) AND (locmap.primary_ind = 'Y' or locmap.primary_ind is null) " +
                    "AND (ploc.is_deleted is null or ploc.is_deleted = false) " +
                    "AND (e.is_deleted is null or e.is_deleted = false) " +
                    "and e.organization_id = :orgId",
            nativeQuery = true)
    Page<UserTemplate> getAllOrgCustomerLists(@Param("orgId") Long orgId,
                                              @Param("searchWord") String searchWord,
                                              Pageable pageable);

    @Query(value = "SELECT " +
            "COALESCE(ploc.id, cloc.id) AS locationId, " +
            "COALESCE(ploc.location_name, cloc.location_name) AS locationName, " +
            "COALESCE(ploc.category, cloc.category) AS locationCategory, " +
            "COALESCE(ploc.location_type, cloc.location_type) AS locationType, " +
            "COALESCE(NULLIF(org.unit_category, ''), '-') AS businessUnit, " +
            "CONCAT_WS(',', COALESCE(ploc.ext1, cloc.ext1), COALESCE(ploc.ext2, cloc.ext2), COALESCE(ploc.add3, cloc.add3)) AS address, " +
            "'-' AS contactPersonName, " +
            "'NA' AS contactPersonImage, org.id as orgId, :isLeaf as isLeaf " +
            "FROM organization org " +
            "LEFT JOIN physical_locations ploc ON ploc.org_id = org.id " +
            "LEFT JOIN location_mapping lm ON lm.source_id = org.id AND lm.source_type = 'ORGANIZATION' " +
            "LEFT JOIN physical_locations cloc ON cloc.id = lm.location_id " +
            "WHERE ((:isMaster = true AND (org.id = :orgId OR org.parent_org_id =:orgId) AND (cloc.category='Business' OR ploc.category='Business' ) " +
            "AND " +
            "((:locationCategory IS NULL OR COALESCE(ploc.category, cloc.category) = :locationCategory) AND " +
            "(:locationType IS NULL OR COALESCE(ploc.location_type, cloc.location_type) = :locationType) AND " +
            "(:businessUnit IS NULL OR COALESCE(org.unit_category, '-') = :businessUnit))) " +
            "OR (:isMaster = false AND org.id = :orgId AND  cloc.category='Business')) " +
            "AND (:groupBy IS NULL OR " +
            "(:groupBy = 'Category' AND COALESCE(ploc.category, cloc.category) = :groupByName) OR " +
            "(:groupBy = 'Type' AND COALESCE(ploc.location_type, cloc.location_type) = :groupByName) OR " +
            "(:groupBy = 'Business Unit' AND COALESCE(org.unit_category, '-') = :groupByName)) " +
            "AND (:searchWords IS NULL OR " +
            "LOWER(ploc.location_name) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(cloc.location_name) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.category) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(cloc.category) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.location_type) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(cloc.location_type) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(org.unit_category) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(org.organization_name) LIKE LOWER(CONCAT('%', :searchWords, '%')))",
            countQuery = "SELECT COUNT(COALESCE(ploc.id, cloc.id)) " +
                    "FROM organization org " +
                    "LEFT JOIN physical_locations ploc ON ploc.org_id = org.id " +
                    "LEFT JOIN location_mapping lm ON lm.source_id = org.id AND lm.source_type = 'ORGANIZATION' " +
                    "LEFT JOIN physical_locations cloc ON cloc.id = lm.location_id " +
                    "WHERE " +
                    "((:isMaster = true AND (org.id = :orgId OR org.parent_org_id =:orgId) AND " +
                    "(cloc.category='Business' OR ploc.category='Business' ) AND " +
                    "((:locationCategory IS NULL OR COALESCE(ploc.category, cloc.category) = :locationCategory) AND " +
                    "(:locationType IS NULL OR COALESCE(ploc.location_type, cloc.location_type) = :locationType) AND " +
                    "(:businessUnit IS NULL OR COALESCE(org.unit_category, '-') = :businessUnit))) " +
                    "OR (:isMaster = false AND org.id = :orgId  AND cloc.category='Business')) " +
                    "AND (:groupBy IS NULL OR " +
                    "(:groupBy = 'Category' AND COALESCE(ploc.category, cloc.category) = :groupByName) OR " +
                    "(:groupBy = 'Type' AND COALESCE(ploc.location_type, cloc.location_type) = :groupByName) OR " +
                    "(:groupBy = 'Business Unit' AND COALESCE(org.unit_category, '-') = :groupByName)) " +
                    "AND (:searchWords IS NULL OR " +
                    "LOWER(ploc.location_name) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
                    "LOWER(cloc.location_name) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
                    "LOWER(ploc.category) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
                    "LOWER(cloc.category) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
                    "LOWER(ploc.location_type) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
                    "LOWER(cloc.location_type) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
                    "LOWER(org.unit_category) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
                    "LOWER(org.organization_name) LIKE LOWER(CONCAT('%', :searchWords, '%')))",
            nativeQuery = true)
    Page<PhysicalLocationOMTemplate> getAllOrgOfficeList(@Param("orgId") Long orgId,
                                                         @Param("groupBy") String groupBy,
                                                         @Param("groupByName") String groupByName,
                                                         @Param("isLeaf") Boolean isLeaf,
                                                         @Param("isMaster") Boolean isMaster,
                                                         @Param("locationCategory") String locationCategory,
                                                         @Param("locationType") String locationType,
                                                         @Param("businessUnit") String businessUnit,
                                                         @Param("searchWords") String searchWords,
                                                         Pageable pageable);


    //OrganizationEmployee

    @Query("SELECT new  com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementTile(e.id,e.entityName,ed.uri," +
            "e.contactPersonEmail,e.contactPersonPhone,e.status ,empd.hasLogin,empd.mobileAllowed ," +
            "date_format(e.createdAt, '%b %d, %Y'),e.organization.id) " +
            "FROM Entity e " +
            "LEFT JOIN EntityDetail ed " +
            "ON ed.entity.id = e.id " +
            "LEFT JOIN EmployeeDetail empd " +
            "ON empd.entityId = ed.id " +
            "WHERE e.organization.id = :orgId " +
            "AND e.entityType = 'Employee'" +
            "AND (e.isDeleted IS NULL OR e.isDeleted = FALSE)" +
            "AND (:searchWord IS NULL " +
            "OR e.entityName LIKE %:searchWord% " +
            "OR e.contactPersonPhone LIKE %:searchWord% " +
            "OR e.contactPersonEmail LIKE %:searchWord%) " +
            "ORDER BY e.createdAt DESC")
    Page<EmployeeManagementTile> getAllOrgEmployeeLists(@Param("orgId") Long orgId,
                                                        @Param("searchWord") String searchWord,
                                                        Pageable pageable);


    @Query(value = "SELECT distinct " +
            "CASE " +
            "WHEN :groupByType = 'CATEGORY' THEN COALESCE(ploc.category, cloc.category) " +
            "WHEN :groupByType = 'BUSINESS_UNIT' THEN IFNULL(org.unit_category, '-') " +
            "WHEN :groupByType = 'TYPE' THEN COALESCE(ploc.location_type, cloc.location_type) " +
            "ELSE '' END AS groupBy, :isLeaf as isLeaf " +
            "FROM organization org " +
            "LEFT JOIN physical_locations ploc ON ploc.org_id = org.id " +
            "LEFT JOIN location_mapping lm ON lm.source_id = org.id AND lm.source_type = 'ORGANIZATION' " +
            "LEFT JOIN physical_locations cloc ON cloc.id = lm.location_id " +
            "WHERE (org.id = :orgId AND ploc.category IS NOT NULL AND ploc.category <> '' AND (cloc.category='Business' OR ploc.category='Business' )) " +
            "OR (org.parent_org_id = :orgId AND cloc.category IS NOT NULL AND cloc.category <> '' AND (cloc.category='Business' OR ploc.category='Business' )) " +
            "AND (:groupByType = 'CATEGORY' AND COALESCE(ploc.category, cloc.category) IS NOT NULL AND COALESCE(ploc.category, cloc.category) <> '' ) " +
            "OR (:groupByType = 'BUSINESS_UNIT' AND IFNULL(org.unit_category, '-') <> '-') " +
            "OR (:groupByType = 'TYPE' AND COALESCE(ploc.location_type, cloc.location_type) IS NOT NULL AND COALESCE(ploc.location_type, cloc.location_type) <> ''AND (cloc.category='Business' OR ploc.category='Business' ))",
            countQuery = "SELECT COUNT(DISTINCT " +
                    "CASE " +
                    "WHEN :groupByType = 'CATEGORY' THEN COALESCE(ploc.category, cloc.category) " +
                    "WHEN :groupByType = 'BUSINESS_UNIT' THEN IFNULL(org.unit_category, '-') " +
                    "WHEN :groupByType = 'TYPE' THEN COALESCE(ploc.location_type, cloc.location_type) " +
                    "ELSE '' END) " +
                    "FROM organization org " +
                    "LEFT JOIN physical_locations ploc ON ploc.org_id = org.id " +
                    "LEFT JOIN location_mapping lm ON lm.source_id = org.id AND lm.source_type = 'ORGANIZATION' " +
                    "LEFT JOIN physical_locations cloc ON cloc.id = lm.location_id " +
                    "WHERE (org.id = :orgId AND ploc.category IS NOT NULL AND ploc.category <> '' AND (cloc.category='Business' OR ploc.category='Business' )) " +
                    "OR (org.parent_org_id = :orgId AND cloc.category IS NOT NULL AND cloc.category <> '' AND (cloc.category='Business' OR ploc.category='Business' )) " +
                    "AND (:groupByType = 'CATEGORY' AND COALESCE(ploc.category, cloc.category) IS NOT NULL AND COALESCE(ploc.category, cloc.category) <> '') " +
                    "OR (:groupByType = 'BUSINESS_UNIT' AND IFNULL(org.unit_category, '-') <> '-') " +
                    "OR (:groupByType = 'TYPE' AND COALESCE(ploc.location_type, cloc.location_type) IS NOT NULL AND COALESCE(ploc.location_type , cloc.location_type) <> ''AND (cloc.category='Business' OR ploc.category='Business' ))",
            nativeQuery = true)
    Page<PhysicalLocationOMTemplate> getAllMasterOrgOfficeGroupByList(@Param("orgId") Long orgId,
                                                                      @Param("isLeaf") Boolean isLeaf,
                                                                      @Param("groupByType") String groupByType,
                                                                      Pageable pageable);

    @Query(value = "SELECT distinct " +
            "CASE " +
            "WHEN :groupByType = 'CATEGORY' THEN  cloc.category " +
            "WHEN :groupByType = 'BUSINESS_UNIT' THEN IFNULL(org.unit_category, '-') " +
            "WHEN :groupByType = 'TYPE' THEN cloc.location_type " +
            "ELSE '' END AS groupBy, :isLeaf as isLeaf " +
            "FROM organization org " +
            "LEFT JOIN location_mapping lm ON lm.source_id = org.id AND lm.source_type = 'ORGANIZATION' " +
            "LEFT JOIN physical_locations cloc ON cloc.id = lm.location_id " +
            "WHERE org.id = :orgId AND cloc.location_type IS NOT NULL AND cloc.location_type <> '' AND cloc.location_type = 'Office Address' " +
            "AND (:groupByType = 'CATEGORY' AND cloc.category IS NOT NULL AND  cloc.category <> '') " +
            "OR (:groupByType = 'BUSINESS_UNIT' AND IFNULL(org.unit_category, '-') <> '-') " +
            "OR (:groupByType = 'TYPE' AND cloc.location_type IS NOT NULL AND cloc.location_type <> '')",
            countQuery = "SELECT COUNT(DISTINCT " +
                    "CASE " +
                    "WHEN :groupByType = 'CATEGORY' THEN cloc.category " +
                    "WHEN :groupByType = 'BUSINESS_UNIT' THEN IFNULL(org.unit_category, '-') " +
                    "WHEN :groupByType = 'TYPE' THEN  cloc.location_type " +
                    "ELSE '' END) " +
                    "FROM organization org " +
                    "LEFT JOIN location_mapping lm ON lm.source_id = org.id AND lm.source_type = 'ORGANIZATION' " +
                    "LEFT JOIN physical_locations cloc ON cloc.id = lm.location_id " +
                    "WHERE org.id = :orgId AND cloc.location_type IS NOT NULL AND cloc.location_type <> '' AND cloc.location_type = 'Office Address' " +
                    "AND (:groupByType = 'CATEGORY' AND  cloc.category IS NOT NULL AND  cloc.category <> '') " +
                    "OR (:groupByType = 'BUSINESS_UNIT' AND IFNULL(org.unit_category, '-') <> '-') " +
                    "OR (:groupByType = 'TYPE' AND cloc.location_type IS NOT NULL AND cloc.location_type <> '')",
            nativeQuery = true)
    Page<PhysicalLocationOMTemplate> getAllSubOrgOfficeGroupByList(@Param("orgId") Long orgId,
                                                                   @Param("isLeaf") Boolean isLeaf,
                                                                   @Param("groupByType") String groupByType,
                                                                   Pageable pageable);


    @Query("SELECT new com.solar.api.tenant.mapper.contract.OrganizationDTO(o.id,o.organizationName, o.logoImage," +
            "o.unitCategoryType, o.unitCategory, o.createdAt,o.businessDescription,e.id, ed.uri, e.entityName, e.contactPersonEmail, e.contactPersonPhone) " +
            "FROM Organization o " +
            "LEFT JOIN Entity e ON o.contactPerson.id = e.id " +
            "LEFT JOIN EntityDetail ed ON ed.entity.id = e.id " +
            "WHERE o.id =(:orgId) ")
    OrganizationDTO getAllBusinessUnitDetailLists(@Param("orgId") Long orgId);
//    TODO:
//    @Query(value = "SELECT o.id as orgId,o.organization_name as unitName, o.logo_image as unitImg,o.status as status," +
//            "o.unit_category_type as unitType, o.unit_category as unitCategory, date_format(o.created_at, '%b %d, %Y') as createdAt," +
//            "o.business_description as unitDescription,e.id as unitManagerId, ed.uri as unitManagerImg, e.entity_name as unitManagerName, " +
//            "e.contact_person_email as unitManagerEmail, e.contact_person_phone as unitManagerPhone, " +
//            "(select count(lm.id) from location_mapping lm where lm.source_type ='ORGANIZATION' and lm.source_id =o.id) as officeCount, " +
//            "( select count(cud.id) from location_mapping lm1 " +
//            "INNER JOIN ext_data_stage_definition edsd ON JSON_UNQUOTE(JSON_EXTRACT(edsd.ext_json, '$.maging_physical_loc_id')) = lm1.location_id " +
//            "INNER JOIN customer_subscription sc ON sc.subscription_template = edsd.ref_id " +
//            "INNER JOIN user u ON u.acct_id = sc.account_id " +
//            "INNER JOIN user_level_privilege prv ON prv.account_id = u.acct_id  and prv.role_id is null " +
//            "INNER JOIN entity e ON e.id = prv.entity_id " +
//            "INNER JOIN customer_detail cud ON cud.entity_id = e.id " +
//            "WHERE cud.states NOT IN ('APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING') " +
//            "and lm1.source_id = o.id AND lm1.source_type = 'Organization') as customerCount, " +
//            "(select count(eg.id) from user_group ug inner join entity_group eg on eg.user_group_id = ug.id where ug.ref_id = o.id and ug.ref_type='Organization') as assetCount " +
//            "FROM organization o " +
//            "LEFT JOIN entity e ON o.contact_person_id = e.id " +
//            "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
//            "WHERE o.id =(:orgId) ",nativeQuery = true)
//    OrganizationManagementTemplate getAllBusinessUnitDetails(@Param("orgId") Long orgId);

    @Query(value = "SELECT o.id as orgId,o.organization_name as unitName, o.logo_image as unitImg,o.status as status," +
            "o.unit_category_type as unitType, o.unit_category as unitCategory, date_format(o.created_at, '%b %d, %Y') as createdAt," +
            "o.business_description as unitDescription,e.id as unitManagerId, ed.uri as unitManagerImg, e.entity_name as unitManagerName, " +
            "e.contact_person_email as unitManagerEmail, e.contact_person_phone as unitManagerPhone, " +
            "(select count(lm.id) from location_mapping lm where lm.source_type ='ORGANIZATION' and lm.source_id =o.id) as officeCount, " +
            "( select count(cud.id) from location_mapping lm1 " +
            "INNER JOIN ext_data_stage_definition edsd ON JSON_UNQUOTE(JSON_EXTRACT(edsd.ext_json, '$.maging_physical_loc_id')) = lm1.location_id " +
            "INNER JOIN customer_subscription sc ON sc.ext_subs_id = edsd.subs_id " +
            "INNER JOIN user u ON u.acct_id = sc.account_id " +
            "INNER JOIN user_level_privilege prv ON prv.account_id = u.acct_id  and prv.role_id is null " +
            "INNER JOIN customer_detail cud ON cud.entity_id = prv.entity_id " +
            "WHERE cud.states NOT IN ('APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING') " +
            "and lm1.source_id = o.id AND lm1.source_type = 'Organization'and sc.account_id is not null) as customerCount, " +
            "(select count(empDetail.id) from location_mapping lm inner join employee_detail empDetail on empDetail.primary_office = lm.location_id " +
            "inner join entity e on e.id = empDetail.entity_id and e.entity_type = 'Employee' " +
            "where lm.source_id = o.id AND lm.source_type = 'Organization') as assetCount " +
            "FROM organization o " +
            "LEFT JOIN entity e ON o.contact_person_id = e.id " +
            "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
            "WHERE o.id =(:orgId) ",nativeQuery = true)
    OrganizationManagementTemplate getAllBusinessUnitDetails(@Param("orgId") Long orgId);

    @Query(value = "SELECT  u.acct_id AS accountId, u.first_name AS firstName, u.last_name AS lastName, " +
            "u.email_address AS emailAddress, date_format(u.created_at, '%b %d, %Y') AS createdAt, " +
            "cud.customer_type AS customerType, e.status AS status, cud.states AS stateCustomer, e.contact_person_phone AS phone, " +
            "CONCAT_WS(',', ploc.ext1, ploc.ext2, ploc.add3) AS region, e.id AS entityId, " +
            "(CASE WHEN sc1.is_checked IS TRUE THEN '1' WHEN sc1.is_checked IS FALSE THEN '0' ELSE 'Null' END) AS isChecked, " +
            "ed.uri AS profileUrl, cud.self_initiative AS selfInitiative, cud.has_Login AS hasLogin, cud.mobile_allowed AS mobileAllowed " +
            "FROM organization o " +
            "LEFT JOIN location_mapping lm ON lm.source_id = o.id AND lm.source_type = 'Organization' " +
            "LEFT JOIN ext_data_stage_definition edsd ON JSON_UNQUOTE(JSON_EXTRACT(edsd.ext_json, '$.maging_physical_loc_id')) = lm.location_id " +
            "LEFT JOIN customer_subscription sc ON sc.ext_subs_id = edsd.subs_id  AND sc.account_id is not null " +
            "LEFT JOIN user u ON u.acct_id = sc.account_id " +
            "LEFT JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
            "LEFT JOIN entity e ON e.id = prv.entity_id " +
            "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
            "LEFT JOIN customer_detail cud ON cud.entity_id = e.id " +
            "LEFT JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
            "LEFT JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
            "LEFT JOIN ca_soft_credit_check sc1 ON sc1.customer_no = e.id " +
            "WHERE cud.states NOT IN ('APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING') " +
            "AND (locmap.primary_ind = 'Y' OR locmap.primary_ind IS NULL) " +
            "AND (ploc.is_deleted IS NULL OR ploc.is_deleted = FALSE) " +
            "AND (e.is_deleted IS NULL OR e.is_deleted = FALSE) " +
            "AND prv.role_id IS NULL " +
            "AND o.id = :orgId " +
            "AND (:searchWord IS NULL OR " +
            "u.first_name LIKE %:searchWord% " +
            "OR u.last_name LIKE %:searchWord% " +
            "OR u.email_address LIKE %:searchWord% " +
            "OR ploc.zip_code LIKE %:searchWord% " +
            "OR e.contact_person_phone LIKE %:searchWord% " +
            "OR cud.customer_type LIKE %:searchWord% ) " +
            "ORDER BY u.created_at DESC",
            countQuery = "SELECT DISTINCT COUNT( u.acct_id ) " +
                    "FROM organization o " +
                    "LEFT JOIN location_mapping lm ON lm.source_id = o.id AND lm.source_type = 'Organization' " +
                    "LEFT JOIN ext_data_stage_definition edsd ON JSON_UNQUOTE(JSON_EXTRACT(edsd.ext_json, '$.maging_physical_loc_id')) = lm.location_id " +
                    "LEFT JOIN customer_subscription sc ON sc.ext_subs_id = edsd.subs_id  AND sc.account_id is not null " +
                    "LEFT JOIN user u ON u.acct_id = sc.account_id " +
                    "LEFT JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
                    "LEFT JOIN entity e ON e.id = prv.entity_id " +
                    "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
                    "LEFT JOIN customer_detail cud ON cud.entity_id = e.id " +
                    "LEFT JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
                    "LEFT JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
                    "LEFT JOIN ca_soft_credit_check sc1 ON sc1.customer_no = e.id " +
                    "WHERE cud.states NOT IN ('APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING') " +
                    "AND (locmap.primary_ind = 'Y' OR locmap.primary_ind IS NULL) " +
                    "AND (ploc.is_deleted IS NULL OR ploc.is_deleted = FALSE) " +
                    "AND (e.is_deleted IS NULL OR e.is_deleted = FALSE) " +
                    "AND prv.role_id IS NULL " +
                    "AND o.id = :orgId",
            nativeQuery = true)
    Page<UserTemplate> getAllSubOrgCustomerList(@Param("orgId") Long orgId,
                                                @Param("searchWord") String searchWord,
                                                Pageable pageable);

    @Query("select count(o.id) from Organization o where o.parentOrgId=:orgId and o.status='Active'")
    Long totalSubOrgCount(@Param("orgId") Long orgId);

    @Query("select count(lm.id) from Organization o " +
            "left join LocationMapping lm " +
            "On lm.sourceId = o.id AND lm.sourceType = 'ORGANIZATION'" +
            "where o.parentOrgId=:orgId and o.status='Active'")
    Long totalSubOrgLocationCount(@Param("orgId") Long orgId);

//   TODO: when Employee Management vemap

//    @Query("SELECT DISTINCT new com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementTile(" +
//            "eg.entityRole.entity.id,eg.entityRole.entity.entityName,ed.uri,eg.entityRole.entity.contactPersonEmail," +
//            "eg.entityRole.entity.contactPersonPhone,eg.entityRole.entity.status, " +
//            "emp.hasLogin,emp.mobileAllowed,date_format(eg.entityRole.entity.createdAt, '%b %d, %Y'),eg.entityRole.functionalRoles.name" +
//            ",eg.entityRole.functionalRoles.category,eg.entityRole.functionalRoles.subCategory) " +
//            "from UserGroup ug " +
//            "inner join EntityGroup eg ON eg.userGroup.id = ug.id AND ug.refType = 'Organization'" +
//            "inner join EmployeeDetail emp on emp.entityId = eg.entityRole.entity.id " +
//            "left join EntityDetail ed ON ed.entity.id = eg.entityRole.entity.id " +
//            "WHERE cast(ug.refId as long) = :orgId " +
//            "AND eg.entityRole.entity.entityType = 'Employee' " +
//            "AND (eg.entityRole.entity.isDeleted IS NULL OR eg.entityRole.entity.isDeleted = FALSE) " +
//            "AND (" +
//            ":searchWord IS NULL " +
//            "OR eg.entityRole.entity.entityName LIKE CONCAT('%', :searchWord, '%') " +
//            "OR eg.entityRole.entity.contactPersonPhone LIKE CONCAT('%', :searchWord, '%') " +
//            "OR eg.entityRole.entity.contactPersonEmail LIKE CONCAT('%', :searchWord, '%')) " +
//            "ORDER BY date_format(eg.entityRole.entity.createdAt, '%b %d, %Y') DESC")
//    Page<EmployeeManagementTile> getAllSubOrgEmployeeLists(@Param("orgId") Long orgId,
//                                                           @Param("searchWord") String searchWord,
//                                                           Pageable pageable);


    @Query("SELECT DISTINCT new com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementTile(" +
            "e.id,e.entityName,ed.uri,e.contactPersonEmail,e.contactPersonPhone,e.status, " +
            "emp.hasLogin,emp.mobileAllowed,date_format(e.createdAt, '%b %d, %Y'),e.createdAt,o.id) " +
            "from Organization o " +
            "inner join LocationMapping lm ON lm.sourceId = o.id AND lm.sourceType = 'Organization'" +
            "inner join EmployeeDetail emp on CAST(emp.primaryOffice AS long) = lm.locationId " +
            "inner join Entity e ON e.id = emp.entityId " +
            "left join EntityDetail ed ON ed.entity.id = e.id " +
            "WHERE o.id= :orgId " +
            "AND e.entityType = 'Employee' " +
            "AND (e.isDeleted IS NULL OR e.isDeleted = FALSE) " +
            "AND (:searchWord IS NULL  " +
            "OR e.entityName LIKE CONCAT('%', :searchWord, '%') " +
            "OR e.contactPersonPhone LIKE CONCAT('%', :searchWord, '%') " +
            "OR e.contactPersonEmail LIKE CONCAT('%', :searchWord, '%') )" +
            "ORDER BY e.createdAt DESC")
    Page<EmployeeManagementTile> getAllSubOrgEmployeeLists(@Param("orgId") Long orgId,
                                                           @Param("searchWord") String searchWord,
                                                           Pageable pageable);

    //    TODO: Reporting Manager is missing
    @Query("SELECT DISTINCT new com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementTile(" +
            "eg.entityRole.entity.id,eg.entityRole.entity.entityName,ed.uri,eg.entityRole.entity.contactPersonEmail," +
            "eg.entityRole.entity.contactPersonPhone,eg.entityRole.entity.status, " +
            "date_format(eg.entityRole.entity.createdAt, '%b %d, %Y'),eg.entityRole.functionalRoles.name," +
            "eg.entityRole.functionalRoles.category,eg.entityRole.functionalRoles.subCategory," +
            "ploc.ext1, ploc.ext2, ploc.add3) " +
            "from UserGroup ug " +
            "inner join EntityGroup eg ON eg.userGroup.id = ug.id AND ug.refType = 'WorkOrder' " +
            "inner join EmployeeDetail emp on emp.entityId = eg.entityRole.entity.id " +
            "left join EntityDetail ed ON ed.entity.id = eg.entityRole.entity.id " +
            "left join PhysicalLocation ploc ON ploc.id = CAST(emp.primaryOffice AS long) " +
            "WHERE ug.refId= CAST(:wordOrderId AS string) " +
            "AND eg.entityRole.entity.entityType = 'Employee' " +
            "AND (eg.entityRole.entity.isDeleted IS NULL OR eg.entityRole.entity.isDeleted = FALSE) " +
            "AND (" +
            ":searchWord IS NULL " +
            "OR eg.entityRole.entity.entityName LIKE CONCAT('%', :searchWord, '%') " +
            "OR eg.entityRole.entity.contactPersonPhone LIKE CONCAT('%', :searchWord, '%') " +
            "OR eg.entityRole.entity.contactPersonEmail LIKE CONCAT('%', :searchWord, '%')) " +
            "ORDER BY date_format(eg.entityRole.entity.createdAt, '%b %d, %Y') DESC")
    Page<EmployeeManagementTile> getAllSubOrgEmployeeWordOrderLists(@Param("wordOrderId") String wordOrderId,
                                                                    @Param("searchWord") String searchWord,
                                                                    Pageable pageable);

    @Query("SELECT new com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO(" +
            "ploc.id, ploc.locationName, ploc.otherDetails, ploc.add1, ploc.add2, ploc.add3, " +
            "ploc.geoLat, ploc.geoLong, ploc.googleCoordinates, ploc.ext1, ploc.ext2, ploc.ext3, ploc.ext4, ploc.zipCode, " +
            "(CASE " +
            "WHEN MAX(CASE WHEN org.unitCategory = :unitCategory THEN 1 ELSE 0 END) = 1 THEN false " +
            "ELSE true " +
            "END)) " +
            "FROM PhysicalLocation ploc " +
            "LEFT JOIN LocationMapping locmap ON locmap.locationId = ploc.id AND locmap.sourceType = 'Organization' " +
            "LEFT JOIN Organization org ON locmap.sourceId = org.id " +
            "WHERE ploc.organization.id = :masterOrgId " +
            "AND (ploc.isDeleted IS NULL OR ploc.isDeleted = false) " +
            "AND ploc.category = 'Business' " +
            "AND NOT EXISTS (SELECT 1 FROM LocationMapping lm WHERE lm.locationId = ploc.id AND lm.sourceId = :subOrgId AND lm.sourceType = 'Organization') " +
            "AND (:searchWords IS NULL OR " +
            "LOWER(ploc.locationName) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.otherDetails) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.add1) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.add2) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.add3) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.googleCoordinates) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.ext1) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.ext2) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.ext3) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.ext4) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(ploc.zipCode) LIKE LOWER(CONCAT('%', :searchWords, '%'))) " +
            "GROUP BY ploc.id, ploc.locationName, ploc.otherDetails, ploc.add1, ploc.add2, ploc.add3, " +
            "ploc.geoLat, ploc.geoLong, ploc.googleCoordinates, ploc.ext1, ploc.ext2, ploc.ext3, ploc.ext4, ploc.zipCode")
    List<PhysicalLocationDTO> getFilteredPhysicalLocations(@Param("masterOrgId") Long masterOrgId,
                                                           @Param("subOrgId") Long subOrgId,
                                                           @Param("unitCategory") String unitCategory,
                                                           @Param("searchWords") String searchWords);

    @Query(value = "SELECT " +
            "GROUP_CONCAT(DISTINCT ploc.category SEPARATOR ', ') AS locationCategory, " +
            "GROUP_CONCAT(DISTINCT org.unit_category SEPARATOR ', ') AS businessUnit, " +
            "GROUP_CONCAT(DISTINCT ploc.location_type SEPARATOR ', ') AS locationType " +
            "FROM organization org " +
            "LEFT JOIN location_mapping lm ON lm.source_id = org.id AND lm.source_type = 'ORGANIZATION' " +
            "LEFT JOIN physical_locations ploc ON ploc.id = lm.location_id " +
            "WHERE (org.id=:orgId or org.parent_org_id = :orgId) " +
            "AND (ploc.location_type IS NOT NULL OR ploc.location_type <> '')  " +
            "AND ((ploc.category IS NOT NULL OR ploc.category <> '') AND ploc.category = 'Business') " +
            "AND (org.unit_category IS NOT NULL OR org.unit_category <> '')",
            nativeQuery = true)
    PhysicalLocationOMFilterTemplate getOrgOfficeFilters(@Param("orgId") Long orgId);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Organization o WHERE o.organizationName = :name AND (:id IS NULL OR o.id != :id)")
    boolean isOrganizationNameExists(@Param("name") String name, @Param("id") Long id);

    @Query("select new com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO(er.id,e.id,e.entityName," +
            "e.contactPersonEmail,er.status,fr.name,e.contactPersonPhone,fr.category,fr.subCategory,date_format(emp.dateOfJoining, '%b %d, %Y'),ed.uri) " +
            "from Entity e " +
            "inner join e.entityRoles er " +
            "left join er.functionalRoles fr " +
            "inner join EmployeeDetail emp on emp.entityId = e.id " +
            "left join EntityDetail ed on ed.entity.id = e.id " +
            "where e.entityType = 'Employee' and e.organization.id = :masterOrgId " +
            "and (e.isDeleted IS NULL OR e.isDeleted = FALSE) " +
            "and (er.isDeleted IS NULL OR er.isDeleted = FALSE)" +
            "and (er.status IS NULL OR er.status = TRUE) " +
            "and (:subOrgId IS NULL OR er.id not in (select eg.entityRole.id from UserGroup ug " +
            "inner join EntityGroup eg on eg.userGroup.id = ug.id " +
            "where CAST(ug.refId As long)= :subOrgId and ug.refType ='Organization')) " +
            "AND (:searchWords IS NULL OR " +
            "LOWER(e.entityName) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(e.contactPersonEmail) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(e.contactPersonPhone) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(fr.name) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(fr.subCategory) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(fr.category) LIKE LOWER(CONCAT('%', :searchWords, '%')))")
    List<DefaultUserGroupResponseDTO> getAllAvailableEmployees(@Param("masterOrgId") Long masterOrgId, @Param("subOrgId") Long subOrgId, @Param("searchWords") String searchWords);


    @Query(value = "SELECT DISTINCT * FROM (" +
            "SELECT u.acct_id AS accountId, u.first_name AS firstName, u.last_name AS lastName, " +
            "u.email_address AS emailAddress, date_format(u.created_at, '%b %d, %Y') AS createdAt, " +
            "cud.customer_type AS customerType, e.status AS status, cud.states AS stateCustomer, e.contact_person_phone AS phone, " +
            "CONCAT_WS(',', ploc.ext1, ploc.ext2, ploc.add3) AS region, e.id AS entityId, " +
            "(CASE WHEN sc1.is_checked IS TRUE THEN '1' WHEN sc1.is_checked IS FALSE THEN '0' ELSE 'Null' END) AS isChecked, " +
            "ed.uri AS profileUrl, cud.self_initiative AS selfInitiative, cud.has_Login AS hasLogin, cud.mobile_allowed AS mobileAllowed, " +
            "ROW_NUMBER() OVER (PARTITION BY u.acct_id ORDER BY u.created_at DESC) AS row_num " +
            "FROM organization o " +
            "LEFT JOIN location_mapping lm ON lm.source_id = o.id AND lm.source_type = 'Organization' " +
            "LEFT JOIN ext_data_stage_definition edsd ON JSON_UNQUOTE(JSON_EXTRACT(edsd.ext_json, '$.maging_physical_loc_id')) = lm.location_id " +
            "LEFT JOIN customer_subscription sc ON sc.ext_subs_id = edsd.subs_id AND sc.account_id IS NOT NULL " +
            "LEFT JOIN user u ON u.acct_id = sc.account_id " +
            "LEFT JOIN user_level_privilege prv ON prv.account_id = u.acct_id " +
            "LEFT JOIN entity e ON e.id = prv.entity_id " +
            "LEFT JOIN entity_detail ed ON ed.entity_id = e.id " +
            "LEFT JOIN customer_detail cud ON cud.entity_id = e.id " +
            "LEFT JOIN location_mapping locmap ON locmap.source_id = u.acct_id " +
            "LEFT JOIN physical_locations ploc ON ploc.id = locmap.location_id " +
            "LEFT JOIN ca_soft_credit_check sc1 ON sc1.customer_no = e.id " +
            "WHERE cud.states NOT IN ('APPROVAL PENDING', 'LEAD', 'CONTRACT PENDING', 'INTERIM-CUSTOMER', 'PROSPECT', 'REQUEST PENDING') " +
            "AND (locmap.primary_ind = 'Y' OR locmap.primary_ind IS NULL) " +
            "AND (ploc.is_deleted IS NULL OR ploc.is_deleted = FALSE) " +
            "AND (e.is_deleted IS NULL OR e.is_deleted = FALSE) " +
            "AND prv.role_id IS NULL " +
            "AND o.id = :orgId " +
            "AND (:searchWord IS NULL OR " +
            "u.first_name LIKE %:searchWord% " +
            "OR u.last_name LIKE %:searchWord% " +
            "OR u.email_address LIKE %:searchWord% " +
            "OR ploc.zip_code LIKE %:searchWord% " +
            "OR e.contact_person_phone LIKE %:searchWord% " +
            "OR cud.customer_type LIKE %:searchWord% ) " +
            ") AS subquery " +
            "WHERE row_num = 1",
            nativeQuery = true)
    Page<UserTemplate> getAllSubOrgsCustomerList(@Param("orgId") Long orgId,
                                                @Param("searchWord") String searchWord,
                                                Pageable pageable);

    @Query(value = "select extDataStageDefinition.ref_id as refId , MAX(extDataStageDefinition.ref_type) as gardenName , MAX(extDataStageDefinition.product_name) as gardenType " +
            ",MAX(en.entity_name) as gardenOwner , " +
            "MAX(DATE_FORMAT(STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.mp_json, '$.S_SSDT')), '%a %b %d %T PKT %Y' ), '%M %d %Y' )) as gardenRegistrationDate, " +
            "MAX(DATE_FORMAT(JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.go_garden_live_date')),'%M %d %Y' )) as goLiveDate " +
            "from location_mapping locationMapping " +
            "left join ext_data_stage_definition extDataStageDefinition on " +
            "locationMapping.location_id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.maging_physical_loc_id')) " +
            "left join entity_role entityRole on " +
            "entityRole.id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.csg_garden_owner_id')) " +
            "left join entity en on " +
            "en.id = entityRole.entity_id " +
            "where locationMapping.source_id = :gardenId " +
            "and (extDataStageDefinition.product_name in (:gardenTypeList) or (:gardenTypeIsPresent = false )) " +
            "and (entityRole.id in (:gardenOwnerList) or (:gardenOwnerIsPresent = false )) " +
            "AND (:groupBy IS NULL OR " +
            "(:groupBy = 'GARDEN TYPE' AND extDataStageDefinition.product_name = :groupByName) OR " +
            "(:groupBy = 'GARDEN OWNER' AND en.entity_name = :groupByName)) " +
            "AND ( (:startDateRegistrationDate IS NULL AND :endDateRegistrationDate IS NULL )" +
            "OR (STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.mp_json,'$.S_SSDT')),'%a %b %d %T PKT %Y') BETWEEN STR_TO_DATE(:startDateRegistrationDate, '%Y-%m-%d') AND STR_TO_DATE(:endDateRegistrationDate, '%Y-%m-%d') )) " +
            "AND ((:startDateGoLiveDate IS NULL AND :endDateGoLiveDate IS NULL ) " +
            "OR (CAST(JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.go_garden_live_date')) AS DATETIME) BETWEEN STR_TO_DATE(:startDateGoLiveDate, '%Y-%m-%d') AND STR_TO_DATE(:endDateGoLiveDate, '%Y-%m-%d') ))" +
            "and locationMapping.source_type = 'Organization' " +
            "AND (:searchWord IS NULL OR " +
            "extDataStageDefinition.ref_type LIKE %:searchWord% " +
            "OR extDataStageDefinition.product_name LIKE %:searchWord% " +
            "OR en.entity_name LIKE %:searchWord% ) " +
            "GROUP BY extDataStageDefinition.ref_id ",
            countQuery = "SELECT COUNT(DISTINCT extDataStageDefinition.ref_id) FROM location_mapping locationMapping " +
                    "left join ext_data_stage_definition extDataStageDefinition on " +
                    "locationMapping.location_id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.maging_physical_loc_id')) " +
                    "left join entity_role entityRole on " +
                    "entityRole.id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.csg_garden_owner_id')) " +
                    "left join entity en on " +
                    "en.id = entityRole.entity_id " +
                    "where locationMapping.source_id = :gardenId " +
                    "and (extDataStageDefinition.product_name in (:gardenTypeList) or (:gardenTypeIsPresent = false )) " +
                    "and (entityRole.id in (:gardenOwnerList) or (:gardenOwnerIsPresent = false )) " +
                    "AND (:groupBy IS NULL OR " +
                    "(:groupBy = 'GARDEN TYPE' AND extDataStageDefinition.product_name = :groupByName) OR " +
                    "(:groupBy = 'GARDEN OWNER' AND en.entity_name = :groupByName)) " +
                    "AND ( (:startDateRegistrationDate IS NULL AND :endDateRegistrationDate IS NULL )" +
                    "OR (STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.mp_json,'$.S_SSDT')),'%a %b %d %T PKT %Y') BETWEEN STR_TO_DATE(:startDateRegistrationDate, '%Y-%m-%d') AND STR_TO_DATE(:endDateRegistrationDate, '%Y-%m-%d') )) " +
                    "AND ((:startDateGoLiveDate IS NULL AND :endDateGoLiveDate IS NULL ) " +
                    "OR (CAST(JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.go_garden_live_date')) AS DATETIME) BETWEEN STR_TO_DATE(:startDateGoLiveDate, '%Y-%m-%d') AND STR_TO_DATE(:endDateGoLiveDate, '%Y-%m-%d') ))" +
                    "and locationMapping.source_type = 'Organization' " +
                    "AND (:searchWord IS NULL OR " +
                    "LOWER(extDataStageDefinition.ref_type) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
                    "OR LOWER(extDataStageDefinition.product_name) LIKE LOWER(CONCAT('%' , :searchWord , '%' ))" +
                    "OR LOWER(en.entity_name) LIKE LOWER(CONCAT('%' , :searchWord , '%')) )"
            , nativeQuery = true)
    Page<LinkedSitesTemplate> findLinkedSiteByGardenId(@Param("gardenId") Long gardenId,
                                                       @Param("searchWord") String searchWord,
                                                       @Param("gardenType") String gardenType,
                                                       @Param("gardenTypeList") List<String> gardenTypeList,
                                                       @Param("gardenOwnerList") List<Long> gardenOwnerList,
                                                       @Param("gardenOwnerIsPresent") Boolean gardenOwnerIsPresent ,
                                                       @Param("gardenTypeIsPresent")  Boolean gardenTypeIsPresent ,
                                                       @Param("startDateRegistrationDate")   String startDateRegistrationDate ,
                                                       @Param("endDateRegistrationDate")  String endDateRegistrationDate ,
                                                       @Param("startDateGoLiveDate")  String startDateGoLiveDate ,
                                                       @Param("endDateGoLiveDate")  String endDateGoLiveDate ,
                                                       @Param("groupBy") String groupBy,
                                                       @Param("groupByName") String groupByName,
                                                       Pageable pageable);
    @Query(value = "SELECT DISTINCT " +
            "(CASE " +
            "WHEN :groupByType = 'GARDEN TYPE' THEN extDataStageDefinition.product_name " +
            "WHEN :groupByType = 'GARDEN OWNER' THEN en.entity_name " +
            "ELSE '' END ) AS groupBy,:isLeaf as isLeaf " +
            "FROM location_mapping locationMapping " +
            "LEFT JOIN ext_data_stage_definition extDataStageDefinition ON " +
            "locationMapping.location_id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.maging_physical_loc_id')) " +
            "LEFT JOIN entity_role entityRole ON " +
            "entityRole.id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.csg_garden_owner_id')) " +
            "LEFT JOIN entity en ON " +
            "en.id = entityRole.entity_id " +
            "WHERE locationMapping.source_type = 'Organization' " +
            "AND (CASE "  +
                    "WHEN :groupByType = 'GARDEN TYPE' THEN (extDataStageDefinition.product_name IS NOT NULL AND extDataStageDefinition.product_name <> '') " +
                    "WHEN :groupByType = 'GARDEN OWNER' THEN (en.entity_name IS NOT NULL AND  en.entity_name <> '')" +
                    "ELSE FALSE END = TRUE )",
            countQuery = "SELECT COUNT(DISTINCT " +
                    "CASE " +
                    "WHEN :groupByType = 'GARDEN TYPE' THEN extDataStageDefinition.product_name " +
                    "WHEN :groupByType = 'GARDEN OWNER' THEN en.entity_name " +
                    "ELSE '' END) " +
                    "FROM location_mapping locationMapping " +
                    "LEFT JOIN ext_data_stage_definition extDataStageDefinition ON " +
                    "locationMapping.location_id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.maging_physical_loc_id')) " +
                    "LEFT JOIN entity_role entityRole ON " +
                    "entityRole.id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.csg_garden_owner_id')) " +
                    "LEFT JOIN entity en ON " +
                    "en.id = entityRole.entity_id " +
                    "WHERE locationMapping.source_type = 'Organization' " +
                    "AND (CASE " +
                    "WHEN :groupByType = 'GARDEN TYPE' THEN (extDataStageDefinition.product_name IS NOT NULL AND extDataStageDefinition.product_name <> '') " +
                    "WHEN :groupByType = 'GARDEN OWNER' THEN (en.entity_name IS NOT NULL AND  en.entity_name <> '')" +
                    "ELSE FALSE END = TRUE )",
            nativeQuery = true)
    Page<LinkedSitesTemplate> findLinkedSiteGroupBy(@Param("groupByType") String groupByType,
                                                    @Param("isLeaf") boolean isLeaf,
                                                    Pageable pageable);

    @Query(value = "SELECT " +
            "GROUP_CONCAT(DISTINCT CONCAT(en.entity_name, ' - ', functionalRole.name, ';',entityRole.id)) AS gardenOwner, " +
            "GROUP_CONCAT(DISTINCT extDataStageDefinition.product_name) AS gardenType " +
            "FROM " +
            "location_mapping locationMapping " +
            "left join ext_data_stage_definition extDataStageDefinition on " +
            "locationMapping.location_id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.maging_physical_loc_id')) " +
            "left join entity_role entityRole on  " +
            "entityRole.id = JSON_UNQUOTE(JSON_EXTRACT(extDataStageDefinition.ext_json, '$.csg_garden_owner_id')) " +
            "left join entity en on  " +
            "en.id = entityRole.entity_id  " +
            "left join functional_roles functionalRole on " +
            "functionalRole.id = entityRole.functional_role_id " +
            "where locationMapping.source_type = 'Organization' " +
            "AND (en.entity_name IS NOT NULL OR en.entity_name <> '')  " +
            "AND (entityRole.id IS NOT NULL OR entityRole.id <> '') " +
            "AND (extDataStageDefinition.product_name IS NOT NULL OR extDataStageDefinition.product_name <> '') " ,nativeQuery = true)
    LinkedSitesFiltersTemplate findAllFiltersData();
}