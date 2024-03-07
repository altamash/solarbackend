package com.solar.api.saas.controller.v1;

import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASMasterTenantController")
@RequestMapping(value = "/saas")
public class SAASMasterTenantController {

    @Autowired
    private MasterTenantService masterTenantService;

    @PostMapping
    public MasterTenant save(@RequestBody MasterTenant masterTenant) {
        return masterTenantService.save(masterTenant);
    }

    @PutMapping
    public MasterTenant update(@RequestBody MasterTenant masterTenant) {
        return masterTenantService.update(masterTenant);
    }

    @GetMapping("/{id}")
    public MasterTenant findById(@PathVariable Long id) {
        return masterTenantService.findById(id);
    }

    @GetMapping
    public List<MasterTenant> findAllFetchTenantRoles() {
        return masterTenantService.findAllFetchTenantRoles();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        masterTenantService.delete(id);
    }

    @DeleteMapping
    public void deleteAll() {
        masterTenantService.deleteAll();
    }
}
