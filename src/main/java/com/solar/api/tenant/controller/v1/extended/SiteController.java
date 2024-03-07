package com.solar.api.tenant.controller.v1.extended;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.integration.Content;
import com.solar.api.tenant.mapper.extended.physicalLocation.SiteDTO;
import com.solar.api.tenant.service.extended.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.physicalLocation.SiteMapper.*;


@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SiteController")
@RequestMapping(value = "/site")
public class SiteController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private SiteService siteService;
    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/addOrUpdate")
    public ObjectNode saveOrUpdate(@RequestBody SiteDTO siteDTO) {
        return siteService.saveOrUpdate(siteDTO);
    }

    @GetMapping("/{id}")
    public SiteDTO findById(@PathVariable Long id) {
        return toSiteDTO(siteService.findById(id));
    }

    @GetMapping("/findAll")
    public List<SiteDTO> findAll() {
        return toSiteDTOs(siteService.findAll());
    }

    @PostMapping("/detail")
    public List<SiteDTO> getSiteAndPhysicalLocationDetailByIds(@QueryParam("content") String content) {
        try {
            Content[] contents = mapper.readValue(content, Content[].class);
            return siteService.getSiteAndPhysicalLocationDetail(Arrays.asList(contents));
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @GetMapping("/findAllBySiteType/{siteType}")
    public List<SiteDTO> findAllBySiteType(@PathVariable String siteType) {
        return siteService.findAllBySiteType(siteType);
    }

    @GetMapping("/findAllWithPhysicalLocations")
    public List<SiteDTO> findAllWithPhysicalLocations() {
        return toSiteDTOs(siteService.findAllWithPhysicalLocations());
    }

    @PutMapping("/updateSiteStatus")
    public ObjectNode updateSiteStatus(@RequestParam("siteId") Long siteId, @RequestParam("isActive") Boolean isActive) {
        return siteService.updateSiteStatus(siteId, isActive);
    }

    @DeleteMapping("/{id}")
    public ObjectNode deleteById(@PathVariable Long id) {
        return siteService.deleteById(id);
    }

    @GetMapping("/findAllBySiteCategory/{category}")
    public List<SiteDTO> findAllBySiteCategory(@PathVariable String category) {
        return siteService.findAllBySiteByCategoryAndIsDeleted(category,Boolean.FALSE);
    }
}
