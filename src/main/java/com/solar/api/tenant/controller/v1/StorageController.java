package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.saas.mapper.companyPreference.CompanyUploadDTO;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.tiles.FileDetailTile;
import com.solar.api.tenant.model.contract.EntityDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("StorageController")
public class StorageController {

    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private StorageService storageService;

    @PostMapping("/storeCompanyFAQ/{compKey}")
    public CompanyUploadDTO uploadFaq(@RequestParam("file") MultipartFile file, @PathVariable Long compKey) {
        return storageService.storeCompanyFAQ(file, compKey);
    }

    @PostMapping("/storeCompanyLogo/{compKey}")
    public CompanyUploadDTO uploadLogo(@RequestParam("file") MultipartFile file, @PathVariable Long compKey) {
        return storageService.storeCompanyLogo(file, compKey);
    }


    @PostMapping("/storeCompanyBanner/{compKey}")
    public CompanyUploadDTO uploadBanner(@RequestParam("file") List<MultipartFile> file,
                                         @RequestParam("bannerList") String bannerList, @PathVariable Long compKey)
            throws JsonProcessingException, NoSuchFieldException {
        return storageService.storeCompanyBanner(file, bannerList, compKey);
    }

    @PostMapping("/storeOneCompanyBanner/{compKey}")
    public CompanyUploadDTO uploadOneBanner(@RequestParam("file") MultipartFile file,
                                            @RequestParam("bannerList") String bannerList, @PathVariable Long compKey)
            throws JsonProcessingException, NoSuchFieldException {
        return storageService.addOrUpdateBannerByIndex(file, bannerList, compKey);
    }

    @PostMapping("/storeFile")
    public String uploadOneBanner(@RequestParam("file") MultipartFile file,
                                  @RequestParam("container") String container,
                                  @RequestParam(value = "directoryReference", required = false) String directoryReference,
                                  @RequestParam("fileName") String fileName,
                                  @RequestHeader("Comp-Key") Long compKey)
            throws IOException, NoSuchFieldException, StorageException, URISyntaxException {
        try (InputStream is = file.getInputStream()) {
            return storageService.uploadInputStream(is, file.getSize(), container, directoryReference, fileName,
                    compKey, false);
        }
    }

    @PostMapping("/storeBillingCredits/csv/{compKey}")
    public String uploadCSV(@RequestParam("file") MultipartFile file, @PathVariable Long compKey) throws StorageException, IOException, URISyntaxException {
        storageService.storeInContainer(file, appProfile, "tenant/" + compKey + "/billing/credits/csv",
                "BillingCredits", compKey, false);
        return "Your file is being processed";
    }

    @PostMapping("/storeAttachmentFile")
    public List<FileDetailTile> storeAttachmentFile(@RequestParam("file") List<MultipartFile> files,
                                  @RequestHeader("Comp-Key") Long compKey)
            throws IOException, NoSuchFieldException, StorageException, URISyntaxException {
        List<FileDetailTile> fileDetailTileList = new ArrayList<>();
        if (files.size() > 0) {
            //add timestamp to each filename

            for (MultipartFile file : files) {
                String uri = storageService.storeInContainer(file, appProfile, "tenant/" + compKey
                        + AppConstants.PATHS.COMMUNICATION_LOG, System.currentTimeMillis()+file.getOriginalFilename(), compKey, false);
                fileDetailTileList.add(FileDetailTile.builder().fileName(file.getOriginalFilename())
                        .fileType(file.getContentType()).uri(uri).fileSize(file.getSize()).build());
            }
            return fileDetailTileList;
        }
        return null;
    }
}
