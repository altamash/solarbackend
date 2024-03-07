package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.CodeTypeRefMapDTO;
import com.solar.api.tenant.model.extended.CodeTypeRefMap;
import com.solar.api.tenant.repository.CodeTypeRefMapRepository;
import com.solar.api.tenant.service.extended.CodeTypeRefMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.CodeTypeRefMapMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("CodeTypeRefMapController")
@RequestMapping(value = "/codeTypeRefMap")
public class CodeTypeRefMapController {

    @Autowired
    private CodeTypeRefMapService codeTypeRefMapService;
    @Autowired
    private CodeTypeRefMapRepository codeTypeRefMapRepository;

    @PostMapping
    public CodeTypeRefMapDTO add(@RequestBody CodeTypeRefMapDTO codeTypeRefMapDTO) {
        return toCodeTypeRefMapDTO(codeTypeRefMapService.save(toCodeTypeRefMap(codeTypeRefMapDTO)));
    }

    @PutMapping
    public CodeTypeRefMapDTO update(@RequestBody CodeTypeRefMapDTO codeTypeRefMapDTO) {
        CodeTypeRefMap codeTypeRefMap = codeTypeRefMapRepository.findById(codeTypeRefMapDTO.getId()).orElse(null);
        return toCodeTypeRefMapDTO(codeTypeRefMap == null ? codeTypeRefMap :
                codeTypeRefMapService.save(toUpdatedCodeTypeRefMap(codeTypeRefMap,
                        toCodeTypeRefMap(codeTypeRefMapDTO))));
    }

    @GetMapping("/{id}")
    public CodeTypeRefMapDTO findById(@PathVariable Long id) {
        return toCodeTypeRefMapDTO(codeTypeRefMapService.findById(id));
    }

    @GetMapping
    public List<CodeTypeRefMapDTO> findAll() {
        return toCodeTypeRefMapDTOs(codeTypeRefMapService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        codeTypeRefMapService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteAll() {
        codeTypeRefMapService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }
}
