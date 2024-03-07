package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.service.process.migration.EMigrationParserLocation;
import com.solar.api.saas.service.process.migration.MigrationService;
import com.solar.api.saas.service.process.upload.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("MigrationController")
@RequestMapping(value = "/migration")
public class MigrationController {

    @Autowired
    private MigrationService migrationService;

    @PostMapping
    @Deprecated
    public UploadResponse migrate(@RequestParam("parser") String parser,
                                  @RequestParam("entity") String entity,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) throws IOException,
            ClassNotFoundException {
        return migrationService.migrate(EMigrationParserLocation.getByName(parser), entity, file, null, isLegacy);
    }
}
