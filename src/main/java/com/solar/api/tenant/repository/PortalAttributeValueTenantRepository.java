package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortalAttributeValueTenantRepository extends JpaRepository<PortalAttributeValueTenant, Long> {
    Optional<PortalAttributeValueTenant> findBySequenceNumber(Integer number);

    List<PortalAttributeValueTenant> findByAttribute(PortalAttributeTenant attribute);

    PortalAttributeValueTenant findByAttributeAndSequenceNumber(PortalAttributeTenant attribute, Integer sequenceNumber);

    PortalAttributeValueTenant findByAttributeValue(String value);

    PortalAttributeValueTenant findByAttributeAndDescription(PortalAttributeTenant attribute, String description);

    List<PortalAttributeValueTenant> findAllByParentReferenceValue(String value);

}
