package com.solar.api.tenant.repository.UserGroup;

import com.solar.api.tenant.mapper.EntityRoleResponseDTO;
import com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile;
import com.solar.api.tenant.mapper.user.userGroup.UserGroupDTO;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.userGroup.EntityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntityRoleRepository extends JpaRepository<EntityRole, Long> {

    List<EntityRole> findByStatus(boolean status);

    List<EntityRole> findByIdIn(List<Long> ids);

    List<EntityRole> findByEntity(Entity entity);

    List<EntityRole> findByIdInAndStatus(List<Long> ids, boolean status);

    EntityRole findByIdAndStatus(Long id, boolean status);


    List<EntityRole> findAllByEntityId(Long entityId);

    List<EntityRole> findByFunctionalRoles(FunctionalRoles functionalRole);

    @Query(" select new com.solar.api.tenant.mapper.EntityRoleResponseDTO" +
            "(er.id as entityRoleId, e.id as entityId, " +
            "fr.id as functionalRoleId, e.entityName as entityName, fr.name as functionalRoleName, ed.uri as uri) from EntityRole er " +
            " inner join  Entity e on e.id = er.entity.id " +
            "inner join  EntityDetail ed on  ed.entity.id = e.id " +
            "inner join FunctionalRoles fr on fr.id =  er.functionalRoles.id ")
    List<EntityRoleResponseDTO> getAllEntityRole();

    @Query(" select er from EntityRole er where er.entity.id=:entityId and er.functionalRoles.id=:roleId ")
    EntityRole findByEntityIdAndFunctionRoleId(@Param("entityId") Long entityId, @Param("roleId") Long roleId);

    @Query(" select er from EntityRole er where er.entity.id=:entityId and er.isDeleted=:isDeleted and er.id not in (:existingRoleIds) ")
    List<EntityRole> findAllByEntityIdAndIsDeleted(@Param("entityId") Long entityId,@Param("isDeleted") Boolean isDeleted, @Param("existingRoleIds") List<Long> existingRoleIds);

    @Query(" select new com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile(" +
            "er.id as entityRoleId,e.id as entityId,fr.id as functionalRoleId,fr.name as functionalRoleName," +
            "e.entityName as entityName,fr.category as category,e.contactPersonEmail as email," +
            "ed.uri as imageUri) from EntityRole er " +
            "left join  Entity e on e.id = er.entity.id " +
            "left join  EntityDetail ed on  ed.entity.id = e.id " +
            "left join FunctionalRoles fr on fr.id =  er.functionalRoles.id " +
            "where (er.isDeleted=false or  er.isDeleted is null) and fr.id =:functionalRoleId ")
    List<EntityFunctionalRoleTile> findByFunctionalRoleId(Long functionalRoleId);

    @Query(" select er from EntityRole er where er.entity.id=:entityId and er.functionalRoles.id=:roleId and (er.isDeleted = false or er.isDeleted is null) and er.status = true")
    EntityRole findByEntityIdAndFunctionRoleIdAndIsDeleted(@Param("entityId") Long entityId, @Param("roleId") Long roleId);

    List<EntityRole> findAllByEntityIdAndIsDeleted(Long entityId,boolean isDeleted);

    @Query(" select new com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile(er.id, e.id, e.entityName ,ed.uri, prv.user.acctId ) " +
        "from EntityRole er " +
        "left join  Entity e on e.id = er.entity.id " +
        "left join  UserLevelPrivilege prv on prv.entity.id = e.id and prv.role.id is null " +
        "left join  EntityDetail ed on  ed.entity.id = e.id " +
        "where  er.id in (:entityRoleIds) ")
    List<EntityFunctionalRoleTile> findByEntityRoleIdIn(List<Long> entityRoleIds);

    @Query(" select new com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile(" +
            "er.id as entityRoleId,e.id as entityId,fr.id as functionalRoleId,fr.name as functionalRoleName," +
            "e.entityName as entityName,fr.category as category,e.contactPersonEmail as email," +
            "ed.uri as imageUri) from EntityRole er " +
            "left join  Entity e on e.id = er.entity.id " +
            "left join  EntityDetail ed on  ed.entity.id = e.id " +
            "left join FunctionalRoles fr on fr.id =  er.functionalRoles.id " +
            "where (er.isDeleted=false or  er.isDeleted is null) and fr.name IN (:functionalRoleName) ")
    List<EntityFunctionalRoleTile> findByFunctionalRoleName(@Param("functionalRoleName") List<String> functionalRoleName);
}
