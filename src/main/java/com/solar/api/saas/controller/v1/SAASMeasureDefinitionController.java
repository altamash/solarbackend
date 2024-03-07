package com.solar.api.saas.controller.v1;

import com.solar.api.saas.mapper.extended.measure.MeasureDefinitionSAASDTO;
import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.saas.service.extended.measureDefinition.MeasureDefinitionSAASService;
import com.solar.api.tenant.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.solar.api.saas.mapper.extended.measure.MeasureDefinitionSAASMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASMeasureDefinitionController")
@RequestMapping(value = "/saas/measureDefinition")
public class SAASMeasureDefinitionController {

    private final MeasureDefinitionSAASService service;
    private final PortalAttributeSAASService portalAttributeSAASService;

    SAASMeasureDefinitionController(MeasureDefinitionSAASService service, PortalAttributeSAASService portalAttributeSAASService) {
        this.service = service;
        this.portalAttributeSAASService = portalAttributeSAASService;
    }

    @PostMapping()
    public MeasureDefinitionSAASDTO add(@RequestBody MeasureDefinitionSAASDTO billingDefinitionDTO) {
        return toMeasureDefinitionDTO(service.save(getBillingDefinition(billingDefinitionDTO)));
    }

    @PutMapping()
    public MeasureDefinitionSAASDTO update(@RequestBody MeasureDefinitionSAASDTO billingDefinitionDTO) {
        return toMeasureDefinitionDTO(service.save(getBillingDefinition(billingDefinitionDTO)));
    }

    private MeasureDefinitionSAAS getBillingDefinition(MeasureDefinitionSAASDTO billingDefinitionDTO) {
        MeasureDefinitionSAAS billingDefinition = toMeasureDefinition(billingDefinitionDTO);
        return billingDefinition;
    }

    @GetMapping("/{id}")
    public MeasureDefinitionSAASDTO findById(@PathVariable Long id) {
        return toMeasureDefinitionDTO(service.findById(id));
    }

    @GetMapping("")
    public List<MeasureDefinitionSAASDTO> findAll() {
        return toMeasureDefinitionDTOs(service.findAll());
    }

    @GetMapping("/code/{code}")
    MeasureDefinitionSAASDTO findByCode(@PathVariable String code) {
        return toMeasureDefinitionDTO(service.findMeasureDefinitionByCode(code));
    }

    @GetMapping("/codes/{codesCSV}")
    List<MeasureDefinitionSAASDTO> findByCodes(@PathVariable String codesCSV) {
        Set<String> ids = Arrays.stream(codesCSV.split(",")).map(id -> id.trim()).collect(Collectors.toSet());
        return toMeasureDefinitionDTOs(service.findByCodes(ids));
    }

    @GetMapping("/regModule/{regModuleId}")
    List<MeasureDefinitionSAASDTO> findByRegModuleId(@PathVariable Long regModuleId) {
        return toMeasureDefinitionDTOs(service.findByRegModuleId(regModuleId));
    }

    @GetMapping("/checkMeasureLinkWithRegister/{measureCodeId}")
    public ResponseEntity<?> checkMeasureLinkWithRegister(@PathVariable Long measureCodeId) {
        BaseResponse<Object> baseResponse= new BaseResponse(HttpStatus.OK.value(), service.checkMeasureLinkWithRegister(measureCodeId));
        return ResponseEntity.ok(baseResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity deleteAll() {
        service.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
