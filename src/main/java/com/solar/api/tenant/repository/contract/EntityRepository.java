package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.mapper.contract.EntityResponseDTO;
import com.solar.api.tenant.model.contract.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntityRepository extends JpaRepository<Entity, Long> {
    List<Entity> findAllByStatus(String status);

    List<Entity> findByEntityName(String entityName);

    List<Entity> findAllByIdIn(List<Long> ids);

    Entity findByContactPersonEmail(String emailId);

    List<Entity> findAllByEntityType(String entityType);

    List<Entity> findAllByIsDeletedAndEntityType(Boolean isDeleted, String entityType);

    Entity findByIdAndEntityType(Long id, String entityType);

    Entity findByContactPersonEmailAndEntityType(String emailId, String entityType);

    @Query("SELECT e FROM Entity e " +
            "WHERE e.contactPersonEmail=:emailId and e.entityType=:entityType and (e.isDeleted=:isDeleted or e.isDeleted is null)")
    Entity findByContactPersonEmailAndEntityTypeAndIsDeleted(@Param("emailId") String emailId, @Param("entityType") String entityType, @Param("isDeleted") Boolean isDeleted);

    List<Entity> findAllByIsDeletedAndEntityTypeAndStatus(Boolean isDeleted, String entityType, String status);
    @Query("SELECT new com.solar.api.tenant.mapper.contract.EntityResponseDTO(e.id as id,e.entityName,e.entityType,e.status,e.isDocAttached," +
            "e.isDeleted,e.companyName,e.contactPersonEmail,e.contactPersonPhone,e.website,e.isActive,ed.uri as imageUri) " +
            "FROM Entity e " +
            "left join  EntityDetail ed on  ed.entity.id = e.id " +
            "WHERE (e.isDeleted=false or e.isDeleted is null) and e.entityType=:entityType and e.status=:status")
    List<EntityResponseDTO> findAllByEntityTypeAndStatus(String entityType, String status);

    @Query("SELECT count(cd.customerType) FROM Entity e, CustomerDetail cd " +
            "WHERE e.entityType='Customer' and e.id=cd.entityId and cd.customerType=:type")
    int countByCustomerType(String type);

    @Query("Select e.id from Entity e where e.isDeleted is null or e.isDeleted=:isDeleted and e.entityType=:entityType and e.organization.id in(:orgIds)")
    List<Long> findAllIdsByOrgId(@Param("orgIds") List<Long> orgIds, String entityType,Boolean isDeleted);

    @Query("Select e from Entity e where e.entityType = :entityType and e.contactPersonEmail = :emailId")
    List<Entity> findByContactPersonEmailAndEntityTypeIn(@Param("emailId")String emailId, @Param("entityType") String entityType);

}
