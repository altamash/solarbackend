package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.controlPanel.*;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.service.controlPanel.ControlPanelService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.solar.api.tenant.mapper.controlPanel.ControlPanelMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ControlPanelController")
@RequestMapping(value = "/controlpanel")
public class ControlPanelController {

    @Autowired
    ControlPanelService controlPanelService;

    @Autowired
    PhysicalLocationService physicalLocationService;


    @PostMapping("/static/add")
    public ControlPanelStaticDataDTO add(@RequestBody ControlPanelStaticDataDTO controlPanelStaticDataDTO) {
        return toControlPanelStaticDataDTO(controlPanelService.addOrUpdateStaticData(toControlPanelStaticData(controlPanelStaticDataDTO)));
    }

    @GetMapping("/static/{id}")
    public ControlPanelStaticDataDTO findById(@PathVariable Long id) {
        return toControlPanelStaticDataDTO(controlPanelService.findStaticById(id));
    }

    @GetMapping("/restructure")
    public CPRestructureDTO getRestructureData() {
        return controlPanelService.getRestructureData();
    }

    @GetMapping("/restructure/categories")
    public List<CPRestructureDTO>  getRestructureDataCategories() {
        return controlPanelService.getRestructureDataCategories();
    }

    @GetMapping("/restructure/products/{product}")
    public ProductType getRestructureDataProduct(@PathVariable String product) {
        return controlPanelService.getProduct(product);
    }

    @GetMapping("/restructure/garden/{gardenId}")
    public SolarGarden getGarden(@PathVariable Long gardenId) {
        return controlPanelService.getGarden(gardenId);
    }

    @GetMapping("/static/findStaticByVariantId/{variantId}")
    public ControlPanelStaticDataDTO findStaticByVariantId(@PathVariable String variantId) {
        return toControlPanelStaticDataDTO(controlPanelService.findStaticByVariantId(variantId));
    }

    @GetMapping("/static")
    public List<ControlPanelStaticDataDTO> getAllStatic() {
        return toControlPanelStaticDataDTOs(controlPanelService.getAllStatic());
    }

    @GetMapping("/static/controlPanelDTO")
    public List<ControlPanelDTO> getAllStaticControlPanelDTO() {
        List<ControlPanelDTO> controlPanelDTOList = new ArrayList<>();
        List<ControlPanelStaticDataDTO> controlPanelStaticDataDTO = toControlPanelStaticDataDTOs(controlPanelService.getAllStatic());
        controlPanelStaticDataDTO.forEach(cp -> {
            PhysicalLocation physicalLocation = new PhysicalLocation();
            if (cp.getLocId() != null) {
                physicalLocation = physicalLocationService.findById(cp.getLocId());
            }
            controlPanelDTOList.add(ControlPanelDTO.builder()
                    .id(cp.getId())
                            .variantName(cp.getVariantName())
                    .variantSize(cp.getVariantSize())
                    .noOfSites(cp.getNoOfSites())
                    .variantOccupancy(cp.getVariantOccupancy())
                    .NoOfInverters(cp.getNoOfInverters())
                    .srcNo(cp.getSrcNo())
                    .variantId(cp.getVariantId())
                    .variantType(cp.getVariantType())
                    .orgId(cp.getOrgId())
                    .productCategory(cp.getProductCategory())
                    .environmentalContribution(cp.getEnvironmentalContribution())
                    .premiseNumber(cp.getPremiseNumber())
                    .premiseAllocation(cp.getPremiseAllocation())
                    .premiseEntityId(cp.getPremiseEntityId())
                    .LocId(cp.getLocId())
                    .physicalLocationDTO(physicalLocation == null ? null : PhysicalLocationMapper.toPhysicalLocationDTO(physicalLocation))
                    .build());
        });
        return controlPanelDTOList;
    }


    @PostMapping("/transactional/add")
    public ControlPanelTransactionalDataDTO add(@RequestBody ControlPanelTransactionalDataDTO controlPanelTransactionalDataDTO) {
        return toControlPanelTransactionalDataDTO(controlPanelService.addOrUpdateTransactionalData(toControlPanelTransactionalData(controlPanelTransactionalDataDTO)));
    }

    @GetMapping("/transactional/{id}")
    public ControlPanelTransactionalDataDTO findTransactionalById(@PathVariable Long id) {
        return toControlPanelTransactionalDataDTO(controlPanelService.findTransactionalById(id));
    }

    @GetMapping("/transactional/findTransactionalByVariantId/{variantId}")
    public ControlPanelTransactionalDataDTO findTransactionalByVariantId(@PathVariable Long variantId) {
        return toControlPanelTransactionalDataDTO(controlPanelService.findTransactionalByVariantId(variantId));
    }

    @GetMapping("/transactional")
    public List<ControlPanelTransactionalDataDTO> getAllTransactional() {
        return toControlPanelTransactionalDataDTOs(controlPanelService.getAllTransactional());
    }

    //this service returns garden and cp size details
    @GetMapping("/transactional/findStaticByVariantSizeAndLoc")
    public List<ControlPanelStaticDataDTO> findStaticByVariantSizeAndLoc(){
        return controlPanelService.getStaticByLocAndVariantSize();
    }

}
