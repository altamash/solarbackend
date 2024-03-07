package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.billing.billingPeriod.BillingPeriodDTO;
import com.solar.api.tenant.service.BillingPeriodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.billing.billingPeriod.BillingPeriodMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BillingPeriodController")
@RequestMapping(value = "/billing")
public class BillingPeriodController {

    private final BillingPeriodService billingPeriodService;

    /**
     * @param billingPeriodService
     */
    BillingPeriodController(BillingPeriodService billingPeriodService) {
        this.billingPeriodService = billingPeriodService;
    }

    /**
     * @param billingPeriodDTO
     * @return
     */
    @PostMapping("/billingPeriod")
    public BillingPeriodDTO add(@RequestBody BillingPeriodDTO billingPeriodDTO) {
        return toBillingPeriodDTO(billingPeriodService.addOrUpdate(toBillingPeriod(billingPeriodDTO)));
    }

    @PutMapping("/billingPeriod")
    public BillingPeriodDTO update(@RequestBody BillingPeriodDTO billingPeriodDTO) {
        return toBillingPeriodDTO(billingPeriodService.addOrUpdate(toBillingPeriod(billingPeriodDTO)));
    }

    @GetMapping("/billingPeriod/{id}")
    public BillingPeriodDTO findById(@PathVariable Long id) {
        return toBillingPeriodDTO(billingPeriodService.findById(id));
    }

    @GetMapping("/billingPeriod")
    public List<BillingPeriodDTO> findAll() {
        return toBillingPeriodDTOs(billingPeriodService.findAll());
    }

    @DeleteMapping("/billingPeriod/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        billingPeriodService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/addBillingPeriod")
    public ResponseEntity deleteAll() {
        billingPeriodService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
