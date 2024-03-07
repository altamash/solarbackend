package com.solar.api.saas.service;

import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;

import java.util.List;

public interface PortalAttributeSAASService {
    PortalAttributeSAAS saveOrUpdate(PortalAttributeSAAS portalAttributeSAAS, List<PortalAttributeValueSAAS> portalAttributeValueSAAS);

    List<PortalAttributeSAAS> save(List<PortalAttributeSAAS> portalAttributeSAAS);

    PortalAttributeSAAS findById(Long id);

    PortalAttributeSAAS findByIdFetchPortalAttributeValues(Long id);

    PortalAttributeSAAS findByName(String name);

    PortalAttributeSAAS findByNameFetchPortalAttributeValues(String name);

    List<PortalAttributeSAAS> findAll();

    List<PortalAttributeSAAS> findAllFetchPortalAttributeValues();

    List<PortalAttributeSAAS> findAllFetchPortalAttributeValuesIdsNotIn(List<Long> ids);

    void delete(Long id);

    void deleteAll();

    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    PortalAttributeValueSAAS saveOrUpdatePortalAttributeValue(PortalAttributeValueSAAS portalAttributeValueSAAS,
                                                              String attributeName);

    List<PortalAttributeValueSAAS> savePortalAttributeValues(List<PortalAttributeValueSAAS> portalAttributeValueSAAS);

    PortalAttributeValueSAAS findPortalAttributeValueById(Long id);

    PortalAttributeValueSAAS findByAttributeAndSequenceNumber(String attributeName, Integer sequenceNumber);

    List<PortalAttributeValueSAAS> findByPortalAttributeName(String attributeName);

    List<PortalAttributeValueSAAS> findByPortalAttributeId(Long portalAttributeId);

    PortalAttributeValueSAAS findByAttributeAndDescription(PortalAttributeSAAS attribute, String description);

    List<PortalAttributeValueSAAS> findAllPortalAttributeValues();

    PortalAttributeValueSAAS findByAttributeValue(String value);

    List<PortalAttributeValueSAAS> findAllByParentReferenceValue(String value);

    void deletePortalAttribute(Long id);

    void deleteAllPortalAttributeValues();
}
