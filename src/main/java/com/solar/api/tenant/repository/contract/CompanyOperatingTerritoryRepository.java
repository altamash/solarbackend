package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.mapper.contract.CompanyOperatingTerritoryDTO;
import com.solar.api.tenant.model.contract.CompanyOperatingTerritory;
import com.solar.api.tenant.model.contract.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyOperatingTerritoryRepository extends JpaRepository<CompanyOperatingTerritory, Long> {
    @Query("SELECT new com.solar.api.tenant.mapper.contract.CompanyOperatingTerritoryDTO(cot.id, cot.name, cot.description,cot.geoLat,cot.geoLong) FROM CompanyOperatingTerritory cot WHERE cot.organization.id = :orgId")
    List<CompanyOperatingTerritoryDTO> findByOrganizationId(@Param("orgId") Long orgId);
    List<CompanyOperatingTerritory> findAllByOrganization(Organization organization);
}
