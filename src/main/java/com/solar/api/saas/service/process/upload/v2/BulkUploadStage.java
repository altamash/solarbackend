package com.solar.api.saas.service.process.upload.v2;

import com.solar.api.saas.service.integration.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BulkUploadStage {
    BaseResponse<Object> stage(MultipartFile file, String uploadType, String customerType);

    BaseResponse<Object> stage(String customersJson, String uploadId);
}
