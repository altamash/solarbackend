package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.EnvironmentLogDTO;
import com.solar.api.tenant.service.extended.EnvironmentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.EnvironmentLogMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EnvironmentLogController")
@RequestMapping(value = "/environmentLog")
public class EnvironmentLogController {

    @Autowired
    private EnvironmentLogService environmentLogService;

    @PostMapping
    public EnvironmentLogDTO add(@RequestBody EnvironmentLogDTO environmentLogDTO) {
        return toEnvironmentLogDTO(environmentLogService.save(toEnvironmentLog(environmentLogDTO)));
    }

    @PutMapping
    public EnvironmentLogDTO update(@RequestBody EnvironmentLogDTO environmentLogDTO) {
        return toEnvironmentLogDTO(environmentLogService.save(toEnvironmentLog(environmentLogDTO)));
    }

    @GetMapping("/{id}")
    public EnvironmentLogDTO findById(@PathVariable Long id) {
        return toEnvironmentLogDTO(environmentLogService.findById(id));
    }

    @GetMapping
    public List<EnvironmentLogDTO> findAll() {
        return toEnvironmentLogDTOs(environmentLogService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        environmentLogService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        environmentLogService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
