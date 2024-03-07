package com.solar.api.tenant.service.extended;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeSAASMapper;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeValueSAASDTO;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.tenant.mapper.extended.FunctionalRolesMapper;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.repository.FunctionalRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionalRolesServiceImpl implements FunctionalRolesService {

    @Autowired
    private FunctionalRolesRepository functionalRolesRepository;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;

    @Override
    public FunctionalRoles saveOrUpdate(FunctionalRoles functionalRoles) {
        functionalRoles.setHierarchyType("project");
        if (functionalRoles.getId() != null && functionalRoles.getId() != 0) {
            FunctionalRoles functionalRolesDb = findFunctionalRolesById(functionalRoles.getId());
            functionalRolesDb = FunctionalRolesMapper.toUpdatedFunctionalRoles(functionalRolesDb, functionalRoles);
            return functionalRolesRepository.save(functionalRolesDb);
        }
//        addFunctionRolesInPortalAttribute(functionalRoles);
        return functionalRolesRepository.save(functionalRoles);
    }

    private void addFunctionRolesInPortalAttribute(FunctionalRoles functionalRoles) {
        PortalAttributeSAAS portalAttributeSAAS = portalAttributeSAASService.findById(75L);
        portalAttributeSAASService.saveOrUpdatePortalAttributeValue(
                PortalAttributeSAASMapper.toPortalAttributeValue(
                        PortalAttributeValueSAASDTO.builder()
                        .attributeValue(functionalRoles.getName())
                        .description(functionalRoles.getName())
                        .attributeId(portalAttributeSAAS.getId())
                        .build()), portalAttributeSAAS.getName());
    }

    @Override
    public FunctionalRoles findFunctionalRolesById(Long id) {
        return functionalRolesRepository.findById(id).orElseThrow(() -> new NotFoundException(FunctionalRoles.class, id));
    }

    @Override
    public List<FunctionalRoles> findAllFunctionalRoles() {
        return functionalRolesRepository.findAll();
    }

}
