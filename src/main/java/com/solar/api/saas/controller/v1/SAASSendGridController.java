package com.solar.api.saas.controller.v1;

import com.solar.api.saas.mapper.sendgridDTO.TemplateDTO;
import com.solar.api.saas.service.SendGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASSendGridController")
@RequestMapping(value = "/saas/sendgrid")
public class SAASSendGridController {

    @Autowired
    SendGridService sendGridService;

    @PostMapping("/template/add")
    public TemplateDTO addPortalAttribute(@RequestBody TemplateDTO templateDTO) {
        return sendGridService.addTemplate(templateDTO);
    }
}
