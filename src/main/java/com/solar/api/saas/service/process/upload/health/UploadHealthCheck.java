package com.solar.api.saas.service.process.upload.health;

import com.solar.api.tenant.model.externalFile.ExternalFile;
import org.springframework.web.multipart.MultipartFile;

public interface UploadHealthCheck {

    HealthCheckResult validate(String entity, MultipartFile file, Long rateMatrixId, String action, Long projectId, ExternalFile externalFile);
}
