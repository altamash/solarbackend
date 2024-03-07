package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.partner.PartnerDetailDTO;
import com.solar.api.tenant.mapper.extended.partner.PartnerHeadDTO;
import com.solar.api.tenant.model.extended.partner.PartnerDetail;
import com.solar.api.tenant.repository.PartnerDetailRepository;
import com.solar.api.tenant.service.extended.partner.PartnerDetailService;
import com.solar.api.tenant.service.extended.partner.PartnerHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.partner.PartnerMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PartnerController")
@RequestMapping(value = "/partner")
public class PartnerController {

    @Autowired
    private PartnerHeadService partnerHeadService;
    @Autowired
    private PartnerDetailService partnerDetailService;
    @Autowired
    private PartnerDetailRepository partnerDetailRepository;

    // PartnerHead ////////////////////////////////////////
    @PostMapping("/head")
    public PartnerHeadDTO addPartnerHeadAndDetails(@RequestBody PartnerHeadDTO partnerHeadDTO) {
        return toPartnerHeadDTO(partnerHeadService.save(toPartnerHead(partnerHeadDTO)));
    }

    @PutMapping("/head")
    public PartnerHeadDTO updatePartnerHeadAndDetails(@RequestBody PartnerHeadDTO partnerHeadDTO) {
        PartnerHeadDTO partnerHeadDTO1 = toPartnerHeadDTO(partnerHeadService.update(toPartnerHead(partnerHeadDTO)));
        partnerHeadDTO1.setPartnerDetails(toPartnerDetailDTOs(partnerDetailService.updateAll(toPartnerDetails(partnerHeadDTO.getPartnerDetails()))));
        return partnerHeadDTO1;
    }

    @GetMapping("/head/{id}")
    public PartnerHeadDTO findPartnerHeadById(@PathVariable Long id) {
        return toPartnerHeadDTO(partnerHeadService.findById(id));
    }

    @GetMapping("/head")
    public List<PartnerHeadDTO> findAllPartnerHeads() {
        return toPartnerHeadDTOs(partnerHeadService.findAll());
    }

    @DeleteMapping("/head/{id}")
    public ResponseEntity deletePartnerHead(@PathVariable Long id) {
        partnerHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/head")
    public ResponseEntity deleteAllPartnerHeads() {
        partnerHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    // PartnerDetail ////////////////////////////////////////
    @PostMapping("/detail")
    public PartnerDetailDTO addPartnerDetail(@RequestBody PartnerDetailDTO partnerDetailDTO) {
        return toPartnerDetailDTO(partnerDetailService.save(toPartnerDetail(partnerDetailDTO)));
    }

    @PostMapping("/details")
    public List<PartnerDetailDTO> addPartnerDetails(@RequestBody List<PartnerDetailDTO> partnerDetailDTOs) {
        return toPartnerDetailDTOs(partnerDetailService.saveAll(toPartnerDetails(partnerDetailDTOs)));
    }

    @PutMapping("/detail")
    public PartnerDetailDTO updatePartnerDetail(@RequestBody PartnerDetailDTO partnerDetailDTO) {
        PartnerDetail partnerDetail = partnerDetailRepository.findById(partnerDetailDTO.getId()).orElse(null);
        return toPartnerDetailDTO(partnerDetail == null ? partnerDetail :
                partnerDetailService.update(toUpdatedPartnerDetail(partnerDetail, toPartnerDetail(partnerDetailDTO))));
    }

    @PutMapping("/details")
    public List<PartnerDetailDTO> updatePartnerDetails(@RequestBody List<PartnerDetailDTO> partnerDetailDTOs) {
        return toPartnerDetailDTOs(partnerDetailService.updateAll(toPartnerDetails(partnerDetailDTOs)));
    }

    @GetMapping("/detail/{id}")
    public PartnerDetailDTO findPartnerDetailById(@PathVariable Long id) {
        return toPartnerDetailDTO(partnerDetailService.findById(id));
    }

    @GetMapping("/detail")
    public List<PartnerDetailDTO> findAllPartnerDetails() {
        return toPartnerDetailDTOs(partnerDetailService.findAll());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deletePartnerDetail(@PathVariable Long id) {
        partnerDetailService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllPartnerDetails() {
        partnerDetailService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/headAndDetails/{registerHeadId}")
    public List<PartnerHeadDTO> findPartnerHeadAndDetails(@PathVariable Long registerHeadId) {
        return toPartnerHeadDTOs(partnerHeadService.findAllByRegisterId(registerHeadId));
    }
}
