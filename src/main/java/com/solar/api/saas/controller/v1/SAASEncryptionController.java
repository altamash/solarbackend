package com.solar.api.saas.controller.v1;


import com.solar.api.saas.service.process.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASEncryptionController")
@RequestMapping(value = "/saas/encryption")
public class SAASEncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @GetMapping("/initKeys")
    public void initKeys() {
        encryptionService.init();
    }

    @PostMapping("/encrypt")
    public List<String> encrypt(@RequestBody String plaintText) {
        return encryptionService.encryptWithRSAWorkflow(plaintText);
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody List<String> strings) {
        return encryptionService.decryptWithRSAWorkflow(strings);
    }

    @GetMapping("/getPPK2")
    public String getPPK2() {
        return encryptionService.getPPK2();
    }

}
