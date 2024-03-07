package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.extended.physicalLocation.SiteDTO;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.extended.physicalLocation.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {
    @Query(value = "select new com.solar.api.tenant.mapper.extended.physicalLocation.SiteDTO(s.id as id,s.siteName as siteName,s.siteType as siteType,s.subType as subType," +
            "s.active as active, ploc )" +
            " FROM Site s" +
            " left join PhysicalLocation ploc on s.id = ploc.externalRefId" +
            " where s.siteType = :siteType ")
    List<SiteDTO> findAllBySiteType(String siteType);

    Site findBySiteName(String name);

    @Query(value = "SELECT  count(s.id)" +
            "FROM sites s " +
            "INNER JOIN physical_locations ploc on s.id = ploc.external_ref_id " +
            "WHERE s.ref_id = :ref_id", nativeQuery = true)
    Integer findSiteCountByRefId(Long ref_id);

    @Query(value = "SELECT * FROM sites s WHERE s.ref_id = :refid", nativeQuery = true)
    Site findByRefId(Long refid);

    @Query("Select s from Site s where s.isDeleted=:isDeleted")
    List<Site> findAllSitesByIsDeleted(@Param("isDeleted") Boolean isDeleted);

    List<Site> findSitesByCategoryAndOrganizationAndIsDeleted(String category, Organization org, Boolean isDeleted);
}
