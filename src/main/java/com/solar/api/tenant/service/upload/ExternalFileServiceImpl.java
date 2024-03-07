package com.solar.api.tenant.service.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.service.process.migration.EMigrationParserLocation;
import com.solar.api.saas.service.process.migration.MigrationService;
import com.solar.api.saas.service.process.upload.BulkUploadService;
import com.solar.api.saas.service.process.upload.UploadResponse;
import com.solar.api.saas.service.process.upload.health.HealthCheckResult;
import com.solar.api.saas.service.process.upload.health.ProjectUploadHealthCheck;
import com.solar.api.saas.service.process.upload.health.UploadHealthCheck;
import com.solar.api.saas.service.process.upload.project.ProjectUploadService;
import com.solar.api.saas.service.process.upload.v2.customer.BulkUploadCustomersService;
import com.solar.api.tenant.model.externalFile.ExternalFile;
import com.solar.api.tenant.repository.ExternalFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class ExternalFileServiceImpl implements ExternalFileService {

    @Autowired
    ExternalFileRepository externalFileRepository;
    @Autowired
    private UploadParserFactory uploadParserFactory;
    @Autowired
    private UploadHealthCheck uploadHealthCheck;
    @Autowired
    private ProjectUploadHealthCheck projectUploadHealthCheck;

    @Override
    public ExternalFile saveOrUpdate(ExternalFile externalFile) {
        return externalFileRepository.save(externalFile);
    }

    @Override
    public ExternalFile getFileName(ExternalFile externalFile) {
        return null;
    }

    @Override
    public ExternalFile getHeader(String header) {
        return externalFileRepository.findByHeader(header);
    }

    @Override
    public List<ExternalFile> getAllExternalFiles() {
        return externalFileRepository.findAll();
    }

    @Override
    public List<ExternalFile> findByImportType(String importType) {
        return externalFileRepository.findByImportType(importType);
    }

    @Override
    public ExternalFile findByHeader(String header) {
        return externalFileRepository.findByHeader(header);
    }

    @Override
    public ExternalFile findAssociatedParser(String associatedParser) {
        return externalFileRepository.findByAssociatedParser(associatedParser)
                .orElseThrow(() -> new NotFoundException(ExternalFile.class, "associatedParser", associatedParser));
    }

    @Override
    public ExternalFile findByImportTypeId(Long import_id) {
        return externalFileRepository.findByImportTypeId(import_id);
    }

    @Override
    public UploadResponse parseWithImportTypeId(Long importTypeId, MultipartFile file, List<Long> correctRowIds, Long rateMatrixId,
                                                Long assetId, String action, Long projectId, Boolean isLegacy) throws IOException, ClassNotFoundException {
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("importTypeId", importTypeId);
        requestMessage.put("fileName", file.getOriginalFilename());
//        JobManager jobManager = jobManagerService.add(EJobName.FILE_IMPORT_PAYMENTS.toString(), requestMessage,
//        EJobStatus.RUNNING.toString(), null, LOGGER);
        ExternalFile externalFile = findByImportTypeId(importTypeId);
        Object parser = uploadParserFactory.getParser(externalFile.getAssociatedParser());
        if (parser instanceof MigrationService) {
            String[] parserParams = externalFile.getParams().split(",");
            File tempFile = Files.createTempFile(null, file.getOriginalFilename()).toFile();
            tempFile.deleteOnExit();
            file.transferTo(tempFile);
            return ((MigrationService) parser).migrate(EMigrationParserLocation.getByName(parserParams[0]),
                    parserParams[1],
                    tempFile,
                    requestMessage,
                    isLegacy);
        } else if (parser instanceof BulkUploadService) {
            String[] parserParams = externalFile.getParams().split(",");
            File tempFile = Files.createTempFile(null, file.getOriginalFilename()).toFile();
            tempFile.deleteOnExit();
            file.transferTo(tempFile);
            return ((BulkUploadService) parser).upload(parserParams[1], tempFile, correctRowIds, rateMatrixId, isLegacy);
        } else if (parser instanceof ProjectUploadService) {
            String[] parserParams = externalFile.getParams().split(",");
            File tempFile = Files.createTempFile(null, file.getOriginalFilename()).toFile();
            tempFile.deleteOnExit();
            file.transferTo(tempFile);
            return ((ProjectUploadService) parser).upload(parserParams[1], tempFile,correctRowIds, assetId, action, projectId);
        } else if (parser instanceof BulkUploadCustomersService) {
            String[] parserParams = externalFile.getParams().split(",");
            File tempFile = Files.createTempFile(null, file.getOriginalFilename()).toFile();
            tempFile.deleteOnExit();
            file.transferTo(tempFile);
            return ((BulkUploadService) parser).upload(parserParams[1], tempFile, correctRowIds, rateMatrixId, isLegacy);
        }
        return null;
    }

    @Override
    public HealthCheckResult bulkUploadHealthCheck(Long importTypeId, MultipartFile file, Long subscriptionRateMatrixId, Long registerId,
                                                   String action, Long assetId, Long projectId) {
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("importTypeId", importTypeId);
        requestMessage.put("fileName", file.getOriginalFilename());
        if (subscriptionRateMatrixId != null) {
            requestMessage.put("fileName", file.getOriginalFilename());
        }
        ExternalFile externalFile = findByImportTypeId(importTypeId);
        String[] parserParams = externalFile.getParams().split(",");
        if (registerId != null && !action.equals(null)) {
            return uploadHealthCheck.validate(parserParams[1], file, registerId,action,null,externalFile);
        } else if (assetId !=null ) {
            return uploadHealthCheck.validate(parserParams[1], file, assetId,null,projectId,externalFile);
        }
        return uploadHealthCheck.validate(parserParams[1], file, subscriptionRateMatrixId,null,null,externalFile);
    }

}
