package com.solar.api.tenant.service;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeSAASMapper;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.saas.repository.PortalAttributeRepository;
import com.solar.api.saas.repository.PortalAttributeValueRepository;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantMapper;
import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;
import com.solar.api.tenant.repository.PortalAttributeTenantRepository;
import com.solar.api.tenant.repository.PortalAttributeValueTenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
//@Transactional("masterTransactionManager")
public class PortalAttributeTenantServiceImpl implements PortalAttributeTenantService {

    @Autowired
    private PortalAttributeTenantRepository attributeRepository;
    @Autowired
    private PortalAttributeValueTenantRepository attributeValueRepository;
    @Autowired
    private PortalAttributeRepository attributeSAASRepository;
    @Autowired
    private PortalAttributeValueRepository attributeValueSAASRepository;

    static final Logger logger = LoggerFactory.getLogger(PortalAttributeTenantServiceImpl.class);

    @Override
    public PortalAttributeTenant saveOrUpdate(PortalAttributeTenant portalAttributeTenant,
                                              List<PortalAttributeValueTenant> portalAttributeValueTenants) {

        if (portalAttributeTenant.getId() != null) {
            Optional<PortalAttributeTenant> existingAttribute =
                    attributeRepository.findByName(portalAttributeTenant.getName());
            if (existingAttribute.isPresent() && existingAttribute.get().getId() != portalAttributeTenant.getId()) {
                throw new AlreadyExistsException(PortalAttributeTenant.class, "name", portalAttributeTenant.getName());
            }
            PortalAttributeTenant finalPortalAttributeTenant1 = portalAttributeTenant;
            PortalAttributeTenant portalAttributeTenantData = attributeRepository.findById(portalAttributeTenant.getId())
                    .orElseThrow(() -> new NotFoundException(PortalAttributeTenant.class, finalPortalAttributeTenant1.getId()));
            portalAttributeTenantData = PortalAttributeTenantMapper.toUpdatedPortalAttribute(portalAttributeTenantData, portalAttributeTenant);
            return attributeRepository.save(portalAttributeTenantData);
        }
        if (attributeRepository.findByName(portalAttributeTenant.getName()).isPresent()) {
            throw new AlreadyExistsException(PortalAttributeTenant.class, "name", portalAttributeTenant.getName());
        }

        if (portalAttributeTenant.getId() == null) {
            Long sequenceNumber = getLastIdentifier();
            portalAttributeTenant.setId(getLastIdentifier());
        }

        portalAttributeTenant = attributeRepository.save(portalAttributeTenant);


        if (portalAttributeValueTenants != null && portalAttributeValueTenants.size() > 0) {
            PortalAttributeTenant finalPortalAttributeTenant = portalAttributeTenant;
            portalAttributeValueTenants.forEach(pa -> pa.setAttribute(finalPortalAttributeTenant));
            attributeValueRepository.saveAll(portalAttributeValueTenants);
        }
        return portalAttributeTenant;
    }

