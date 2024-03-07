package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.SystemProductionLogDTO;
import com.solar.api.tenant.service.extended.SystemProductionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.SystemProductionLogMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SystemProductionLogController")
@RequestMapping(value = "/systemProductionLog")
public class SystemProductionLogController {

    @Autowired
    private SystemProductionLogService systemProductionLogService;

    @PostMapping
    public SystemProductionLogDTO
    add(@RequestBody SystemProductionLogDTO systemProductionLogDTO) {
        return toSystemProductionLogDTO(systemProductionLogService.save(toSystemProductionLog(systemProductionLogDTO)));
    }

    @PutMapping
    public SystemProductionLogDTO update(@RequestBody SystemProductionLogDTO systemProductionLogDTO) {
        return toSystemProductionLogDTO(systemProductionLogService.save(toSystemProductionLog(systemProductionLogDTO)));
    }

    @GetMapping("/{id}")
    public SystemProductionLogDTO findById(@PathVariable Long id) {
        return toSystemProductionLogDTO(systemProductionLogService.findById(id));
    }

    @GetMapping
    public List<SystemProductionLogDTO> findAll() {
        return toSystemProductionLogDTOs(systemProductionLogService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        systemProductionLogService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        systemProductionLogService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
