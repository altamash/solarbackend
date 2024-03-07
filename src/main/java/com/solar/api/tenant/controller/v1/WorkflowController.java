package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.service.workflow.HookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("WorkflowController")
@RequestMapping(value = "/workflow")
public class WorkflowController {

    @Autowired
    private HookValidator hookValidator;

    @PostMapping("/logNotification/{hookConstant}")
    public void logNotification(@RequestBody Map<String, String> requestBody,
                                @RequestParam("hookConstant") String hookConstant,
                                @RequestParam("senderId") Long senderId,
                                @RequestParam(value = "dynamicRecipientId", required = false) Long dynamicRecipientId) {
        hookValidator.hookFinder(null, hookConstant, senderId, dynamicRecipientId, requestBody, null);
    }

}
