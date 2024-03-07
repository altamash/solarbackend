package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.resources.HRHeadDTO;
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
@RestController("HRHeadController")
@RequestMapping(value = "/hrDirectory")
public class HRHeadController {

    @Autowired
    private HRHeadService hRHeadService;

    @PostMapping
    public HRHeadDTO add(@RequestBody HRHeadDTO hrHeadDTO) {
        return toHRHeadDTO(hRHeadService.save(toHRHead(hrHeadDTO)));
    }

    @PutMapping
    public HRHeadDTO update(@RequestBody HRHeadDTO hrHeadDTO) {
        return toHRHeadDTO(hRHeadService.save(toHRHead(hrHeadDTO)));
    }

    @GetMapping("/{id}")
    public HRHeadDTO findById(@PathVariable Long id) {
        return toHRHeadDTO(hRHeadService.findById(id));
    }

    @GetMapping
    public List<HRHeadDTO> findAll() {
        return toHRHeadDTOs(hRHeadService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        hRHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        hRHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
