package com.solar.api.tenant.service.override.portalAttribute;

import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;

import java.util.List;

interface PortalAttributeTenantGetterService {
    // PortalAttribute ////////////////////////////////////////////////////////////////////////////////
    PortalAttributeTenant findById(Long id);

    PortalAttributeTenant findByIdFetchPortalAttributeValues(Long id);

    PortalAttributeTenant findByName(String name);

    PortalAttributeTenant findByNameFetchPortalAttributeValues(String name);

    List<PortalAttributeTenant> findAll();

    List<PortalAttributeTenant> findAllFetchPortalAttributeValues();

    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    PortalAttributeValueTenant findPortalAttributeValueById(Long id);

    PortalAttributeValueTenant findByAttributeAndSequenceNumber(String attributeName, Integer sequenceNumber);

    List<PortalAttributeValueTenant> findByPortalAttributeName(String attributeName);

    List<PortalAttributeValueTenant> findByPortalAttributeId(Long portalAttributeId);

    PortalAttributeValueTenant findByAttributeAndDescription(PortalAttributeTenant attribute, String description);

    List<PortalAttributeValueTenant> findAllPortalAttributeValues();

    PortalAttributeValueTenant findByAttributeValue(String value);

}
