package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.process.upload.UploadResponse;
import com.solar.api.saas.service.process.upload.health.HealthCheckResult;
import com.solar.api.tenant.mapper.externalFile.ExternalFileDTO;
import com.solar.api.tenant.mapper.externalFile.ExternalFileMapper;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.upload.ExternalFileService;
import com.solar.api.tenant.service.userDetails.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ExternalFileController")
@RequestMapping(value = "/externalFile")
public class ExternalFileController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExternalFileService externalFileService;
    @Autowired
    private BillingService billingService;

    @Autowired
    private UserService userService;


    @PostMapping("/add")
    public ExternalFileDTO addFile(@RequestBody ExternalFileDTO externalFileDTO) {
        return ExternalFileMapper.toExternalFileDTO(
                externalFileService.saveOrUpdate(ExternalFileMapper.toExternalFile(externalFileDTO)));
    }

    @GetMapping("/header")
    public ExternalFileDTO findByHeader(@QueryParam("header") String header) {
        return ExternalFileMapper.toExternalFileDTO(
                externalFileService.findByHeader(header));
    }

    @GetMapping("/getAll")
    public List<ExternalFileDTO> getAll() {
        return ExternalFileMapper.toExternalFileDTOs(externalFileService.getAllExternalFiles());
    }

    @GetMapping("/getAllMultipart")
    public List<ExternalFileDTO> getAllMultipart() {
        return ExternalFileMapper.toExternalFileDTOs(externalFileService.findByImportType("MULTIPART"));
    }

    @PostMapping("/upload")
    public ObjectNode migrate(@RequestParam("importTypeId") Long importTypeId,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) throws IOException, ClassNotFoundException {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        if (file.isEmpty()) {
            messageJson.put("message", "Please upload file");
            return messageJson;
        }
        externalFileService.parseWithImportTypeId(importTypeId, file, null, null, null, null, null, isLegacy);
        messageJson.put("message", "The uploaded file is being processed");
        return messageJson;
    }

    @PostMapping("/bulkUpload")
    public UploadResponse bulkUpload(@RequestParam("importTypeId") Long importTypeId,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "correctRowIds", required = false) String correctRowIds,
                                     @RequestParam(value = "rateMatrixId", required = false) Long rateMatrixId,
                                     @RequestParam(value = "assetId", required = false) Long assetId,
                                     @RequestParam(value = "action", required = false) String action,
                                     @RequestParam(value = "projectId", required = false) Long projectId,
                                     @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) throws IOException, ClassNotFoundException {
        List<Long> rowIds =
                Arrays.stream(correctRowIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        return externalFileService.parseWithImportTypeId(importTypeId, file, rowIds, rateMatrixId, assetId, action, projectId, isLegacy);
    }

    @PostMapping("/bulkUpload/health")
    public HealthCheckResult uploadSubscriptionMappingsHealthCheck(@RequestParam("importTypeId") Long importTypeId,
                                                                   @RequestParam("file") MultipartFile file,
                                                                   @RequestParam(value = "rateMatrixId", required = false) Long rateMatrixId,
                                                                   @RequestParam(value = "registerId", required = false) Long registerId,
                                                                   @RequestParam(value = "action", required = false) String action,
                                                                   @RequestParam(value = "assetId", required = false) Long assetId,
                                                                   @RequestParam(value = "projectId", required = false) Long projectId) throws IOException {
        return externalFileService.bulkUploadHealthCheck(importTypeId, file, rateMatrixId, registerId, action, assetId, projectId);
    }

    @PostMapping("/downloadACHFile/{ppdOrCcd}/{subscriptionCode}/{subscriptionRateMatrixIdsCSV}/{billingMonth}")
    public @ResponseBody
    ObjectNode downloadACHFile(@PathVariable("ppdOrCcd") Long ppdOrCcd,
                               @PathVariable("subscriptionCode") String subscriptionCode,
                               @PathVariable("subscriptionRateMatrixIdsCSV") String subscriptionRateMatrixIdsCSV,
                               @PathVariable("billingMonth") String billingMonth,
                               @AuthenticationPrincipal UserDetailsImpl userDetail,
                               @RequestHeader("Comp-Key") Long compKey) throws Exception {
        try {

            List<Long> subscriptionRateMatrixIds =
                    Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());

            List<String> blobUrls = billingService.generateACHCSV(subscriptionRateMatrixIds, ppdOrCcd, billingMonth, compKey, subscriptionCode, userDetail.getUsername());

            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", blobUrls.toString());
            return messageJson;

        } catch (Exception e) {
            //LOGGER.error(e.getMessage(), e);
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", "Fail to download  ACH file.");
            return messageJson;
        }
    }

    @PostMapping("/downloadCaUsersFile")
    public @ResponseBody
    ObjectNode downloadCaUsersFile(//@AuthenticationPrincipal UserDetailsImpl userDetail,
                               @RequestHeader("Comp-Key") Long compKey) throws Exception {
        try {
            List<String> blobUrls = userService.generateCaUsersCSV(compKey);
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", blobUrls.toString());
            return messageJson;

        } catch (Exception e) {
            //LOGGER.error(e.getMessage(), e);
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("blobUrl", "Fail to download leads file.");
            return messageJson;
        }
    }

}
