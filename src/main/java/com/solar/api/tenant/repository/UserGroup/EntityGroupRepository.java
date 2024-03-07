package com.solar.api.tenant.repository.UserGroup;

import com.solar.api.tenant.mapper.EntityGroupTemplate;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementEntityTile;
import com.solar.api.tenant.model.userGroup.EntityGroup;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.model.userGroup.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntityGroupRepository extends JpaRepository<EntityGroup, Long> {

    List<EntityGroup> findByStatus(boolean status);

    List<EntityGroup> findByUserGroup(UserGroup userGroup);

    EntityGroup findByEntityRoleAndUserGroupAndIsDeleted(EntityRole entityRole, UserGroup userGroup, Boolean isDeleted);

    @Query("SELECT eg FROM EntityGroup eg WHERE userGroupId = :groupId AND status = true")
    List<EntityGroup> getResource(Long groupId);

    @Query(value = "SELECT * FROM entity_group WHERE user_group_id in (:userGroupId) AND status = true", nativeQuery = true)
    List<EntityGroup> findByUserGroupIds(List<Long> userGroupId);

    @Query("SELECT eg FROM EntityGroup eg WHERE eg.userGroup.id = :groupId AND eg.entityRole.id in (:entityRoleIds)")
    List<EntityGroup> findByEntityRolesAndUserGroup(@Param("entityRoleIds") List<Long> entityRoleIds, @Param("groupId") Long groupId);

    @Query("select count(eg.id) from EntityGroup eg where eg.userGroup.id in " +
            "(select ug.id from UserGroup ug where ug.refId is not null and ug.refType is not null) and eg.entityRole.id in " +
            "(select er.id from EntityRole er where er.entity.id = :entityId and er.isDeleted= :isDeleted and er.status=:status) " +
            "and eg.status=:status and eg.isDeleted=:isDeleted")
    Integer findUserGroupCountByEntityId(@Param("entityId") Long entityId,
                                         @Param("isDeleted") Boolean isDeleted,
                                         @Param("status") Boolean status);

    @Query(value = "select eg.id as entityGroupId," +
            "e.entity_name as entityName, ed.personal_email as email," +
            "ed.designation as designation,ed.phone_number as contactNumber, " +
            "ed.employment_type as employeeType,ed.date_of_joining as joiningDate, edt.uri as imageURI,er.id as entityRoleId,e.id as entityId  " +
            "from entity_group eg " +
            "left join entity_role er on eg.entity_role_id = er.id " +
            "left join entity e on er.entity_id = e.id " +
            "left join employee_detail ed on e.id = ed.entity_id " +
            "left join entity_detail edt on e.id = edt.entity_id " +
            "where eg.user_group_id = :userGroupId AND eg.status = true", nativeQuery = true)
    List<EntityGroupTemplate> findByUserGroupId(Long userGroupId);

    @Query("select count(eg.id) from EntityGroup eg where eg.userGroup.id in " +
            "(select ug.id from UserGroup ug where ug.refId is not null and ug.refType is not null) and eg.entityRole.id in " +
            "(select er.id from EntityRole er where er.functionalRoles.id = :functionalRoleId and er.entity.id= :entityId " +
            "and (er.isDeleted= false or er.isDeleted is null) and er.status=:status) " +
            "and eg.status=:status and eg.isDeleted=false")
    Integer findUserGroupCountByFunctionalRoleId(@Param("functionalRoleId") Long functionalRoleId, @Param("entityId") Long entityId,
                                                 @Param("status") Boolean status);

    @Query(value = "select eg.* from entity_group eg " +
            "left join entity_role er on eg.entity_role_id = er.id " +
            "left join functional_roles fr on er.functional_role_id = fr.id " +
            "where er.entity_id = :entityId and fr.id = :roleId and eg.status=true and (eg.is_deleted=false or eg.is_deleted is null)", nativeQuery = true)
    List<EntityGroup> findByEntityRoleId(@Param("entityId") Long entityId, @Param("roleId") Long roleId);

    List<EntityGroup> findByUserGroupAndStatus(UserGroup userGroup, boolean status);

    @Query(" select er from EntityGroup er where er.userGroup.id=:id and er.isDeleted=:isDeleted and er.entityRole.id not in (:entityRoleIds) ")
    List<EntityGroup> findAllByEntityGroupIdAndIsDeleted(@Param("id") Long id, @Param("isDeleted") Boolean isDeleted, @Param("entityRoleIds") List<Long> entityRoleIds);

    @Query(" select er from EntityGroup er where er.userGroup.id=:id and er.isDeleted=:isDeleted and er.entityRole.id in (:entityRoleIds) ")
    List<EntityGroup> findAllByEntityGroupIdAndIsDeletedTrue(@Param("id") Long id, @Param("isDeleted") Boolean isDeleted, @Param("entityRoleIds") List<Long> entityRoleIds);

    @Query("select count(eg) from EntityGroup eg where eg.userGroup.id in (select ug.id from UserGroup ug " +
            "where ug.refId = :refId and ug.status = :status and ug.isActive =:isActive and ug.isDeleted =:isDeleted) " +
            "and eg.status=:status and eg.isDeleted=:isDeleted")
    Integer getResourceCountByRefId(@Param("refId") String refId, @Param("status") Boolean status,
                                    @Param("isActive") Boolean isActive, @Param("isDeleted") Boolean isDeleted);

    @Query("select new com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementEntityTile(e,ug.refId) from Entity  e " +
            "inner join EntityRole er on e.id = er.entity.id  " +
            "inner join EntityGroup eg on er.id = eg.entityRole.id  " +
            "inner join UserGroup ug on eg.userGroup.id = ug.id " +
            "where eg.userGroup.id in (select ug.id from UserGroup ug " +
            "where ug.refId in( :refIds) and ug.status = :status and ug.isActive =:isActive and ug.isDeleted =:isDeleted) " +
            "and eg.status=:status and eg.isDeleted=:isDeleted " +
            "and e.id = :entityId")
    List<ProjectManagementEntityTile> getResourceByRefIdAndEntityId(@Param("refIds") List<String> refIds, @Param("status") Boolean status,
                                                                    @Param("isActive") Boolean isActive, @Param("isDeleted") Boolean isDeleted,
                                                                    @Param("entityId") Long entityId);

    @Query("Select eg from EntityGroup eg where eg.userGroup in (:userGroup) and eg.status=:status and eg.isDeleted=:isDeleted ")
    List<EntityGroup> findAllByUserGroup(UserGroup userGroup, boolean status, boolean isDeleted);
}
