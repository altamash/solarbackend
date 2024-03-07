package com.solar.api.tenant.service.override.portalAttribute;

import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.repository.PortalAttributeValueRepository;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;
import com.solar.api.tenant.repository.PortalAttributeTenantRepository;
import com.solar.api.tenant.repository.PortalAttributeValueTenantRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantMapper.*;

@Service
public class PortalAttributeOverrideServiceImpl implements PortalAttributeOverrideService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private PortalAttributeTenantGetterService portalAttributeTenantGetterService;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;
    @Autowired
    private PortalAttributeTenantRepository attributeRepository;
    @Autowired
    private PortalAttributeValueTenantRepository attributeValueRepository;
    @Autowired
    private PortalAttributeValueRepository attributeValueSAASRepository;

    // PortalAttribute ////////////////////////////////////////////////////////////////////////////////
    @Override
    public PortalAttributeTenantDTO findByIdFetchPortalAttributeValues(Long id) {
        PortalAttributeTenant portalAttributeTenant =
                portalAttributeTenantGetterService.findByIdFetchPortalAttributeValues(id);
        if (portalAttributeTenant != null) {
            return toPortalAttributeDTO(portalAttributeTenant);
        } else {
            return toPortalAttributeDTO(portalAttributeSAASService.findByIdFetchPortalAttributeValues(id));
        }
    }

    @Override
    public PortalAttributeTenantDTO findByNameFetchPortalAttributeValues(String name) {
        PortalAttributeTenant portalAttributeTenant =
                portalAttributeTenantGetterService.findByNameFetchPortalAttributeValues(name);
        if (portalAttributeTenant != null) {
            PortalAttributeTenantDTO portalAttributeTenantDTO = toPortalAttributeDTO(portalAttributeTenant);
            portalAttributeTenantDTO.setPortalAttributeValuesTenant(toPortalAttributeValueDTOs(portalAttributeTenant.getPortalAttributeValuesTenant()));
            return portalAttributeTenantDTO;
        } else {
            PortalAttributeSAAS portalAttributeSAAS =
                    portalAttributeSAASService.findByNameFetchPortalAttributeValues(name);
            PortalAttributeTenantDTO portalAttributeTenantDTO = toPortalAttributeDTO(portalAttributeSAAS);
            if (portalAttributeTenantDTO != null) {
                portalAttributeTenantDTO.setPortalAttributeValuesTenant(toPortalAttributeValueDTOsFromSAAS(portalAttributeSAAS.getPortalAttributeValuesSAAS()));
            }
            return portalAttributeTenantDTO;
        }
    }

    /**
     * Combination of saas and tenant schema portal attributes
     *
     * @return portalAttributeDTOs
     */
    @Override
    public List<PortalAttributeTenantDTO> findAllFetchPortalAttributeValues() {
        List<PortalAttributeTenant> portalAttributesTenant =
                portalAttributeTenantGetterService.findAllFetchPortalAttributeValues();
        List<PortalAttributeSAAS> portalAttributeSAAS;
        if (portalAttributesTenant.isEmpty()) {
            portalAttributeSAAS = portalAttributeSAASService.findAllFetchPortalAttributeValues();
        } else {
            portalAttributeSAAS = portalAttributeSAASService
                    .findAllFetchPortalAttributeValuesIdsNotIn(portalAttributesTenant.stream().map(pa -> pa.getId())
                            .collect(Collectors.toList()));
        }
        List<PortalAttributeTenantDTO> portalAttributeTenantDTOs = toPortalAttributeDTOs(portalAttributesTenant);
        List<PortalAttributeTenantDTO> portalAttributeSAASDTOs = toPortalAttributeDTOsFromSAAS(portalAttributeSAAS);
        portalAttributeSAASDTOs.addAll(portalAttributeTenantDTOs);
        portalAttributeSAASDTOs.sort(Comparator.comparing(PortalAttributeTenantDTO::getId));
        return portalAttributeSAASDTOs;
    }

    @Override
    public List<PortalAttributeTenantDTO> findAllLevelOne() {
        List<PortalAttributeTenantDTO> allPortalAttribute = findAllFetchPortalAttributeValues();
        List<PortalAttributeTenantDTO> portalAttributesLevelOne =
                allPortalAttribute.stream().filter(pa -> StringUtils.isEmpty(pa.getParent())).collect(Collectors.toList());
        for (PortalAttributeTenantDTO parent : portalAttributesLevelOne) {
            try {
                setChildren(parent, allPortalAttribute);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return portalAttributesLevelOne;
    }

    private void setChildren(PortalAttributeTenantDTO parent, List<PortalAttributeTenantDTO> allPortalAttribute) {
        List<PortalAttributeTenantDTO> children = findByParentId(allPortalAttribute, parent.getId());
        parent.setChildren(children);
        for (PortalAttributeTenantDTO child : children) {
            children = findByParentId(allPortalAttribute, child.getId());
            if (!children.isEmpty()) {
                setChildren(child, allPortalAttribute);
            }
        }
    }

    private List<PortalAttributeTenantDTO> findByParentId(List<PortalAttributeTenantDTO> allPortalAttribute, Long id) {
        return allPortalAttribute.stream().filter(pa -> !StringUtils.isEmpty(pa.getParent()) && Long.valueOf(pa.getParent()).equals(id)).collect(Collectors.toList());
    }

    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    @Override
    public PortalAttributeValueTenantDTO findPortalAttributeValueById(Long id) {
        PortalAttributeValueTenant portalAttributeValueTenant =
                portalAttributeTenantGetterService.findPortalAttributeValueById(id);
        if (portalAttributeValueTenant != null) {
            return toPortalAttributeValueDTO(portalAttributeValueTenant);
        } else {
            return toPortalAttributeValueDTO(portalAttributeSAASService.findPortalAttributeValueById(id));
        }
    }

    @Override
    public PortalAttributeValueTenantDTO findByAttributeAndSequenceNumber(String attributeName,
                                                                          Integer sequenceNumber) {
        PortalAttributeValueTenant portalAttributeValueTenant =
                portalAttributeTenantGetterService.findByAttributeAndSequenceNumber(attributeName, sequenceNumber);
        if (portalAttributeValueTenant != null) {
            return toPortalAttributeValueDTO(portalAttributeValueTenant);
        }
        return toPortalAttributeValueDTO(portalAttributeSAASService.findByAttributeAndSequenceNumber(attributeName,
                sequenceNumber));
    }

    @Override
    public List<PortalAttributeValueTenantDTO> findByPortalAttributeName(String attributeName) {
        List<PortalAttributeValueTenant> portalAttributeValueTenants =
                portalAttributeTenantGetterService.findByPortalAttributeName(attributeName);
        if (!portalAttributeValueTenants.isEmpty()) {
            return toPortalAttributeValueDTOs(portalAttributeValueTenants);
        } else {
            return toPortalAttributeValueDTOsFromSAAS(portalAttributeSAASService.findByPortalAttributeName(attributeName));
        }
    }

    @Override
    public List<PortalAttributeValueTenantDTO> findByPortalAttributeId(Long portalAttributeId) {
        List<PortalAttributeValueTenant> portalAttributeValueTenants =
                portalAttributeTenantGetterService.findByPortalAttributeId(portalAttributeId);
        if (!portalAttributeValueTenants.isEmpty()) {
            return toPortalAttributeValueDTOs(portalAttributeValueTenants);
        } else {
            return toPortalAttributeValueDTOsFromSAAS(portalAttributeSAASService.findByPortalAttributeId(portalAttributeId));
        }
    }

    @Override
    public List<PortalAttributeValueTenantDTO> findAllPortalAttributeValues() {
        List<PortalAttributeValueTenant> portalAttributeValueTenants =
                portalAttributeTenantGetterService.findAllPortalAttributeValues();
        if (!portalAttributeValueTenants.isEmpty()) {
            return toPortalAttributeValueDTOs(portalAttributeValueTenants);
        } else {
            return toPortalAttributeValueDTOsFromSAAS(portalAttributeSAASService.findAllPortalAttributeValues());
        }
    }

    @Override
    public PortalAttributeValueTenantDTO findByAttributeValue(String value) {
        PortalAttributeValueTenant portalAttributeValueTenant =
                portalAttributeTenantGetterService.findByAttributeValue(value);
        if (portalAttributeValueTenant != null) {
            return toPortalAttributeValueDTO(portalAttributeValueTenant);
        }
        return toPortalAttributeValueDTO(portalAttributeSAASService.findByAttributeValue(value));
    }

    @Override
    public List<PortalAttributeValueTenantDTO> findByPortalAttrId(Long portalAttributeId) {
            return toPortalAttributeValueDTOsFromSAAS(portalAttributeSAASService.findByPortalAttributeId(portalAttributeId));
    }
}
