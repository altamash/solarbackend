package com.solar.api.saas.controller.v1;

import com.solar.api.saas.mapper.attribute.systemAttribute.SystemAttributeDTO;
import com.solar.api.saas.model.attribute.SystemAttribute;
import com.solar.api.saas.service.systemAttribute.SystemAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.solar.api.saas.mapper.attribute.systemAttribute.SystemAttributeMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SAASSystemAttributeController")
@RequestMapping(value = "/systemAttribute")
public class SAASSystemAttributeController {

    @Autowired
    private SystemAttributeService systemAttributeService;

    @PostMapping
    public SystemAttributeDTO addSystemAttribute(@RequestBody SystemAttributeDTO systemAttributeDto) {
        return toSystemAttributeDTO(
                systemAttributeService.save(toSystemAttribute(systemAttributeDto)));
    }

    @PutMapping
    public SystemAttributeDTO updateSystemAttribute(@RequestBody SystemAttributeDTO systemAttributeDto) {
        return toSystemAttributeDTO(
                systemAttributeService.update(toSystemAttribute(systemAttributeDto)));
    }

    @GetMapping("/{id}")
    public SystemAttributeDTO findById(@PathVariable Long id) {
        return toSystemAttributeDTO(systemAttributeService.findById(id));
    }

    @GetMapping("/attributeKey/{attributeKey}")
    public SystemAttributeDTO findByAttributeKey(String attributeKey) {
        return toSystemAttributeDTO(systemAttributeService.findByAttributeKey(attributeKey));
    }

    @GetMapping("/parentAttribute/{parentAttribute}")
    public List<SystemAttributeDTO> findByParent(@PathVariable String parentAttribute) {
        return toSystemAttributeDTOs(systemAttributeService.findByParentAttribute(parentAttribute));
    }

    @GetMapping("/attribute/{attribute}")
    public SystemAttributeDTO findByAttribute(@PathVariable String attribute) {
        return toSystemAttributeDTO(systemAttributeService.findByAttribute(attribute));
    }

    @GetMapping
    public List<SystemAttributeDTO> findAll() {
        return toSystemAttributeDTOs(systemAttributeService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        systemAttributeService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        systemAttributeService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/carousel/channel/{channel}")
    public SystemAttribute findAllCarouselImagesByChannel(@PathVariable String channel) throws IOException {
        return systemAttributeService.findAllCarouselImagesByChannel(channel);
    }
}
