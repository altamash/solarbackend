package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.systemCalculation.SystemCalculationDTO;
import com.solar.api.tenant.service.extended.SystemCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.systemCalculation.SystemCalculationMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SystemCalculationController")
@RequestMapping(value = "/systemCalculation")
public class SystemCalculationController {

    @Autowired
    private SystemCalculationService systemCalculationService;

    @PostMapping
    public SystemCalculationDTO add(@RequestBody SystemCalculationDTO serviceHeadDTO) {
        return toSystemCalculationDTO(systemCalculationService.save(toSystemCalculation(serviceHeadDTO)));
    }

    @PutMapping
    public SystemCalculationDTO update(@RequestBody SystemCalculationDTO serviceHeadDTO) {
        return toSystemCalculationDTO(systemCalculationService.save(toSystemCalculation(serviceHeadDTO)));
    }

    @GetMapping("/{id}")
    public SystemCalculationDTO findById(@PathVariable Long id) {
        return toSystemCalculationDTO(systemCalculationService.findById(id));
    }

    @GetMapping
    public List<SystemCalculationDTO> findAll() {
        return toSystemCalculationDTOs(systemCalculationService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        systemCalculationService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        systemCalculationService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
