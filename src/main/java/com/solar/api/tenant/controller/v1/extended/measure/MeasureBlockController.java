package com.solar.api.tenant.controller.v1.extended.measure;

import com.solar.api.tenant.mapper.extended.resources.MeasureBlockHeadDTO;
import com.solar.api.tenant.service.extended.measure.MeasureBlockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.measure.MeasureBlockMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("MeasureBlockController")
@RequestMapping(value = "/measureBlock")
public class MeasureBlockController {

    private final MeasureBlockService measureBlockService;

    MeasureBlockController(MeasureBlockService measureBlockService){
        this.measureBlockService = measureBlockService;
    }

    @PostMapping("/head")
//    @PreAuthorize("hasPermission('Measure API', 'Write')")
    public MeasureBlockHeadDTO add(@RequestBody MeasureBlockHeadDTO measureBlockHeadDTO) {
        return toMeasureBlockHeadDTO(measureBlockService.save(toMeasureBlockHead(measureBlockHeadDTO)));
    }

    @PutMapping("/head")
//    @PreAuthorize("hasPermission('Measure API', 'Update')")
    public MeasureBlockHeadDTO update(@RequestBody MeasureBlockHeadDTO measureBlockHeadDTO) {
        return toMeasureBlockHeadDTO(measureBlockService.save(toMeasureBlockHead(measureBlockHeadDTO)));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasPermission('Measure API', 'Read')")
    public MeasureBlockHeadDTO findById(@PathVariable Long id) { return toMeasureBlockHeadDTO(measureBlockService.findById(id)); }

    @GetMapping("/findAll")
//    @PreAuthorize("hasPermission('Measure API', 'ReadAll')")
    public List<MeasureBlockHeadDTO> findAll() {
        return toMeasureBlockHeadDTOs(measureBlockService.findAll());
    }

    @GetMapping("/regModule/{regModuleId}")
//    @PreAuthorize("hasPermission('Measure API', 'ReadAll')")
    List<MeasureBlockHeadDTO> findAllByRegModuleId(@PathVariable Long regModuleId) {
        return toMeasureBlockHeadDTOs(measureBlockService.findAllByRegModuleId(regModuleId));
    }

    @GetMapping("/getBlockHeaderAndFormat/{registerId}/{blockId}")
//    @PreAuthorize("hasPermission('Measure API', 'Read')")
    public ResponseEntity<?> getBlockHeaderAndFormat(@PathVariable Long registerId,@PathVariable Long blockId) {
        return ResponseEntity.ok(measureBlockService.getBlockHeaderAndFormat(registerId,blockId));
    }

}
