package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PortalAttributeTenantRepository extends JpaRepository<PortalAttributeTenant, Long> {
    Optional<PortalAttributeTenant> findByName(String name);

    @Query("select pa from PortalAttributeTenant pa LEFT JOIN FETCH pa.portalAttributeValuesTenant where pa.name = :name")
    Optional<PortalAttributeTenant> findByNameFetchPortalAttributeValues(String name);

    @Query("select DISTINCT pa from PortalAttributeTenant pa LEFT JOIN FETCH pa.portalAttributeValuesTenant")
    List<PortalAttributeTenant> findAllFetchPortalAttributeValues();

    @Query("select pa from PortalAttributeTenant pa LEFT JOIN FETCH pa.portalAttributeValuesTenant where pa.id = :id")
    PortalAttributeTenant findByIdFetchPortalAttributeValues(Long id);

    @Query("SELECT MAX(p.id) FROM PortalAttributeTenant p")
    Long getLastIdentifier();
}
