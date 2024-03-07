package com.solar.api.saas.service.process.upload.project;

import com.solar.api.saas.service.process.upload.UploadResponse;
import com.solar.api.tenant.model.process.JobManagerTenant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ProjectUploadService {

    UploadResponse upload(String entity, File file, List<Long> correctRowIds, Long assetId, String action, Long projectId);

    UploadResponse uploadAssetBlockFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                           Long assetId, JobManagerTenant jobManagerTenant, String action) throws IOException, Exception;
    UploadResponse uploadProjectInventoryFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                           Long assetId, JobManagerTenant jobManagerTenant, Long projectId) throws Exception;


}
