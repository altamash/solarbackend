package com.solar.api.tenant.controller.v1;

import com.solar.api.helper.Message;
import com.solar.api.tenant.model.contract.OrganizationDetail;
import com.solar.api.tenant.service.contract.OrganizationDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("OrganizationDetailController")
@RequestMapping(value = "/organizationDetail")
public class OrganizationDetailController {
    @Autowired
    private OrganizationDetailService organizationDetailService;

    @GetMapping("/getAllLinkedContractOfUnit")
    public ResponseEntity<Map> getAllLinkedContractOfUnit( @RequestParam("orgBusinessUnitId") Long orgBusinessUnitId) {
        Map response = new HashMap();
        List<OrganizationDetail> orgDetailList = organizationDetailService.getLinkedContractsByMasterOrUnit(orgBusinessUnitId);
        if(orgDetailList!=null) {
            response.put("code", HttpStatus.OK);
            response.put("message", Message.ORG_DETAIL_GET_LINKED_CONTRACTS.getMessage());
        }else{
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", Message.ERROR_ORG_DETAIL_GET_LINKED_CONTRACTS);
        }
        response.put("data",orgDetailList);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/removeLinkedContractByMasterAndUnit")
    public ResponseEntity<Map> removeLinkedContractByMasterAndUnit( @RequestParam("linkedContractId") Long linkedContractId,
                                                                    @RequestParam("orgId") Long orgId) {
        Map response = new HashMap();
        Boolean isDeleted = organizationDetailService.removeLinkedContractByMasterAndUnit(orgId,linkedContractId);
        if(isDeleted) {
            response.put("code", HttpStatus.OK);
            response.put("message", Message.ORG_DETAIL_REMOVE_LINKED_CONTRACTS_SUCCESSFULLY.getMessage());
        }else{
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", Message.ERROR_ORG_DETAIL_REMOVE_LINKED_CONTRACTS);
        }
        response.put("data",null);
        return ResponseEntity.ok(response);
    }
}
