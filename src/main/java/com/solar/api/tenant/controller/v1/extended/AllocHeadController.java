package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.AllocHeadDTO;
import com.solar.api.tenant.model.extended.AllocHead;
import com.solar.api.tenant.repository.AllocHeadRepository;
import com.solar.api.tenant.service.extended.AllocHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.AllocHeadMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("AllocHeadController")
@RequestMapping(value = "/allocHead")
public class AllocHeadController {

    @Autowired
    private AllocHeadService allocHeadService;
    @Autowired
    private AllocHeadRepository allocHeadRepository;

    @PostMapping
    public AllocHeadDTO add(@RequestBody AllocHeadDTO allocHeadDTO) {
        return toAllocHeadDTO(allocHeadService.save(toAllocHead(allocHeadDTO)));
    }

    @PutMapping
    public AllocHeadDTO update(@RequestBody AllocHeadDTO allocHeadDTO) {
        AllocHead allocHead = allocHeadRepository.findById(allocHeadDTO.getAllocId()).orElse(null);
        return toAllocHeadDTO(allocHead == null ? allocHead : allocHeadService.save(toUpdatedAllocHead(allocHead,
                toAllocHead(allocHeadDTO))));
    }

    @GetMapping("/{id}")
    public AllocHeadDTO findById(@PathVariable Long id) {
        return toAllocHeadDTO(allocHeadService.findById(id));
    }

    @GetMapping
    public List<AllocHeadDTO> findAll() {
        return toAllocHeadDTOs(allocHeadService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        allocHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity delete() {
        allocHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
