package com.solar.api.tenant.controller.v1;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRatesDerivedDTO;
import com.solar.api.tenant.service.RateManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateTypeMatrixMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("RateManagementController")
@RequestMapping(value = "/rate")
public class RateManagementController {

    @Autowired
    RateManagementService rateManagementService;

    @PostMapping("/add")
    public SubscriptionRatesDerivedDTO add(@RequestBody SubscriptionRatesDerivedDTO subscriptionRatesDerivedDTO) throws AlreadyExistsException {
        return toSubscriptionRatesDerivedDTO(rateManagementService.addOrUpdate(toSubscriptionRatesDerived(subscriptionRatesDerivedDTO)));
    }

    @PutMapping("/update")
    public SubscriptionRatesDerivedDTO update(@RequestBody SubscriptionRatesDerivedDTO subscriptionRatesDerivedDTO) throws AlreadyExistsException {
        return toSubscriptionRatesDerivedDTO(rateManagementService.addOrUpdate(toSubscriptionRatesDerived(subscriptionRatesDerivedDTO)));
    }

    @GetMapping("/get")
    public List<SubscriptionRatesDerivedDTO> getAll() {
        return toSubscriptionRatesDerivedDTOs(rateManagementService.findAll());
    }

    @GetMapping("/findByCalculationGroup/{calcGroup}")
    public List<SubscriptionRatesDerivedDTO> findByCalculationGroup(@PathVariable String calcGroup) {
        return toSubscriptionRatesDerivedDTOs(rateManagementService.findByCalculationGroup(calcGroup));
    }

    @GetMapping("/get/{id}")
    public SubscriptionRatesDerivedDTO findById(@PathVariable Long id) {
        return toSubscriptionRatesDerivedDTO(rateManagementService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        rateManagementService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity deleteAll() {
        rateManagementService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
