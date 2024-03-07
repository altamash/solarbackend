package com.solar.api.saas.controller.v1;

import com.solar.api.saas.mapper.utilitycompany.UtilityCompanyDTO;
import com.solar.api.saas.mapper.utilitycompany.UtilityCompanyMapper;
import com.solar.api.saas.service.UtilityCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.saas.mapper.utilitycompany.UtilityCompanyMapper.toUtilityCompanyDTO;
import static com.solar.api.saas.mapper.utilitycompany.UtilityCompanyMapper.toUtilityCompanyDTOs;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASUtilityCompanyController")
@RequestMapping(value = "/utilityCompany")
public class SAASUtilityCompanyController {

    @Autowired
    private UtilityCompanyService utilityCompanyService;

    @PostMapping
    public UtilityCompanyDTO add(@RequestBody UtilityCompanyDTO utilityCompanyDTO) {
        return UtilityCompanyMapper.toUtilityCompanyDTO(
                utilityCompanyService.saveOrUpdate(UtilityCompanyMapper.toUtilityCompany(utilityCompanyDTO)));
    }

    @PutMapping
    public UtilityCompanyDTO update(@RequestBody UtilityCompanyDTO utilityCompanyDTO) {
        return UtilityCompanyMapper.toUtilityCompanyDTO(
                utilityCompanyService.saveOrUpdate(UtilityCompanyMapper.toUtilityCompany(utilityCompanyDTO)));
    }

    @GetMapping("/{id}")
    public UtilityCompanyDTO findById(@PathVariable Long id) {
        return toUtilityCompanyDTO(utilityCompanyService.findById(id));
    }

    @GetMapping("/companyName/{companyName}")
    public UtilityCompanyDTO findByUserId(@PathVariable String companyName) {
        return toUtilityCompanyDTO(utilityCompanyService.findByCompanyName(companyName));
    }

    @GetMapping("/utilityType/{utilityType}")
    public List<UtilityCompanyDTO> findByUtilityType(@PathVariable String utilityType) {
        return toUtilityCompanyDTOs(utilityCompanyService.findByUtilityType(utilityType));
    }

    @GetMapping("/email/{email}")
    public UtilityCompanyDTO findByEmail(@PathVariable String email) {
        return toUtilityCompanyDTO(utilityCompanyService.findByEmail(email));
    }

    @GetMapping
    public List<UtilityCompanyDTO> findAll() {
        return toUtilityCompanyDTOs(utilityCompanyService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        utilityCompanyService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        utilityCompanyService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
