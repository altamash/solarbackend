package com.solar.api.tenant.controller.v2;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.process.upload.v2.BulkUploadService;
import com.solar.api.saas.service.process.upload.v2.mapper.UploadPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BulkUploadController")
@RequestMapping(value = "/upload")
public class BulkUploadController {

    private final BulkUploadService bulkUploadService;

    public BulkUploadController(BulkUploadService bulkUploadService) {
        this.bulkUploadService = bulkUploadService;
    }

    @PostMapping(value = "/customers/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> validateCustomers(@RequestPart("file") MultipartFile file,
                                                  @RequestParam("uploadType")String uploadType,
                                                  @RequestParam("customerType")String customerType) {

        return bulkUploadService.validate(file, uploadType, customerType);
    }

    //uploadType : lead, prospect, customer
    //customerType : commercial, individual
    @PostMapping(value = "/customers/validateJson", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> validateCustomersJson(@RequestBody String customersJson,
                                                      @RequestParam(name = "uploadId", required = false) String uploadId,
                                                      @RequestParam("uploadType")String uploadType,
                                                      @RequestParam("customerType")String customerType) {
        return bulkUploadService.validate(customersJson, uploadId,uploadType,customerType);
    }

    //uploadType : lead, prospect, customer
    //customerType : commercial, individual
    @PostMapping(value = "/customers", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> uploadCustomers(@RequestBody UploadPayload uploadPayload,
                                                @RequestParam(name = "uploadId") String uploadId){
        return bulkUploadService.upload(uploadId,
                List.of(uploadPayload.getCorrectRowIds().split(",")).stream()
                        .map(String::trim).map(Long::parseLong).collect(Collectors.toList()),
                List.of(uploadPayload.getCorrectStagedIds().split(",")).stream()
                        .map(String::trim).map(Long::parseLong).collect(Collectors.toList()),
                uploadPayload.getCustomerType()
                );
    }

    @PostMapping(value = "/customers/status", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> showProgressPercent(@RequestBody String correctStagedIds,
                                                    @RequestParam("uploadId") String uploadId) {
        return bulkUploadService.showProgress(uploadId, List.of(correctStagedIds.split(",")).stream()
                .map(String::trim).map(Long::parseLong).collect(Collectors.toList()));
    }
    @DeleteMapping("/customer/delete")
    public BaseResponse<Object> deleteCustomer(@RequestParam("uploadId") String uploadId, @RequestParam("index") int index) {
        return bulkUploadService.deleteCustomer(uploadId , index);
    }

}
