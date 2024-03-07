package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.tenant.mapper.EntityCountDTO;
import com.solar.api.tenant.mapper.contract.EntityDTO;
import com.solar.api.tenant.mapper.contract.EntityMapper;
import com.solar.api.tenant.mapper.contract.EntityResponseDTO;
import com.solar.api.tenant.service.contract.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EntityController")
@RequestMapping(value = "/entity")
public class EntityController {
    @Autowired
    private EntityService entityService;

    @PostMapping("/org/{organizationId}/refCode/{refCode}")
    public EntityDTO add(@RequestParam("entityDTO") String entityDTO,
                         @PathVariable Long organizationId,
                         @PathVariable String refCode,
                         @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles)
            throws Exception {
        EntityDTO dto = new ObjectMapper().readValue(entityDTO, EntityDTO.class);
        return EntityMapper.toEntityDTO(
                entityService.add(EntityMapper.toEntity(dto), organizationId, refCode, multipartFiles));
    }

    @PutMapping
    public EntityDTO update(@RequestBody EntityDTO entityDTO) {
        return EntityMapper.toEntityDTO(
                entityService.update(EntityMapper.toEntity(entityDTO)));
    }

    @GetMapping("/{id}")
    public EntityDTO findById(@PathVariable Long id) {
        return EntityMapper.toEntityDTO(entityService.findById(id));
    }

    @GetMapping
    public List<EntityDTO> findAll() {
        return EntityMapper.toEntityDTOList(entityService.findAll());
    }

    @GetMapping("/validateEmail/{emailId}")
    public boolean validateEmail(@PathVariable String emailId) {
        return entityService.isValidateEmail(emailId);
    }

    @GetMapping("/findByEntityType/{entityType}")
    public List<EntityResponseDTO> findAllByEntityType(@PathVariable String entityType) {
        return entityService.findAllByEntityType(entityType);
    }

    @GetMapping("/validateEmployeeEmail/{emailId}")
    public boolean validateEmployeeEmail(@PathVariable String emailId) {
        return entityService.isValidateEmployeeEmail(emailId);
    }

    @GetMapping("/validateCustomerEmail/{emailId}")
    public Map<String, String> validateCustomerEmail(@PathVariable String emailId) {
        return entityService.isValidateCustomerEmail(emailId);
    }

    @GetMapping("/getCustomerProfile/{userId}")
    public Map<String, Object> getCustomerProfileByUserId(@PathVariable Long userId) {
        return entityService.getCustomerProfileByUserId(userId);
    }




    @GetMapping("/countByEntityType")
    public EntityCountDTO countByEntityType() {
        return entityService.countByEntityType();
    }
}
