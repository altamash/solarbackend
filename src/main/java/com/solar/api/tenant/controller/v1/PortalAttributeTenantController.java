package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeSAASMapper;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantData;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantMapper;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;
import com.solar.api.tenant.service.PortalAttributeTenantService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantMapper.*;

@CrossOrigin
@RestController("PortalAttributeTenantController")
public class PortalAttributeTenantController {

    @Autowired
    private PortalAttributeTenantService portalAttributeTenantService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;

    @Autowired
    private MasterTenantRepository masterTenantRepository;

    @PreAuthorize("checkAccess()")
    @PostMapping("/portalAttribute")
    public PortalAttributeTenantDTO addPortalAttribute(@RequestBody PortalAttributeTenantData portalAttributeTenantData,
                                                       @RequestParam(required = false) Boolean addInTenant) {
        /*Long portalAttributeId = portalAttributeTenantData.getPortalAttribute().getId();
        if (portalAttributeId != null && portalAttributeId <= AppConstants.SAAS_RESERVED_AUTO_INCREMENT) {
            throw new BadRequestException("Tenant schema portal_attribute id must be greater than " + AppConstants
            .SAAS_RESERVED_AUTO_INCREMENT);
        }
        portalAttributeTenantData.getPortalAttributeValues().stream()
                .filter(value -> value.getId() != null && value.getId() <= AppConstants.SAAS_RESERVED_AUTO_INCREMENT)
                .findAny().ifPresent(pa -> {
                    throw new BadRequestException("Tenant schema portal_attribute_value id must be greater than " +
                    AppConstants.SAAS_RESERVED_AUTO_INCREMENT);
                });*/
        return addOrUpdate(portalAttributeTenantData, addInTenant);
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/portalAttributes")
    public List<PortalAttributeTenantDTO> addPortalAttributes(@RequestBody List<PortalAttributeTenantDTO> portalAttributes) {
        return PortalAttributeTenantMapper.toPortalAttributeDTOs(portalAttributeTenantService.save(PortalAttributeTenantMapper.toPortalAttributes(portalAttributes)));
    }

    @PreAuthorize("checkAccess()")
    @PutMapping("/portalAttribute")
    public PortalAttributeTenantDTO updatePortalAttribute(@RequestBody PortalAttributeTenantData portalAttributeTenantData,
                                                          @RequestParam(required = false) Boolean addInTenant) {
        return addOrUpdate(portalAttributeTenantData, addInTenant);
    }

    private PortalAttributeTenantDTO addOrUpdate(PortalAttributeTenantData portalAttributeData, Boolean addInTenant) {
        //currently we are handling all saas from backend so setting it true for tenant  its done to just run tenant for now
        // 4/04/2022
           addInTenant = true;
        if (addInTenant != null && addInTenant.booleanValue() == true) {
            PortalAttributeTenant portalAttributeTenant = toPortalAttribute(portalAttributeData.getPortalAttribute());
            List<PortalAttributeValueTenant> portalAttributeValueTenants =
                    toPortalAttributeValues(portalAttributeData.getPortalAttributeValues());
            portalAttributeTenant = portalAttributeTenantService.saveOrUpdate(portalAttributeTenant,
                    portalAttributeValueTenants);
            portalAttributeTenant.setPortalAttributeValuesTenant(portalAttributeValueTenants);
            return toPortalAttributeDTO(portalAttributeTenant);
        } else {
            PortalAttributeSAAS portalAttributeSAAS =
                    PortalAttributeSAASMapper.toPortalAttribute(portalAttributeData.getPortalAttribute());
            List<PortalAttributeValueSAAS> portalAttributeValueSAAS =
                    PortalAttributeSAASMapper.toPortalAttributeValues(PortalAttributeSAASMapper.toPortalAttributeValueDTOsFromTenant(portalAttributeData.getPortalAttributeValues()));
            portalAttributeSAAS = portalAttributeSAASService.saveOrUpdate(portalAttributeSAAS, portalAttributeValueSAAS);
            portalAttributeSAAS.setPortalAttributeValuesSAAS(portalAttributeValueSAAS);
            return toPortalAttributeDTO(portalAttributeSAAS);
        }
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttribute/{id}")
    public PortalAttributeTenantDTO findById(@PathVariable("id") Long id) {
        return attributeOverrideService.findByIdFetchPortalAttributeValues(id);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttribute/name/{name}")
    public PortalAttributeTenantDTO findByName(@PathVariable("name") String name) {
        return attributeOverrideService.findByNameFetchPortalAttributeValues(name);
    }

    /**
     * Combination of saas and tenant schema portal attributes
     *
     * @return
     */
    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttribute")
    public List<PortalAttributeTenantDTO> findAll() {
        return attributeOverrideService.findAllFetchPortalAttributeValues();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttribute/levelOne")
    public List<PortalAttributeTenantDTO> findAllLevelOne() {
        return attributeOverrideService.findAllLevelOne();
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/portalAttribute/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        portalAttributeTenantService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/portalAttribute")
    public ResponseEntity deleteAll() {
        portalAttributeTenantService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    @PostMapping("/portalAttributeValue/{attributeName}")
    public PortalAttributeValueTenantDTO addPortalAttributeValue(@RequestBody PortalAttributeValueTenantDTO portalAttributeValueTenantDTO, @PathVariable("attributeName") String attributeName) {
        return toPortalAttributeValueDTO(portalAttributeTenantService.saveOrUpdatePortalAttributeValue(toPortalAttributeValue(portalAttributeValueTenantDTO), attributeName));
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/portalAttributeValues")
    public List<PortalAttributeValueTenantDTO> addPortalAttributevalues(@RequestBody List<PortalAttributeValueTenantDTO> portalAttributeValues) {
        return PortalAttributeTenantMapper.toPortalAttributeValueDTOs(portalAttributeTenantService.savePortalAttributeValues(PortalAttributeTenantMapper.toPortalAttributeValues(portalAttributeValues)));
    }

    @PreAuthorize("checkAccess()")
    @PutMapping("/portalAttributeValue/{attributeName}")
    public PortalAttributeValueTenantDTO updatePortalAttributeValue(@RequestBody PortalAttributeValueTenantDTO portalAttributeValueTenantDTO, @PathVariable("attributeName") String attributeName) {
        return toPortalAttributeValueDTO(portalAttributeTenantService.saveOrUpdatePortalAttributeValue(toPortalAttributeValue(portalAttributeValueTenantDTO), attributeName));
    }
    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttributeValue/{id}")
    public PortalAttributeValueTenantDTO findPortalAttributeValueById(@PathVariable("id") Long id) {
        return attributeOverrideService.findPortalAttributeValueById(id);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttributeValue/attribute/{attributeName}")
    public List<PortalAttributeValueTenantDTO> findAllPortalAttributeValuesByAttributeName(@PathVariable(
            "attributeName") String attributeName) {
        return attributeOverrideService.findByPortalAttributeName(attributeName);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttributeValue/attributeById/{attributeId}")
    public List<PortalAttributeValueTenantDTO> findAllPortalAttributeValuesByAttributeId(@PathVariable("attributeId") Long portalAttributeId) {
        return attributeOverrideService.findByPortalAttributeId(portalAttributeId);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/portalAttributeValues")
    public List<PortalAttributeValueTenantDTO> findAllPortalAttributeValues() {
        return attributeOverrideService.findAllPortalAttributeValues();
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/portalAttributeValue/{id}")
    public ResponseEntity deletePortalAttributeValue(@PathVariable("id") Long id) {
        portalAttributeTenantService.deletePortalAttribute(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @DeleteMapping("/portalAttributeValue")
    public ResponseEntity deleteAllPortalAttributeValues() {
        portalAttributeTenantService.deleteAllPortalAttributeValues();
        return new ResponseEntity(HttpStatus.OK);
    }

    // this un-authorized api fetches all the portal attribute values for a given portal attribute id (used for register interest)
    @GetMapping("/portalAttributeValue/portalAttributeById/{attributeId}")
    public List<PortalAttributeValueTenantDTO> findAllPortalAttributeValuesByPortalAttributeById(
            @RequestHeader ("Comp-Key") Long compKey,
            @PathVariable("attributeId") Long portalAttributeId) {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        return attributeOverrideService.findByPortalAttributeId(portalAttributeId);
    }
}
