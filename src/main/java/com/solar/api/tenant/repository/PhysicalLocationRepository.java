package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhysicalLocationRepository extends JpaRepository<PhysicalLocation, Long> {

    List<PhysicalLocation> findAllByLocationType(String locationType);
    List<PhysicalLocation> findAllByCategory(String locationCategory);

    List<PhysicalLocation> findByIdIn(List<Long> physicalLocationIds);

    List<PhysicalLocation> findAllByExternalRefId(Long siteId);

    List<PhysicalLocation> findAllByEntityIdIn(List<Long> entityIds);

    PhysicalLocation findByEntityId(Long entityId);

    @Query(value = "select l.* from ec1001.physical_locations l " +
            "left join ec1001.sites s on l.external_ref_id = s.id " +
            "left join ec1001.organization o on s.ref_id = o.id " +
            "where o.sub_type != :unitType " +
            "and l.location_type= :locationType " +
            "and o.id=:parentOrgId", nativeQuery = true)
    List<PhysicalLocation> findAllByLocationTypeAndExternalRefId(String locationType, String unitType, Long parentOrgId);

    @Query(value = "select l.* from physical_locations l " +
            " left join sites s on l.external_ref_id = s.id " +
            " left join organization o on s.ref_id = o.id " +
            " where o.sub_type != :unitType and l.location_type= :locationType", nativeQuery = true)
    List<PhysicalLocation> findAllByLocationTypeAndExternalRefId(String locationType, String unitType);

    @Query("SELECT CASE WHEN COUNT(L) > 0 THEN true ELSE false END " +
            "FROM PhysicalLocation L WHERE L.id = :locId AND L.externalRefId = :extRefId")
    boolean findSiteLocationAssociationExist(Long locId, Long extRefId);

    @Query("select new com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO(pl.id as id , pl.add1 as add1 , pl.add2 as add2, pl.zipCode as zipCode, pl.geoLat as geoLat, " +
            " pl.geoLong as geoLang, pl.ext1 as ext1, pl.ext2 as ext2, pl.contactPerson as contactPerson, pl.email as email )" +
            "from PhysicalLocation pl " +
            "where pl.externalRefId in (Select s.id from Site s where s.organization.id = :orgId) ")
    List<PhysicalLocationDTO> getAllLocationsDTOByExternalRefId(@Param("orgId") Long orgId);

    @Query("Select pl from PhysicalLocation pl where pl.organization.id = :orgId and pl.status=:status")
    List<PhysicalLocation> findAllByOrgIdAndStatus(@Param("orgId") Long orgId, @Param("status") String status);

    @Query("Select pl from PhysicalLocation pl where pl.isDeleted=:isDeleted order by  pl.status asc ")
    List<PhysicalLocation> findAllPhysicalLocationsByIsDeleted(@Param("isDeleted") Boolean isDeleted);

    @Query("Select pl from PhysicalLocation pl where pl.id in(:locIds) and pl.isDeleted=:isDeleted and pl.status=:status")
    List<PhysicalLocation> findByIdInAndStatusAndIsDeleted(@Param("locIds") List<Long> physicalLocationIds,
                                                           @Param("status") String status,
                                                           @Param("isDeleted") Boolean isDeleted);

    @Query(value = "select distinct ploc.zipCode"+
            " from UserLevelPrivilege prv" +
            " inner join LocationMapping locmap" +
            " on locmap.sourceId = prv.user.acctId" +
            " inner join PhysicalLocation ploc" +
            " on ploc.id = locmap.locationId" +
            " where prv.entity.entityType =:entityType and (prv.entity.isActive is null or prv.entity.isActive =:isActive) and (prv.entity.isDeleted is null" +
            " or prv.entity.isDeleted = :isDeleted) and (ploc.isDeleted is null or ploc.isDeleted = :locationDeleted) ")
    List<String> findZipCodesByEntityAndLocationStatus(@Param("entityType") String entityType,@Param("isActive") Boolean isActive,@Param("isDeleted") Boolean isDeleted,@Param("locationDeleted")  Boolean locationDeleted);

    @Query("select pl from PhysicalLocation pl " +
            "left join LocationMapping lm " +
            "on lm.locationId  = pl.id and lm.sourceType= 'ORGANIZATION' " +
            "left join Organization o on o.id = lm.sourceId " +
            "where o.id =:orgId" )
    List<PhysicalLocation>  findSubOrgLocations(@Param("orgId") Long orgId);
}
