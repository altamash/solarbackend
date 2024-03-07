package com.solar.api.tenant.service.override.portalAttribute;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;
import com.solar.api.tenant.repository.PortalAttributeTenantRepository;
import com.solar.api.tenant.repository.PortalAttributeValueTenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("masterTransactionManager")
class PortalAttributeTenantGetterServiceImpl implements PortalAttributeTenantGetterService {

    @Autowired
    private PortalAttributeTenantRepository attributeRepository;
    @Autowired
    private PortalAttributeValueTenantRepository attributeValueRepository;

    @Override
    public PortalAttributeTenant findById(Long id) {
        return attributeRepository.findById(id).orElse(null);
    }

    @Override
    public PortalAttributeTenant findByIdFetchPortalAttributeValues(Long id) {
        return attributeRepository.findByIdFetchPortalAttributeValues(id);
    }

    @Override
    public PortalAttributeTenant findByName(String name) {
        return attributeRepository.findByName(name).orElseThrow(() -> new NotFoundException(PortalAttributeTenant.class,
                "name", name));
    }

    @Override
    public PortalAttributeTenant findByNameFetchPortalAttributeValues(String name) {
        return attributeRepository.findByNameFetchPortalAttributeValues(name).orElse(null);
    }

    @Override
    public List<PortalAttributeTenant> findAll() {
        return attributeRepository.findAll();
    }

    @Override
    public List<PortalAttributeTenant> findAllFetchPortalAttributeValues() {
        return attributeRepository.findAllFetchPortalAttributeValues();
    }

    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    @Override
    public PortalAttributeValueTenant findPortalAttributeValueById(Long id) {
        return attributeValueRepository.findById(id).orElse(null);
    }

    @Override
    public PortalAttributeValueTenant findByAttributeAndSequenceNumber(String attributeName, Integer sequenceNumber) {
        PortalAttributeTenant attribute = getByName(attributeName);
        return attributeValueRepository.findByAttributeAndSequenceNumber(attribute, sequenceNumber);
    }

    @Override
    public List<PortalAttributeValueTenant> findByPortalAttributeName(String attributeName) {
        PortalAttributeTenant attribute = getByName(attributeName);
        return attributeValueRepository.findByAttribute(attribute);
    }

    private PortalAttributeTenant getByName(String attributeName) {
        return attributeRepository.findByName(attributeName).orElse(null);
    }

    @Override
    public List<PortalAttributeValueTenant> findByPortalAttributeId(Long portalAttributeId) {
        PortalAttributeTenant attribute = attributeRepository.findById(portalAttributeId).orElse(null);
        return attributeValueRepository.findByAttribute(attribute);
    }

    @Override
    public PortalAttributeValueTenant findByAttributeAndDescription(PortalAttributeTenant attribute, String description) {
        return attributeValueRepository.findByAttributeAndDescription(attribute, description);
    }

    @Override
//    @Cacheable("appCache")
    public List<PortalAttributeValueTenant> findAllPortalAttributeValues() {
        return attributeValueRepository.findAll();
    }

    @Override
    public PortalAttributeValueTenant findByAttributeValue(String value) {
        return attributeValueRepository.findByAttributeValue(value);
    }
}
