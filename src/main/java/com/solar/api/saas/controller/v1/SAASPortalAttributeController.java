package com.solar.api.saas.controller.v1;

import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeData;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeSAASDTO;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeSAASMapper;
import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeValueSAASDTO;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.saas.service.PortalAttributeSAASService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeSAASMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASPortalAttributeController")
@RequestMapping(value = "/saas")
public class SAASPortalAttributeController {

    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;

    @PostMapping("/portalAttribute")
    public PortalAttributeSAASDTO addPortalAttribute(@RequestBody PortalAttributeData portalAttributeData) {
        return addOrUpdate(portalAttributeData);
    }

    @PostMapping("/portalAttributes")
    public List<PortalAttributeSAASDTO> addPortalAttributes(@RequestBody List<PortalAttributeSAASDTO> portalAttributes) {
        return PortalAttributeSAASMapper.toPortalAttributeDTOs(portalAttributeSAASService.save(PortalAttributeSAASMapper.toPortalAttributes(portalAttributes)));
    }

    @PutMapping("/portalAttribute")
    public PortalAttributeSAASDTO updatePortalAttribute(@RequestBody PortalAttributeData portalAttributeData) {
        return addOrUpdate(portalAttributeData);
    }

    private PortalAttributeSAASDTO addOrUpdate(PortalAttributeData portalAttributeData) {
        PortalAttributeSAAS portalAttributeSAAS = toPortalAttribute(portalAttributeData.getPortalAttribute());
        List<PortalAttributeValueSAAS> portalAttributeValueSAAS =
                toPortalAttributeValues(portalAttributeData.getPortalAttributeValues());
        portalAttributeSAAS = portalAttributeSAASService.saveOrUpdate(portalAttributeSAAS, portalAttributeValueSAAS);
        portalAttributeSAAS.setPortalAttributeValuesSAAS(portalAttributeValueSAAS);
        return toPortalAttributeDTO(portalAttributeSAAS);
    }

    @GetMapping("/portalAttribute/{id}")
    public PortalAttributeSAASDTO findById(@PathVariable("id") Long id) {
        return toPortalAttributeDTO(portalAttributeSAASService.findById(id));
    }

    @GetMapping("/portalAttribute/name/{name}")
    public PortalAttributeSAASDTO findByName(@PathVariable("name") String name) {
        PortalAttributeSAAS portalAttributeSAAS = portalAttributeSAASService.findByNameFetchPortalAttributeValues(name);
        PortalAttributeSAASDTO portalAttributeSAASDTO = toPortalAttributeDTO(portalAttributeSAAS);
        if (portalAttributeSAASDTO != null) {
            portalAttributeSAASDTO.setPortalAttributeValues(toPortalAttributeValueDTOs(portalAttributeSAAS.getPortalAttributeValuesSAAS()));
        }
        return portalAttributeSAASDTO;
    }

    @GetMapping("/portalAttribute")
    public List<PortalAttributeSAASDTO> findAll() {
        return toPortalAttributeDTOs(portalAttributeSAASService.findAllFetchPortalAttributeValues());
    }

    @DeleteMapping("/portalAttribute/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        portalAttributeSAASService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/portalAttribute")
    public ResponseEntity deleteAll() {
        portalAttributeSAASService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // PortalAttributeValue ////////////////////////////////////////////////////////////////////////////////
    @PostMapping("/portalAttributeValue/{attributeName}")
    public PortalAttributeValueSAASDTO addPortalAttributeValue(@RequestBody PortalAttributeValueSAASDTO portalAttributeValueSAASDTO, @PathVariable("attributeName") String attributeName) {
        return toPortalAttributeValueDTO(portalAttributeSAASService.saveOrUpdatePortalAttributeValue(toPortalAttributeValue(portalAttributeValueSAASDTO), attributeName));
    }

    @PostMapping("/portalAttributeValues")
    public List<PortalAttributeValueSAASDTO> addPortalAttributevalues(@RequestBody List<PortalAttributeValueSAASDTO> portalAttributeValues) {
        return PortalAttributeSAASMapper.toPortalAttributeValueDTOs(portalAttributeSAASService.savePortalAttributeValues(PortalAttributeSAASMapper.toPortalAttributeValues(portalAttributeValues)));
    }

    @PutMapping("/portalAttributeValue/{attributeName}")
    public PortalAttributeValueSAASDTO updatePortalAttributeValue(@RequestBody PortalAttributeValueSAASDTO portalAttributeValueSAASDTO, @PathVariable("attributeName") String attributeName) {
        return toPortalAttributeValueDTO(portalAttributeSAASService.saveOrUpdatePortalAttributeValue(toPortalAttributeValue(portalAttributeValueSAASDTO), attributeName));
    }

    @GetMapping("/portalAttributeValue/{id}")
    public PortalAttributeValueSAASDTO findPortalAttributeValueById(@PathVariable("id") Long id) {
        return toPortalAttributeValueDTO(portalAttributeSAASService.findPortalAttributeValueById(id));
    }

    @GetMapping("/portalAttributeValue/attribute/{attributeName}")
    public List<PortalAttributeValueSAASDTO> findAllPortalAttributeValuesByAttribute(@PathVariable("attributeName") String attributeName) {
        return toPortalAttributeValueDTOs(portalAttributeSAASService.findByPortalAttributeName(attributeName));
    }

    @GetMapping("/portalAttributeValue/attributeById/{attributeId}")
    public List<PortalAttributeValueSAASDTO> findAllPortalAttributeValuesByAttributeId(@PathVariable("attributeId") Long portalAttributeId) {
        return toPortalAttributeValueDTOs(portalAttributeSAASService.findByPortalAttributeId(portalAttributeId));
    }

    @GetMapping("/portalAttributeValues")
    public List<PortalAttributeValueSAASDTO> findAllPortalAttributeValues() {
        return toPortalAttributeValueDTOs(portalAttributeSAASService.findAllPortalAttributeValues());
    }

    @DeleteMapping("/portalAttributeValue/{id}")
    public ResponseEntity deletePortalAttributeValue(@PathVariable("id") Long id) {
        portalAttributeSAASService.deletePortalAttribute(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/portalAttributeValue")
    public ResponseEntity deleteAllPortalAttributeValues() {
        portalAttributeSAASService.deleteAllPortalAttributeValues();
        return new ResponseEntity(HttpStatus.OK);
    }
}
