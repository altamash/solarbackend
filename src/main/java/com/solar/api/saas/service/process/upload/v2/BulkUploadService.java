package com.solar.api.saas.service.process.upload.v2;

import com.solar.api.saas.service.integration.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BulkUploadService extends BulkUploadStage {

    String FINAL_DEFAULT_EMAIL = "";

    BaseResponse<Object> validate(MultipartFile file, String uploadType, String customerType);

    BaseResponse<Object> validate(String customersJson, String uploadId, String uploadType, String customerType);

    BaseResponse<Object> upload(String uploadId, List<Long> correctRowIds, List<Long> correctStagedIds, String customerType);

    String getMongoTemplate(String customerType) throws Exception;

    BaseResponse<Object> showProgress(String uploadId, List<Long> correctStagedIds);

    BaseResponse<Object> deleteCustomer(String uploadId, int index);
}
