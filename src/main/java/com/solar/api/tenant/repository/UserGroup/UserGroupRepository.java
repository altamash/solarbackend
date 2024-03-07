package com.solar.api.tenant.repository.UserGroup;

import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.mapper.user.userGroup.EmployeeDTO;
import com.solar.api.tenant.mapper.user.userGroup.UserGroupDTO;
import com.solar.api.tenant.model.userGroup.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    UserGroup findByUserGroupName(String userGroupName);

    @Query(value = "SELECT e.id AS id, e.entity_name AS employeeName, e.company_name AS companyName, " +
            "e.contact_person_email AS employeeEmail, " +
            "e.contact_person_phone AS employeePhone, " +
            "fr.name AS employeeDesignation,er.id as entityRoleId, fr.id as functionalRoleId,ed.uri as imageUri, ed.id as entityDetailId FROM user_group ug " +
            "INNER JOIN entity_group eg ON eg.user_group_id = ug.id " +
            "INNER JOIN entity_role er ON er.id = eg.entity_role_id " +
            "INNER JOIN functional_roles fr ON fr.id = er.functional_role_id " +
            "INNER JOIN  entity e ON e.id = er.entity_id " +
            "left join entity_detail ed on e.id=ed.entity_id " +
            "WHERE ug.id = :userGroupId and eg.is_deleted=false and eg.status=true "+
            "order by e.created_at desc", nativeQuery = true)
    List<EmployeeDTO> findAllEmployeesByUserGroupId(Long userGroupId);

    UserGroup findByRefIdAndRefTypeAndStatus(String refId, String refType, boolean status);

    UserGroup findByRefIdAndStatus(String refId, boolean status);

    List<UserGroup> findByParentId(String parentId);

    @Query(" select new com.solar.api.tenant.mapper.user.userGroup.UserGroupDTO" +
            "(ug.id as id, ug.userGroupName as userGroupName, ug.userGroupType as userGroupType, " +
            "ug.status as status, ug.isActive as isActive, ug.refType as refType, ug.refId as refId, ug.parentId as parentId, ug.createdBy as createdBy, " +
            "ug.updatedBy as updatedBy, count(eg.id) as count) from UserGroup ug " +
            " inner JOIN  EntityGroup eg on eg.userGroup.id = ug.id " +
            " where eg.status=1 and ug.userGroupType =:groupType and ug.userGroupName <> 'Default User Group' and ug.isDeleted = false group by ug.id ")
    List<UserGroupDTO> getAllByUserGroupType(String groupType);

    UserGroup findByRefIdAndRefTypeAndStatusAndIsDeleted(String refId, String refType, boolean status, boolean isDeleted);

    @Query("select new com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO(er.id,e.id,e.entityName," +
            "e.contactPersonEmail, er.status, fr.name, e.contactPersonPhone, fr.category, fr.subCategory,date_format(emp.dateOfJoining, '%b %d, %Y'),ed.uri)" +
            "from UserGroup ug " +
            "inner join EntityGroup eg ON ug.id = eg.userGroup.id " +
            "inner join EntityRole er ON er.id = eg.entityRole.id " +
            "inner join Entity e ON e.id = er.entity.id " +
            "left join FunctionalRoles fr ON er.functionalRoles.id = fr.id " +
            "inner join EmployeeDetail emp ON emp.entityId = e.id " +
            "left join EntityDetail ed ON ed.entity.id = e.id " +
            "where (e.isDeleted IS NULL OR e.isDeleted = FALSE) " +
            "and (er.isDeleted IS NULL OR er.isDeleted = FALSE) " +
            "and (er.status IS NULL OR er.status = TRUE) " +
            "and ug.refId = CAST(:businessUnitId AS string)" +
            "and ug.refType = 'Organization' " +
            "and er.id not in (select eg.entityRole.id  FROM UserGroup ug " +
            "inner join EntityGroup eg ON eg.userGroup.id = ug.id " +
            "where ug.refId = :workOrderId AND ug.refType = 'WorkOrder') " +
            "AND (:searchWords IS NULL OR " +
            "LOWER(e.entityName) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(e.contactPersonEmail) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(e.contactPersonPhone) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(fr.name) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(fr.subCategory) LIKE LOWER(CONCAT('%', :searchWords, '%')) OR " +
            "LOWER(fr.category) LIKE LOWER(CONCAT('%', :searchWords, '%')))")
    List<DefaultUserGroupResponseDTO> getAllAvailableEmployeesForWorkOrder(@Param("businessUnitId") Long businessUnitId, @Param("workOrderId") String workOrderId, @Param("searchWords") String searchWords);
}
