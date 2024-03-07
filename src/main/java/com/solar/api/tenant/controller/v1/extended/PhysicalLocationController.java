package com.solar.api.tenant.controller.v1.extended;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.physicalLocation.Site;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.extended.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PhysicalLocationController")
@RequestMapping(value = "/physicalLocation")
public class PhysicalLocationController {

    @Autowired
    private PhysicalLocationService physicalLocationService;

    @Autowired
    private SiteService siteService;

    @PostMapping("/addOrUpdate")
    public PhysicalLocationDTO addOrUpdate(@RequestBody PhysicalLocationDTO physicalLocationDTO) {
        return toPhysicalLocationDTO(physicalLocationService.saveOrUpdate(toPhysicalLocation(physicalLocationDTO)));
    }

    @GetMapping("/findAllByLocationType/{locationType}")
    public List<PhysicalLocationDTO> findAllByLocationType(@PathVariable String locationType) {
        return toPhysicalLocationDTOs(physicalLocationService.findAllByLocationType(locationType));
    }

    @GetMapping("/findAllByLocationCategory/{locationCategory}")
    public List<PhysicalLocationDTO> findAllByLocationCategory(@PathVariable String locationCategory) {
        return toPhysicalLocationDTOs(physicalLocationService.findAllByLocationCategory(locationCategory));
    }
    @GetMapping("/{id}")
    public PhysicalLocationDTO findById(@PathVariable Long id) {
        return physicalLocationService.findPhysicalLocationWithSitesById(id);
    }

    @GetMapping("/findAll")
    public List<PhysicalLocationDTO> findAll() {
        return toPhysicalLocationDTOs(physicalLocationService.findAllPhysicalLocationsByIsDeleted(false));
    }

    @GetMapping("/findAllNotLinkedToSite")
    public List<PhysicalLocationDTO> findAllNotLinkedToSite() {
        return toPhysicalLocationDTOs(physicalLocationService.findAllNotLinkedToSite());
    }

    @DeleteMapping("/{id}")
    public ObjectNode deleteById(@PathVariable Long id) {
        return physicalLocationService.deleteById(id);
    }

    @GetMapping("/findLocationByUserId/{userId}")
    public PhysicalLocationDTO findLocationByUserId(@PathVariable Long userId) {
        return physicalLocationService.findLocationByUserId(userId);
    }

    @GetMapping("/findAllSiteByLocationType")
    public ResponseEntity<Map<String, Object>> findAllByLocationTypeAndOrgId(@RequestParam(value = "parentOrgId", required = false) Long parentOrgId,
                                                                             @RequestParam("locationType") String locationType,
                                                                             @RequestParam(value = "unitType", required = false) String unitType) {
        List<PhysicalLocationDTO> physicalLocationDTOS = toPhysicalLocationDTOs(physicalLocationService.getAllSiteUnselectedLocationByType(locationType, unitType, parentOrgId));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "location get success");

        if (physicalLocationDTOS == null || physicalLocationDTOS.isEmpty()) {
            response.put("message", "location not found");
        }

        response.put("code", HttpStatus.OK);
        response.put("data", physicalLocationDTOS);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/findAllLocationByOrg/{organizationId}")
    public ResponseEntity<Map> findAllByLocationOrgId(@PathVariable Long organizationId) {

        Site site = siteService.getSiteByRefId(organizationId);

        List<PhysicalLocation> physicalLocation = physicalLocationService.getAllLocationsByExternalRefId(site.getId());

        Map response = new HashMap();
        response.put("message", "location get success");

        if (physicalLocation == null || physicalLocation.size() == 0) {
            response.put("message", "location not found");
        }

        response.put("code", HttpStatus.OK);
        response.put("data", physicalLocation);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/removeLocationOrgAssociation/{locationId}")
    public ResponseEntity<Map> removeLocationOrgAssociation(@PathVariable Long locationId) {

        PhysicalLocation physicalLocation = physicalLocationService.removeLocationAssociationById(locationId);

        Map response = new HashMap();
        response.put("message", "location removed");

        if (physicalLocation == null || physicalLocation.getId() == 0) {
            response.put("message", "location not found");
        }

        response.put("code", HttpStatus.OK);
        response.put("data", null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllPhysicalLocationByEntityId/entityId/{entityId}")
    public ResponseEntity<Map> getAllPhysicalLocationByEntityId(@PathVariable Long entityId) {
        return ResponseEntity.ok(physicalLocationService.getAllPhysicalLocationByEntityId(entityId));
    }

    @GetMapping("/findAllByOrg")
    public List<PhysicalLocationDTO> findAllByOrg(@RequestParam("orgId") Long orgId) {
        return toPhysicalLocationDTOs(physicalLocationService.findAllByOrgId(orgId));
    }

    @PutMapping("/updateLocationStatus")
    public ObjectNode updateLocationStatus(@RequestParam("locId") Long locId, @RequestParam("isActive") Boolean isActive) {
        return physicalLocationService.updateLocationStatus(locId, isActive);
    }


    @PutMapping("/markLocationAsPrimary")
    public ObjectNode markLocationAsPrimary(@RequestParam("locId") Long locId, @RequestParam("acctId") Long acctId) {
        return physicalLocationService.markLocationAsPrimary(acctId, locId);
    }
}
