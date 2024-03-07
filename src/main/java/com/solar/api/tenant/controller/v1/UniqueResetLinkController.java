package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.mapper.user.forgotPassword.UniqueResetLinkDTO;
import com.solar.api.tenant.mapper.user.forgotPassword.UniqueResetLinkMapper;
import com.solar.api.tenant.model.user.UniqueResetLink;
import com.solar.api.tenant.repository.UniqueResetLinkRepository;
import com.solar.api.tenant.service.UniqueResetLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("UniqueResetLinkController")
@RequestMapping(value = "/resetLink")
public class UniqueResetLinkController {

    @Autowired
    private UniqueResetLinkRepository uniqueResetLinkRepository;

    @Autowired
    private UniqueResetLinkService uniqueResetLinkService;

    @GetMapping("/getToken/{unique_text}")
    public ResponseEntity<UniqueResetLinkDTO> getUniqueText(@PathVariable("unique_text") String uniqueText) {

        UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(uniqueText);

        if (uniqueResetLinkData != null) {
            return new ResponseEntity<>(UniqueResetLinkMapper.toUniqueResetLinkDTO(uniqueResetLinkData), HttpStatus.OK);
        } else {
            return new ResponseEntity(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<UniqueResetLinkDTO>> getAllTokens() {
        return new ResponseEntity<>(UniqueResetLinkMapper.toUniqueResetLinkDTOs(uniqueResetLinkRepository.findAll()),
                HttpStatus.OK);
    }
}