    @Override
    public List<PortalAttributeTenant> save(List<PortalAttributeTenant> portalAttributeTenants) {
        return attributeRepository.saveAll(portalAttributeTenants);
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
    public PortalAttributeValueTenant saveOrUpdatePortalAttributeValue(PortalAttributeValueTenant portalAttributeValueTenant,
                                                                       String attributeName) {
        /*PortalAttributeTenant attribute = attributeRepository.findByName(
                attributeName).orElseThrow(() -> new NotFoundException(PortalAttributeTenant.class, "name", attributeName));
        if (portalAttributeValueTenant.getId() != null) {
            PortalAttributeValueTenant portalAttributeValueTenantData =
                    attributeValueRepository.getOne(portalAttributeValueTenant.getId());
            if (portalAttributeValueTenantData == null) {
                throw new NotFoundException(PortalAttributeValueTenant.class, portalAttributeValueTenantData.getId());
            }
            portalAttributeValueTenantData = PortalAttributeTenantMapper.toUpdatedPortalAttributeValue(portalAttributeValueTenantData,
                    portalAttributeValueTenant);
            return attributeValueRepository.save(portalAttributeValueTenantData);
        }
        portalAttributeValueTenant.setAttribute(attribute);
        return attributeValueRepository.save(portalAttributeValueTenant);*/
        PortalAttributeValueTenant portalAttributeValueTenantFinalData = null;
        Optional<PortalAttributeTenant> attributeTenant = attributeRepository.findByName(attributeName);
        if (attributeTenant.isPresent()) {
            List<PortalAttributeValueTenant> portalAttributeValueTenants = attributeValueRepository.findAllByParentReferenceValue(portalAttributeValueTenant.getParentReferenceValue());
            if (!portalAttributeValueTenants.isEmpty()) {
                Optional<PortalAttributeValueTenant> existingValue = portalAttributeValueTenants.stream().filter(val -> val.getAttributeValue().equalsIgnoreCase(portalAttributeValueTenant.getAttributeValue())).findFirst();
                //update value
                if (existingValue.isPresent()) {
                    PortalAttributeValueTenant portalAttributeValueTenantData = PortalAttributeTenantMapper.toUpdatedPortalAttributeValue(existingValue.get(),
                            portalAttributeValueTenant);
                    portalAttributeValueTenantFinalData = attributeValueRepository.save(portalAttributeValueTenantData);
                } else {
                    //inserted new value
                    portalAttributeValueTenant.setAttribute(attributeTenant.get());
                    portalAttributeValueTenantFinalData = attributeValueRepository.save(portalAttributeValueTenant);
                }
            } else {
                //inserted new value
                portalAttributeValueTenant.setAttribute(attributeTenant.get());
                portalAttributeValueTenantFinalData = attributeValueRepository.save(portalAttributeValueTenant);
            }

        } else {

            Optional<PortalAttributeSAAS> attributeSAAS = attributeSAASRepository.findByName(attributeName);
            PortalAttributeValueSAAS portalAttributeValueSAAS1 = null;
            if (attributeSAAS.isPresent()) {
                List<PortalAttributeValueSAAS> portalAttributeValueSAAS = attributeValueSAASRepository.findAllByParentReferenceValue(portalAttributeValueTenant.getParentReferenceValue());
                if (!portalAttributeValueSAAS.isEmpty()) {
                    Optional<PortalAttributeValueSAAS> existingValue = portalAttributeValueSAAS.stream().filter(val -> val.getAttributeValue().equalsIgnoreCase(portalAttributeValueTenant.getAttributeValue())).findFirst();
                    // update in saas value
                    if (existingValue.isPresent()) {
                        PortalAttributeValueSAAS portalAttributeValueSAASData = PortalAttributeSAASMapper.toUpdatedPortalAttributeValue(existingValue.get(),
                                PortalAttributeSAASMapper.FromTenantToPortalAttributeValueSAAS(portalAttributeValueTenant));
                        portalAttributeValueSAAS1 = attributeValueSAASRepository.save(portalAttributeValueSAASData);
                    } else {
                        //inserted new value
                        portalAttributeValueTenant.setAttribute(PortalAttributeTenantMapper.fromAttributeSAAStoPortalAttributeTenant(attributeSAAS.get()));
                        portalAttributeValueSAAS1 = attributeValueSAASRepository.save(PortalAttributeSAASMapper.FromTenantToPortalAttributeValueSAAS(portalAttributeValueTenant));
                    }
                } else {
                    portalAttributeValueTenant.setAttribute(PortalAttributeTenantMapper.fromAttributeSAAStoPortalAttributeTenant(attributeSAAS.get()));
                    portalAttributeValueSAAS1 = attributeValueSAASRepository.save(PortalAttributeSAASMapper.FromTenantToPortalAttributeValueSAAS(portalAttributeValueTenant));
                }

            }
            portalAttributeValueTenantFinalData = PortalAttributeTenantMapper.fromSAASToPortalAttributeValueTenant(portalAttributeValueSAAS1);
        }
        return portalAttributeValueTenantFinalData;
    }

    @Override
    public List<PortalAttributeValueTenant> savePortalAttributeValues(List<PortalAttributeValueTenant> portalAttributeValueTenants) {
        portalAttributeValueTenants.forEach(portalAttributeValue -> {
            PortalAttributeTenant attribute = attributeRepository.findByName(
                    portalAttributeValue.getAttributeName()).orElseThrow(() -> new NotFoundException(
                    PortalAttributeTenant.class, "name", portalAttributeValue.getAttributeName()));
            portalAttributeValue.setAttribute(attribute);
        });
        return attributeValueRepository.saveAll(portalAttributeValueTenants);
    }

    @Override
    public void deletePortalAttribute(Long id) {
        attributeValueRepository.deleteById(id);
    }

    @Override
    public void deleteAllPortalAttributeValues() {
        attributeValueRepository.deleteAll();
    }

    @Override
    public Long getLastIdentifier() {
        Long lastIdentifier = attributeRepository.getLastIdentifier();
        return ((lastIdentifier == null ? 0 : lastIdentifier) % 50000) + 50001;
    }
}
