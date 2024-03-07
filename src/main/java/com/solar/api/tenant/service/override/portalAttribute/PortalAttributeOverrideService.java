package com.solar.api.tenant.service.override.portalAttribute;

import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;

import java.util.List;

public interface PortalAttributeOverrideService {

    // PortalAttribute ////////////////////////////////////////////////////////////////////////////////
    PortalAttributeTenantDTO findByIdFetchPortalAttributeValues(Long id);

    PortalAttributeTenantDTO findByNameFetchPortalAttributeValues(String name);

    List<PortalAttributeTenantDTO> findAllFetchPortalAttributeValues();

    List<PortalAttributeTenantDTO> findAllLevelOne();
    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    PortalAttributeValueTenantDTO findPortalAttributeValueById(Long id);

    PortalAttributeValueTenantDTO findByAttributeAndSequenceNumber(String attributeName, Integer sequenceNumber);

    List<PortalAttributeValueTenantDTO> findByPortalAttributeName(String attributeName);

    List<PortalAttributeValueTenantDTO> findByPortalAttributeId(Long portalAttributeId);

    List<PortalAttributeValueTenantDTO> findAllPortalAttributeValues();

    PortalAttributeValueTenantDTO findByAttributeValue(String value);

    List<PortalAttributeValueTenantDTO> findByPortalAttrId(Long portalAttributeId);

    }
