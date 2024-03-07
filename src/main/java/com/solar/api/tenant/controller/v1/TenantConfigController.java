package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.tenant.mapper.preferences.TenantConfigDTO;
import com.solar.api.tenant.mapper.preferences.TenantConfigMapper;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController("TenantConfigController")
@RequestMapping(value = "/tenantConfig")
public class TenantConfigController {
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @PreAuthorize("checkAccess()")
    @PostMapping("/add")
    public TenantConfigDTO add(@RequestBody TenantConfigDTO tenantConfigDTO) throws Exception {
        return TenantConfigMapper.toTenantConfigDTO(
                tenantConfigService.add(TenantConfigMapper.toTenantConfig(tenantConfigDTO)));
    }
    @PreAuthorize("checkAccess()")
    @PutMapping("/edit")
    public TenantConfigDTO update(@RequestBody TenantConfigDTO tenantConfigDTO) throws Exception {
        return TenantConfigMapper.toTenantConfigDTO(
                tenantConfigService.update(TenantConfigMapper.toTenantConfig(tenantConfigDTO)));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/{id}")
    public TenantConfigDTO findById(@PathVariable Long id) throws Exception {
        return TenantConfigMapper.toTenantConfigDTO(tenantConfigService.findById(id));
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getAll")
    public List<TenantConfigDTO> findAll() throws Exception {
        return TenantConfigMapper.toTenantConfigDTOList(tenantConfigService.findAll());
    }

    /**
     * Description: Api to return list of email domains in tenant config
     *
     * @return
     * @throws Exception
     * @author: ibtehaj
     */
    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllEmailDomain")
    public Map findAllEmailDomain() {
        return tenantConfigService.findAllEmailDomain();
    }


    @GetMapping("/getByParameter/{parameter}")
    public TenantConfigDTO findByParameter(@RequestHeader ("Comp-Key") Long compKey,
                                           @PathVariable("parameter") String parameter) throws Exception {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        Optional<TenantConfig> tenantConfigOptional = tenantConfigService.findByParameter(parameter);
        return (tenantConfigOptional.isPresent()? TenantConfigMapper.toTenantConfigDTO(tenantConfigOptional.get()) : TenantConfigDTO.builder().id(null).parameter("").description("")
                .varType("").text("").format("").category("").build());
    }
}