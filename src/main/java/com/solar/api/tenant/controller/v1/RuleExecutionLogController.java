package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.process.rule.RuleExecutionLogDTO;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.process.rule.RuleExecutionLogMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("RuleExecutionLogController")
@RequestMapping(value = "/ruleExecutionLog")
public class RuleExecutionLogController {

    @Autowired
    private RuleExecutionLogServiceImpl service;

    @PostMapping
    public RuleExecutionLogDTO add(@RequestBody RuleExecutionLogDTO executionLogDTO) {
        return toRuleExecutionLogDTO(service.save(toRuleExecutionLog(executionLogDTO)));
    }

    @PutMapping
    public RuleExecutionLogDTO update(@RequestBody RuleExecutionLogDTO executionLogDTO) {
        return toRuleExecutionLogDTO(service.update(toRuleExecutionLog(executionLogDTO)));
    }

    @GetMapping("/{id}")
    public RuleExecutionLogDTO findById(@PathVariable Long id) {
        return toRuleExecutionLogDTO(service.findById(id));
    }

    @GetMapping
    public List<RuleExecutionLogDTO> findAll() {
        return toRuleExecutionLogDTOs(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        service.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
