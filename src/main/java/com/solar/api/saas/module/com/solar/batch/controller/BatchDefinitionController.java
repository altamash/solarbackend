package com.solar.api.saas.module.com.solar.batch.controller;

import com.solar.api.saas.mapper.extended.BatchDefinitionDTO;
import com.solar.api.saas.module.com.solar.batch.service.BatchDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.saas.mapper.extended.BatchDefinitionMapper.*;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BatchDefinitionController")
@RequestMapping(value = "/batchDefinition")
public class BatchDefinitionController {

    @Autowired
    private BatchDefinitionService batchDefinitionService;

    @PostMapping("/add")
    public BatchDefinitionDTO addBatchDefinition(@RequestBody BatchDefinitionDTO batchDefinitionDTO) {
        return toBatchDefinitionDTO(batchDefinitionService.save(toBatchDefinition(batchDefinitionDTO)));
    }

    @PutMapping("/update")
    public BatchDefinitionDTO updateBatchDefinition(@RequestBody BatchDefinitionDTO batchDefinitionDTO) {
        return toBatchDefinitionDTO(batchDefinitionService.save(toBatchDefinition(batchDefinitionDTO)));
    }

    @GetMapping("/{id}")
    public BatchDefinitionDTO findBatchDefinitionById(@PathVariable Long id) {
        return toBatchDefinitionDTO(batchDefinitionService.findById(id));
    }

    @GetMapping
    public List<BatchDefinitionDTO> findAllBatchDefinitions() {
        return toBatchDefinitionDTOs(batchDefinitionService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBatchDefinition(@PathVariable Long id) {
        batchDefinitionService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAllBatchDefinitions() {
        batchDefinitionService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
