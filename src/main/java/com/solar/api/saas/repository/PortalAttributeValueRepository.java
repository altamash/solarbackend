package com.solar.api.saas.repository;

import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortalAttributeValueRepository extends JpaRepository<PortalAttributeValueSAAS, Long> {
    Optional<PortalAttributeValueSAAS> findBySequenceNumber(Integer number);

    List<PortalAttributeValueSAAS> findByAttribute(PortalAttributeSAAS attribute);

    PortalAttributeValueSAAS findByAttributeAndSequenceNumber(PortalAttributeSAAS attribute, Integer sequenceNumber);

    PortalAttributeValueSAAS findByAttributeValue(String value);

    PortalAttributeValueSAAS findByAttributeAndDescription(PortalAttributeSAAS attribute, String description);

    List<PortalAttributeValueSAAS> findAllByParentReferenceValue(String value);
}
