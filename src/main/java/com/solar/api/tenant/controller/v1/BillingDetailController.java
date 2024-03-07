package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDetailDTO;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDiscountDTO;
import com.solar.api.tenant.service.BillingDetailService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.process.billing.billingDiscount.BillingDiscount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.solar.api.tenant.mapper.billing.billingHead.BillingDetailMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BillingDetailController")
@RequestMapping(value = "/billing")
public class BillingDetailController {

    private final BillingDetailService billingDetailService;

    @Autowired
    private BillingDiscount billingDiscountService;

    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;

    /**
     * @param billingDetailService
     */
    BillingDetailController(BillingDetailService billingDetailService) {
        this.billingDetailService = billingDetailService;
    }

    /**
     * @param billingDetailDTO
     * @return
     */
    @PostMapping("/billingDetail")
    public BillingDetailDTO add(@RequestBody BillingDetailDTO billingDetailDTO) {
        return toBillingDetailDTO(billingDetailService.addOrUpdateBillingDetail(toBillingDetail(billingDetailDTO)));
    }

    /**
     * @param billingDiscountDTO
     * @return
     */
    @PostMapping("/billingDetail/addDiscount")
    public ObjectNode add(@RequestBody BillingDiscountDTO billingDiscountDTO) {
        return billingDiscountService.addDiscount(billingDiscountDTO);
    }

    /**
     * Description: Method to add discount to bills
     * @param billingDiscountDTO
     * @return
     */
    @PostMapping("/billingDetail/v1/addDiscount")
    public Map addDiscountV1(@RequestBody BillingDiscountDTO billingDiscountDTO) {
        Map response = new HashMap();
        if (billingDiscountDTO != null) {
            return billingDiscountService.addDiscountV1(billingDiscountDTO);
        } else {
            response.put("code", HttpStatus.PRECONDITION_FAILED);
            response.put("message", "Parameters cannot be null");
            response.put("data", null);
        }
        return response;

    }
    /**
     * Description: Method for bulk discount of bills
     * @param billingDiscountDTO
     * @param billingHeadIds
     */
    @PostMapping("/billingDetail/v1/addDiscountBulk")
    public Map bulkAddDiscountV1(@RequestBody BillingDiscountDTO billingDiscountDTO, @RequestParam("billingHeadIds") String billingHeadIds) {
        Map response = new HashMap();
        if (billingDiscountDTO != null && billingHeadIds != null) {
            billingDiscountService.bulkAddDiscountV1(billingDiscountDTO, billingHeadIds);
            response.put("code", HttpStatus.OK);
            response.put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
            response.put("data", null);
        } else {
            response.put("code", HttpStatus.PRECONDITION_FAILED);
            response.put("message", "Parameters cannot be null");
            response.put("data", null);
        }
        return response;

    }

    @PutMapping("/billingDetail")
    public BillingDetailDTO update(@RequestBody BillingDetailDTO billingDetailDTO) {
        return toBillingDetailDTO(billingDetailService.addOrUpdateBillingDetail(toBillingDetail(billingDetailDTO)));
    }

    @GetMapping("/billingDetail/{id}")
    public BillingDetailDTO findById(@PathVariable Long id) {
        return toBillingDetailDTO(billingDetailService.findById(id));
    }

    @GetMapping("/billingDetail/headById/{id}")
    public List<BillingDetailDTO> findByHeadId(@PathVariable Long id) {

        List<BillingDetailDTO> billingDetailDTO = toBillingDetailDTOs(billingDetailService.findByBillingHeadId(id));
        billingDetailDTO.forEach(detail -> {
            detail.setPortalAttribute(attributeOverrideService.findByAttributeValue(detail.getBillingCode()));
        });
        return billingDetailDTO;
    }

    @GetMapping("/billingDetail")
    public List<BillingDetailDTO> findAll() {
        return toBillingDetailDTOs(billingDetailService.findAll());
    }

    @DeleteMapping("/billingDetail/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        billingDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/billingDetail")
    public ResponseEntity deleteAll() {
        billingDetailService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
