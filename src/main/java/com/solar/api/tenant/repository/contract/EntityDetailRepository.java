package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.mapper.contract.EntityDetailDTO;
import com.solar.api.tenant.mapper.tiles.entityDetail.EntityDetailTile;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntityDetailRepository extends JpaRepository<EntityDetail, Long> {
    @Query(value = "select * FROM entity_detail where entity_id=:entityId order by id desc limit 1",nativeQuery = true)
    EntityDetail findByEntityId(@Param("entityId")Long entityId);
    @Query(value = "select * FROM entity_detail where entity_id=:entityId order by id desc limit 1",
            nativeQuery = true)
    EntityDetail findCustomerByEntityId(@Param("entityId") Long entityId);

    List<EntityDetail> findAllByEntityIdIn(List<Long> entityIds);

    @Query(value = "select new com.solar.api.tenant.mapper.tiles.entityDetail.EntityDetailTile(ed.id, ed.entity.id, ed.uri, ed.fileName) " +
            "FROM EntityDetail ed where ed.entity.id = :entityId ")
    EntityDetailTile getImageByEntityId(@Param("entityId") Long entityId);

    @Query(value = "select new com.solar.api.tenant.mapper.tiles.entityDetail.EntityDetailTile(ed.id, ed.entity.id, ed.uri, ed.fileName, prv.user.acctId)  " +
                    " from UserLevelPrivilege prv " +
                    " inner join EntityDetail ed  on prv.entity.id = ed.entity.id " +
                    " where prv.user.acctId in(:acctIds) and prv.role is null")
    List<EntityDetailTile> findByAccountIds(@Param("acctIds") List<Long> acctIds);

    @Query(value = "select new com.solar.api.tenant.mapper.tiles.entityDetail.EntityDetailTile(ed.id, ed.entity.id, ed.uri, ed.fileName)  " +
            " from EntityDetail ed "+
            " where ed.id in(:entityDetailIds) ")
    List<EntityDetailTile> findProfilePictureByEntityDetailIds(@Param("entityDetailIds") List<Long> entityDetailIds);

}
