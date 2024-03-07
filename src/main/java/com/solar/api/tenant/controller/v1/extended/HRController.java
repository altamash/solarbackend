package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.resources.HRDetailDTO;
import com.solar.api.tenant.mapper.extended.resources.HRHeadDTO;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.service.extended.resources.HRDetailService;
import com.solar.api.tenant.service.extended.resources.HRHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.resources.HRMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("HRController")
@RequestMapping(value = "/hr")
public class HRController {

    @Autowired
    private HRHeadService hrHeadService;
    @Autowired
    private HRDetailService hrDetailService;

    @PostMapping("/head/add")
    public HRHeadDTO hrAdd(@RequestBody HRHeadDTO hrHeadDTO) {
        return toHRHeadDTO(hrHeadService.save(toHRHead(hrHeadDTO)));
    }

    @PutMapping("/head/update")
    public HRHeadDTO hrUpdate(@RequestBody HRHeadDTO hrHeadDTO) {
        HRHeadDTO hrHeadDTO1 = toHRHeadDTO(hrHeadService.update(toHRHead(hrHeadDTO)));
        hrHeadDTO1.setHrDetails(toHRDetailDTOs(hrDetailService.update(toHRDetails(hrHeadDTO.getHrDetails()))));
        return hrHeadDTO1;
    }

    @GetMapping("/head/{id}")
    public HRHeadDTO findById(@PathVariable Long id) {
        return toHRHeadDTO(hrHeadService.findById(id));
    }

    @GetMapping("/head/registerId/{registerId}")
    public List<HRHeadDTO> findAllByRegisterId(@PathVariable Long registerId) {
        return toHRHeadDTOs(hrHeadService.findAllByRegisterId(registerId));
    }

    @GetMapping("/head/findAll")
    public List<HRHeadDTO> findAll() {
        return toHRHeadDTOs(hrHeadService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        hrHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity deleteAll() {
        hrHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/detail/add")
    public ResponseEntity<?> hrDetailAdd(@RequestBody List<HRDetailDTO> hrDetailDTO) {
        toHRDetailDTOs(hrDetailService.saveAll(toHRDetails(hrDetailDTO)));
        BaseResponse<Object> baseResponse = new BaseResponse(HttpStatus.OK.value(), "HR Details Saved");
        return ResponseEntity.ok(baseResponse);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<?> hrDetailUpdate(@RequestBody List<HRDetailDTO> hrDetailDTOs) {
        hrDetailService.update(toHRDetails(hrDetailDTOs));
        BaseResponse<Object> baseResponse = new BaseResponse(HttpStatus.OK.value(), "HR Details Updated");
        return ResponseEntity.ok(baseResponse);
    }


}
