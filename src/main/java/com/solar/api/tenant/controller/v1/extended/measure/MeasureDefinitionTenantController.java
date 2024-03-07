package com.solar.api.tenant.controller.v1.extended.measure;

import com.solar.api.saas.mapper.extended.measure.MeasureDefinitionSAASMapper;
import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.saas.service.extended.measureDefinition.MeasureDefinitionSAASService;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;
import com.solar.api.tenant.service.extended.measure.MeasureDefinitionTenantService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantMapper.toMeasureDefinition;
import static com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantMapper.toMeasureDefinitionDTO;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("MeasureDefinitionTenantController")
@RequestMapping(value = "/measureDefinition")
public class MeasureDefinitionTenantController {

    private final MeasureDefinitionTenantService service;
    private final MeasureDefinitionOverrideService overrideService;
    @Autowired
    private MeasureDefinitionSAASService measureDefinitionSAASService;

    MeasureDefinitionTenantController(MeasureDefinitionTenantService service,
                                      MeasureDefinitionOverrideService overrideService) {
        this.service = service;
        this.overrideService = overrideService;
    }

    @PostMapping
//    @PreAuthorize("hasPermission('Measure API', 'Write')")
    public MeasureDefinitionTenantDTO add(@RequestBody MeasureDefinitionTenantDTO billingDefinitionDTO,
                                          @RequestParam(required = false) Boolean addInTenant) {
        /*if (billingDefinitionDTO.getId() != null && billingDefinitionDTO.getId() <= AppConstants
        .SAAS_RESERVED_AUTO_INCREMENT) {
            throw new BadRequestException("Tenant schema measure_definition id must be greater than " + AppConstants
            .SAAS_RESERVED_AUTO_INCREMENT);
        }*/
        if (addInTenant != null && addInTenant.booleanValue() == true) {
            return toMeasureDefinitionDTO(service.save(getBillingDefinition(billingDefinitionDTO)));
        }
        return toMeasureDefinitionDTO(measureDefinitionSAASService.save(getBillingDefinitionSAAS(billingDefinitionDTO)));
    }

    @PutMapping
//    @PreAuthorize("hasPermission('Measure API', 'Update')")
    public MeasureDefinitionTenantDTO update(@RequestBody MeasureDefinitionTenantDTO billingDefinitionDTO,
                                             @RequestParam(required = false) Boolean addInTenant) {
        /*if (addInTenant != null && addInTenant.booleanValue() == true) {
            return toMeasureDefinitionDTO(service.save(getBillingDefinition(billingDefinitionDTO)));
        }*/
        //return toMeasureDefinitionDTO(measureDefinitionSAASService.save(getBillingDefinitionSAAS(billingDefinitionDTO)));
        return toMeasureDefinitionDTO(service.update(getBillingDefinition(billingDefinitionDTO)));
    }

    private MeasureDefinitionTenant getBillingDefinition(MeasureDefinitionTenantDTO billingDefinitionDTO) {
        MeasureDefinitionTenant billingDefinition = toMeasureDefinition(billingDefinitionDTO);
        return billingDefinition;
    }

    private MeasureDefinitionSAAS getBillingDefinitionSAAS(MeasureDefinitionTenantDTO billingDefinitionDTO) {
        MeasureDefinitionSAAS billingDefinition = MeasureDefinitionSAASMapper.toMeasureDefinition(billingDefinitionDTO);
        return billingDefinition;
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasPermission('Measure API', 'Read')")
    public MeasureDefinitionTenantDTO findById(@PathVariable Long id) {
        return overrideService.findById(id);
    }

    @GetMapping
//    @PreAuthorize("hasPermission('Measure API', 'ReadAll')")
    public List<MeasureDefinitionTenantDTO> findAll() {
        return overrideService.findAll();
    }

    @GetMapping("/code/{code}")
    MeasureDefinitionTenantDTO findByCode(@PathVariable String code) {
        return overrideService.findMeasureDefinitionByCode(code);
    }

    @GetMapping("/codes/{codesCSV}")
//    @PreAuthorize("hasPermission('Measure API', 'ReadAll')")
    List<MeasureDefinitionTenantDTO> findByCodes(@PathVariable String codesCSV) {
        Set<String> ids = Arrays.stream(codesCSV.split(",")).map(id -> id.trim()).collect(Collectors.toSet());
        return overrideService.findByCodes(ids);
    }

    @GetMapping("/regModule/{regModuleId}") //1
//    @PreAuthorize("hasPermission('Measure API', 'ReadAll')")
    List<MeasureDefinitionTenantDTO> findByRegModuleId(@PathVariable Long regModuleId) {
        return overrideService.findByRegModuleId(regModuleId);
    }

    @GetMapping("/checkMeasureLinkWithRegister/{measureCodeId}")
//    @PreAuthorize("hasPermission('Measure API', 'Read')")
    public ResponseEntity<?> checkMeasureLinkWithRegister(@PathVariable Long measureCodeId) {
        BaseResponse<Object> baseResponse = new BaseResponse(HttpStatus.OK.value(),
                overrideService.checkMeasureLinkWithRegister(measureCodeId));
        return ResponseEntity.ok(baseResponse);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasPermission('Measure API', 'Delete')")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
//    @PreAuthorize("hasPermission('Measure API', 'Delete')")
    public ResponseEntity deleteAll() {
        service.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
