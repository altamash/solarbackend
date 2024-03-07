package com.solar.api.tenant.controller.v1;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.CalculationGroupDTO;
import com.solar.api.tenant.service.CalculationGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.CalculationGroupMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("CalculationGroupController")
@RequestMapping(value = "/calculationGroup")
public class CalculationGroupController {

    @Autowired
    CalculationGroupService calculationGroupService;

    @PostMapping("/add")
    public CalculationGroupDTO add(@RequestBody CalculationGroupDTO calculationGroupDTO) throws AlreadyExistsException {
        return toCalculationGroupDTO(calculationGroupService.addOrUpdate(toCalculationGroup(calculationGroupDTO)));
    }

    @PutMapping("/update")
    public CalculationGroupDTO update(@RequestBody CalculationGroupDTO calculationGroupDTO) throws AlreadyExistsException {
        return toCalculationGroupDTO(calculationGroupService.addOrUpdate(toCalculationGroup(calculationGroupDTO)));
    }

    @GetMapping("/get")
    public List<CalculationGroupDTO> getAll() {
        return toCalculationGroupDTOs(calculationGroupService.findAll());
    }

    @GetMapping("/get/{id}")
    public CalculationGroupDTO findById(@PathVariable Long id) {
        return toCalculationGroupDTO(calculationGroupService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        calculationGroupService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity deleteAll() {
        calculationGroupService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
