package com.solar.api.saas.service;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeSAASMapper;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.saas.repository.PortalAttributeRepository;
import com.solar.api.saas.repository.PortalAttributeValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("masterTransactionManager")
public class PortalAttributeSAASServiceImpl implements PortalAttributeSAASService {

    @Autowired
    private PortalAttributeRepository attributeRepository;
    @Autowired
    private PortalAttributeValueRepository attributeValueRepository;

    @Override
    public PortalAttributeSAAS saveOrUpdate(PortalAttributeSAAS portalAttributeSAAS,
                                            List<PortalAttributeValueSAAS> portalAttributeValueSAAS) {
        if (portalAttributeSAAS.getId() != null) {
            PortalAttributeSAAS finalPortalAttributeSAAS = portalAttributeSAAS;
            PortalAttributeSAAS portalAttributeSAASData = attributeRepository.findById(portalAttributeSAAS.getId())
                    .orElseThrow(() -> new NotFoundException(PortalAttributeSAAS.class, finalPortalAttributeSAAS.getId()));
            portalAttributeSAASData = PortalAttributeSAASMapper.toUpdatedPortalAttribute(portalAttributeSAASData,
                    portalAttributeSAAS);
            return attributeRepository.save(portalAttributeSAASData);
        }
        if (attributeRepository.findByName(portalAttributeSAAS.getName()).isPresent()) {
            throw new AlreadyExistsException(PortalAttributeSAAS.class, "name", portalAttributeSAAS.getName());
        }
        portalAttributeSAAS = attributeRepository.save(portalAttributeSAAS);
        if (portalAttributeValueSAAS != null) {
            PortalAttributeSAAS finalPortalAttributeSAAS = portalAttributeSAAS;
            portalAttributeValueSAAS.forEach(pa -> pa.setAttribute(finalPortalAttributeSAAS));
            attributeValueRepository.saveAll(portalAttributeValueSAAS);
        }
        return portalAttributeSAAS;
    }

    @Override
    public List<PortalAttributeSAAS> save(List<PortalAttributeSAAS> portalAttributeSAAS) {
        return attributeRepository.saveAll(portalAttributeSAAS);
    }

    @Override
    public PortalAttributeSAAS findById(Long id) {
        return attributeRepository.findById(id).orElseThrow(() -> new NotFoundException(PortalAttributeSAAS.class, id));
    }

    @Override
    public PortalAttributeSAAS findByIdFetchPortalAttributeValues(Long id) {
        return attributeRepository.findByIdFetchPortalAttributeValues(id);
    }

    @Override
    public PortalAttributeSAAS findByName(String name) {
        return attributeRepository.findByName(name).orElseThrow(() -> new NotFoundException(PortalAttributeSAAS.class,
                "name", name));
    }

    @Override
    public PortalAttributeSAAS findByNameFetchPortalAttributeValues(String name) {
        return attributeRepository.findByNameFetchPortalAttributeValues(name).orElse(null);
    }

    @Override
    public List<PortalAttributeSAAS> findAll() {
        return attributeRepository.findAll();
    }

    @Override
    public List<PortalAttributeSAAS> findAllFetchPortalAttributeValues() {
        return attributeRepository.findAllFetchPortalAttributeValues();
    }

    @Override
    public List<PortalAttributeSAAS> findAllFetchPortalAttributeValuesIdsNotIn(List<Long> ids) {
        return attributeRepository.findAllFetchPortalAttributeValuesIdsNotIn(ids);
    }

    @Override
    public void delete(Long id) {
        attributeRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        attributeRepository.deleteAll();
    }

    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    @Override
    public PortalAttributeValueSAAS saveOrUpdatePortalAttributeValue(PortalAttributeValueSAAS portalAttributeValueSAAS,
                                                                     String attributeName) {
        PortalAttributeSAAS attribute = attributeRepository.findByName(
                attributeName).orElseThrow(() -> new NotFoundException(PortalAttributeSAAS.class, "name", attributeName));
        if (portalAttributeValueSAAS.getId() != null) {
            PortalAttributeValueSAAS portalAttributeValueSAASData = findPortalAttributeValueById(portalAttributeValueSAAS.getId());
            if (portalAttributeValueSAASData == null) {
                throw new NotFoundException(PortalAttributeValueSAAS.class, portalAttributeValueSAASData.getId());
            }
            portalAttributeValueSAASData = PortalAttributeSAASMapper.toUpdatedPortalAttributeValue(portalAttributeValueSAASData,
                    portalAttributeValueSAAS);
            return attributeValueRepository.save(portalAttributeValueSAASData);
        }
        portalAttributeValueSAAS.setAttribute(attribute);
        return attributeValueRepository.save(portalAttributeValueSAAS);
    }

    @Override
    public List<PortalAttributeValueSAAS> savePortalAttributeValues(List<PortalAttributeValueSAAS> portalAttributeValueSAAS) {
        portalAttributeValueSAAS.forEach(portalAttributeValue -> {
            PortalAttributeSAAS attribute = attributeRepository.findByName(
                    portalAttributeValue.getAttributeName()).orElseThrow(() -> new NotFoundException(
                    PortalAttributeSAAS.class, "name", portalAttributeValue.getAttributeName()));
            portalAttributeValue.setAttribute(attribute);
        });
        return attributeValueRepository.saveAll(portalAttributeValueSAAS);
    }

    @Override
    public PortalAttributeValueSAAS findPortalAttributeValueById(Long id) {
        return attributeValueRepository.findById(id).orElse(null);
    }

    @Override
    public PortalAttributeValueSAAS findByAttributeAndSequenceNumber(String attributeName, Integer sequenceNumber) {
        PortalAttributeSAAS attribute = getByName(attributeName);
        return attributeValueRepository.findByAttributeAndSequenceNumber(attribute, sequenceNumber);
    }

    @Override
    public List<PortalAttributeValueSAAS> findByPortalAttributeName(String attributeName) {
        PortalAttributeSAAS attribute = getByName(attributeName);
        return attributeValueRepository.findByAttribute(attribute);
    }

    @Override
    public List<PortalAttributeValueSAAS> findByPortalAttributeId(Long portalAttributeId) {
        PortalAttributeSAAS attribute = attributeRepository.findById(portalAttributeId).orElse(null);
        return attributeValueRepository.findByAttribute(attribute);
    }

    @Override
    public PortalAttributeValueSAAS findByAttributeAndDescription(PortalAttributeSAAS attribute, String description) {
        return attributeValueRepository.findByAttributeAndDescription(attribute, description);
    }

    public List<PortalAttributeValueSAAS> findAllPortalAttributeValues() {
        return attributeValueRepository.findAll();
    }

    @Override
    public PortalAttributeValueSAAS findByAttributeValue(String value) {
        return attributeValueRepository.findByAttributeValue(value);
    }

    @Override
    public List<PortalAttributeValueSAAS> findAllByParentReferenceValue(String value) {
        return attributeValueRepository.findAllByParentReferenceValue(value);
    }

    @Override
    public void deletePortalAttribute(Long id) {
        attributeValueRepository.deleteById(id);
    }

    @Override
    public void deleteAllPortalAttributeValues() {
        attributeValueRepository.deleteAll();
    }

    private PortalAttributeSAAS getByName(String attributeName) {
        return attributeRepository.findByName(attributeName).orElse(null);
    }
}
