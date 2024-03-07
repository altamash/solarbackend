package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.billing.billingHead.BillSavingDTO;
import com.solar.api.tenant.service.BillSavingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.billing.billingHead.BillSavingMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BillSavingController")
@RequestMapping(value = "/billing")
public class BillSavingController {

    private final BillSavingService billSavingService;

    /**
     * @param billSavingService
     */
    BillSavingController(BillSavingService billSavingService) {
        this.billSavingService = billSavingService;
    }

    /**
     * @param billSavingDTO
     * @return
     */
    @PostMapping("/billSaving")
    public BillSavingDTO add(@RequestBody BillSavingDTO billSavingDTO) {
        return toBillSavingDTO(billSavingService.addOrUpdate(toBillSaving(billSavingDTO)));
    }

    @PutMapping("/billSaving")
    public BillSavingDTO update(@RequestBody BillSavingDTO billSavingDTO) {
        return toBillSavingDTO(billSavingService.addOrUpdate(toBillSaving(billSavingDTO)));
    }

    @GetMapping("/billSaving/{id}")
    public BillSavingDTO findById(@PathVariable Long id) {
        return toBillSavingDTO(billSavingService.findById(id));
    }

    @GetMapping("/billSaving")
    public List<BillSavingDTO> findAll() {
        return toBillSavingDTOs(billSavingService.findAll());
    }

    @DeleteMapping("/billSaving/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        billSavingService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/billSaving")
    public ResponseEntity deleteAll() {
        billSavingService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

}
