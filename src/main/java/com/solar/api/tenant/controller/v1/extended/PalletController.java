package com.solar.api.tenant.controller.v1.extended;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PalletController")
@RequestMapping(value = "/pallet")
public class PalletController {
}
