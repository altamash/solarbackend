package com.solar.api.tenant.service;

import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;

import java.util.List;

public interface PortalAttributeTenantService {
    // PortalAttribute ////////////////////////////////////////////////////////////////////////////////
    PortalAttributeTenant saveOrUpdate(PortalAttributeTenant portalAttributeTenant,
                                       List<PortalAttributeValueTenant> portalAttributeValueTenants);

    List<PortalAttributeTenant> save(List<PortalAttributeTenant> portalAttributeTenants);

    void delete(Long id);

    void deleteAll();

    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    PortalAttributeValueTenant saveOrUpdatePortalAttributeValue(PortalAttributeValueTenant portalAttributeValueTenant,
                                                                String attributeName);

    List<PortalAttributeValueTenant> savePortalAttributeValues(List<PortalAttributeValueTenant> portalAttributeValueTenants);

    void deletePortalAttribute(Long id);

    void deleteAllPortalAttributeValues();

    Long getLastIdentifier();

}
