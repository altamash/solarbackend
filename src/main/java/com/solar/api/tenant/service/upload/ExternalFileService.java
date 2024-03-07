package com.solar.api.tenant.service.upload;

import com.solar.api.saas.service.process.upload.UploadResponse;
import com.solar.api.saas.service.process.upload.health.HealthCheckResult;
import com.solar.api.tenant.model.externalFile.ExternalFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ExternalFileService {

    public List<ExternalFile> getAllExternalFiles();

    List<ExternalFile> findByImportType(String importType);

    public ExternalFile saveOrUpdate(ExternalFile externalFile);

    public ExternalFile getFileName(ExternalFile externalFile);

    public ExternalFile getHeader(String header);

    public ExternalFile findByHeader(String Header);

    public ExternalFile findAssociatedParser(String associatedParser);

    ExternalFile findByImportTypeId(Long import_id);

    UploadResponse parseWithImportTypeId(Long importTypeId, MultipartFile file, List<Long> correctRowId, Long rateMatrixId, Long assetId,
                    String action, Long projectId, Boolean isLegacy) throws IOException, ClassNotFoundException;

    HealthCheckResult bulkUploadHealthCheck(Long importTypeId, MultipartFile file, Long subscriptionRateMatrixId, Long registerId,
                                            String action, Long assetId, Long projectId) throws IOException;

}
