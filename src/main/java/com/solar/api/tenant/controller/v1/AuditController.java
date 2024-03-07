package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.model.AuditResponseWrapper;
import com.solar.api.tenant.service.AuditService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("AuditController")
@RequestMapping("/audit")
public class AuditController {

    private AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @ApiOperation(value = "Get activities for BillingHead, BillingDetail, PaymentTransactionHead, " +
            "PaymentTransactionDetail (type param)")
    @GetMapping("/get/page/{page}/pagesize/{pagesize}")
    public AuditResponseWrapper getActivities(
            @PathVariable("page") int pageNumber,
            @PathVariable("pagesize") Integer pageSize,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "changedPropertiesCSV", required = false) String changedPropertiesCSV,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "revisionDate", required = false) String revisionDate,
            @RequestParam(value = "daysBefore", required = false) Integer daysBefore,
            @RequestParam(value = "revisionStartDate", required = false) String revisionStartDate,
            @RequestParam(value = "revisionEndDate", required = false) String revisionEndDate,
            @RequestParam(value = "propertyName", required = false) String fieldName,
            @RequestParam(value = "propertyValue", required = false) String fieldValue,
            @RequestParam(value = "includeAdditions", required = false) Boolean includeAdditions) throws ClassNotFoundException,
            InvocationTargetException, NoSuchMethodException, IllegalAccessException, ParseException,
            NoSuchFieldException {
        return auditService.getAllActivities(pageNumber, pageSize, sort, type, id, userId, changedPropertiesCSV,
                revisionDate, daysBefore, revisionStartDate, revisionEndDate, fieldName, fieldValue, includeAdditions);
    }
}
