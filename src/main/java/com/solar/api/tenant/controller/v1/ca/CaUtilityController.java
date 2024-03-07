package com.solar.api.tenant.controller.v1.ca;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.saas.controller.v1.CrudController;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.mapper.ca.CaUtilityMapper;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.service.ca.CaUtilityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.solar.api.tenant.mapper.ca.CaUtilityMapper.toCaUtility;

@CrossOrigin
@RestController()
@RequestMapping(value = "/CaUtility")
@AllArgsConstructor
public class CaUtilityController implements CrudController<CaUtilityDTO> {
    private CaUtilityService caUtilityService;
    private ObjectMapper objectMapper;
    @Override
    public ResponseEntity<?> getById(Long id) {
        return ResponseEntity.ok(caUtilityService.getById(id));
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(caUtilityService.getAll());
    }

    @Override
    public ResponseEntity<?> save(CaUtilityDTO obj) {
        return ResponseEntity.ok(null);//caUtilityService.save(toCaUtility(obj)));
    }

    @Override
    public ResponseEntity<?> update(CaUtilityDTO obj) {
        return ResponseEntity.ok(caUtilityService.update(toCaUtility(obj)));
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        return ResponseEntity.ok(caUtilityService.delete(id));
    }

    @PostMapping("save")
    public ResponseEntity<?> saveUtility (@RequestParam("caUtility") String caUtilityDTO,
                          @RequestParam(name = "documents", required = false) List<MultipartFile> multipartFiles){
        try {
            CaUtilityDTO utilityDTO= objectMapper.readValue(caUtilityDTO,CaUtilityDTO.class);
            CaUtility utility= CaUtilityMapper.toCaUtility(utilityDTO);
            return null;//ResponseEntity.ok(caUtilityService.save(utility,multipartFiles));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
