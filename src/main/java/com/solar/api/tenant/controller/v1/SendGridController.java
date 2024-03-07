package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.mapper.sendgridDTO.TemplateDTO;
import com.solar.api.saas.mapper.sendgridDTO.VersionDTO;
import com.solar.api.saas.service.SendGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SendGridController")
@RequestMapping(value = "/sendgrid")
public class SendGridController {

    @Autowired
    SendGridService sendGridService;

    @PostMapping("/templateAndVersion")
    public VersionDTO addTemplateAndVersion(@RequestBody TemplateDTO templateDTO) {
        return sendGridService.addTemplateAndVersion(templateDTO);
    }

    @PostMapping("/template/add")
    public TemplateDTO addTemplate(@RequestBody TemplateDTO templateDTO) {
        return sendGridService.addTemplate(templateDTO);
    }

    @PostMapping("/version/add")
    public VersionDTO addVersion(@RequestBody VersionDTO versionDTO) {
        return sendGridService.addVersion(versionDTO);
    }

}
