package com.solar.api.tenant.controller.v1.ca;

import com.solar.api.saas.controller.v1.CrudController;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.service.ca.CaReferralInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.solar.api.tenant.mapper.ca.CaReferralInfoMapper.toCaReferralInfo;

@CrossOrigin
@RestController()
@RequestMapping(value = "/CaReferralInfo")
@AllArgsConstructor
public class CaReferralInfoController implements CrudController<CaReferralInfoDTO> {

    private final CaReferralInfoService caReferralInfoService;
    @Override
    public ResponseEntity<?> getById(Long id) {
        return ResponseEntity.ok(caReferralInfoService.getById(id));
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(caReferralInfoService.getAll());
    }

    @Override
    public ResponseEntity<?> save(CaReferralInfoDTO obj) {
        return ResponseEntity.ok(caReferralInfoService.save(toCaReferralInfo(obj)));
    }

    @Override
    public ResponseEntity<?> update(CaReferralInfoDTO obj) {
        return ResponseEntity.ok(caReferralInfoService.update(toCaReferralInfo(obj)));
    }



    @Override
    public ResponseEntity<?> delete(Long id) {
        return ResponseEntity.ok(caReferralInfoService.delete(id));
    }
}
