package com.solar.api.saas.repository;

import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PortalAttributeRepository extends JpaRepository<PortalAttributeSAAS, Long> {
    Optional<PortalAttributeSAAS> findByName(String name);

    @Query("select pa from PortalAttributeSAAS pa LEFT JOIN FETCH pa.portalAttributeValuesSAAS where pa.name = :name")
    Optional<PortalAttributeSAAS> findByNameFetchPortalAttributeValues(String name);

    @Query("select DISTINCT pa from PortalAttributeSAAS pa LEFT JOIN FETCH pa.portalAttributeValuesSAAS")
    List<PortalAttributeSAAS> findAllFetchPortalAttributeValues();

    @Query("select DISTINCT pa from PortalAttributeSAAS pa LEFT JOIN FETCH pa.portalAttributeValuesSAAS where pa.id not in :ids")
    List<PortalAttributeSAAS> findAllFetchPortalAttributeValuesIdsNotIn(List<Long> ids);

    @Query("select pa from PortalAttributeSAAS pa LEFT JOIN FETCH pa.portalAttributeValuesSAAS where pa.id = :id")
    PortalAttributeSAAS findByIdFetchPortalAttributeValues(Long id);
}
