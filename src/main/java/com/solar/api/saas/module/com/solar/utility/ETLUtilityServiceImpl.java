package com.solar.api.saas.module.com.solar.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.module.com.solar.batch.service.BatchDefinitionService;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.job.JobHandler;
import com.solar.api.saas.service.process.parser.product.EParserLocation;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.externalFile.ExternalFile;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.repository.DocuLibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class ETLUtilityServiceImpl implements ETLUtilityService {

    @Autowired
    @Lazy
    private BatchService batchService;

    @Value("${app.profile}")
    private String appProfile;

    @Value("${app.storage.container}")
    private String storageContainer;

    @Autowired
    private StorageService storageService;
    @Autowired
    private DocuLibraryRepository docuLibraryRepository;

    @Autowired
    @Lazy
    private BatchDefinitionService batchDefinitionService;
    @Autowired
    @Lazy
    private JobHandler jobHandler;
    @Override
    public ObjectNode etl(ExternalFile externalFile, MultipartFile file, String compKey, String mongoSubId) {
        EParserLocation eParserLocation = EParserLocation.get(externalFile.getAssociatedParser());
        if (Objects.requireNonNull(eParserLocation) == EParserLocation.BILL_CREDIT_IMPORT_ETL) {
            return billingCreditsETLInit(file, compKey, externalFile.getHeader());
        }
        if (Objects.requireNonNull(eParserLocation) == EParserLocation.PROJECTION_IMPORT_ETL_YEARLY||
                Objects.requireNonNull(eParserLocation) == EParserLocation.PROJECTION_IMPORT_ETL_MONTHLY||
                Objects.requireNonNull(eParserLocation) == EParserLocation.PROJECTION_IMPORT_ETL_QUARTERLY||
                Objects.requireNonNull(eParserLocation) == EParserLocation.PROJECTION_IMPORT_ETL_DAILY
        ) {
            return ProjectionETLInit(file, compKey, externalFile.getHeader(), mongoSubId);
        }
        return new ObjectMapper().createObjectNode().put("message", "importTypeId doesn't match any associatedParser. " +
                "Please try again with correct importTypeId");
    }

    private ObjectNode billingCreditsETLInit(MultipartFile file, String compKey, String header) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        ObjectNode response = new ObjectMapper().createObjectNode();
        JobManagerTenant jobManagerTenant = batchService.findRunningInstance(AppConstants.BILLING_CREDITS_IMPORT_JOB,
                AppConstants.RUNNING);
        if (jobManagerTenant != null) {
            response.put("message", "Billing Credits job already running");
        } else {
            BatchDefinition batchDefinition = batchService.findById(AppConstants.BILLING_CREDITS);
            int jobCommand = batchService.explorer(batchDefinition.getId());
            if (jobCommand != 0) {
                String timePath = dtf.format(now);
                long fileId = batchService.getLastJobInstanceId(AppConstants.BILLING_CREDITS_IMPORT_JOB) + 1;
                try {
                    storeFile(file, appProfile, "tenant/" + compKey + AppConstants.BILLING_CREDITS_PATH + "/" + timePath,
                            AppConstants.BILLING_CREDITS_STRING + fileId + AppConstants.MIME_TYPE_CSV,
                            Long.valueOf(compKey));
                    batchService.BillingCreditsJob(compKey, AppConstants.BILLING_CREDITS_STRING + fileId + AppConstants.MIME_TYPE_CSV, header,timePath);
                    response.put("message", "Your file is being processed. It may take several minutes.");
                } catch (Exception e) {
                    response.put("exception", e.getMessage());
                }
            } else {
                response.put("message", "There is already an active running instance of this job, Please try " +
                        "again later");
            }
        }
        return response;
    }

    private void storeFile(MultipartFile file, String container, String directory, String fileName, Long compKey)
            throws URISyntaxException, IOException, StorageException {
        storageService.storeInContainer(file, container, directory, fileName, compKey, false);
    }

    private ObjectNode ProjectionETLInit(MultipartFile file, String compKey, String header, String mongoSubId) {

        ObjectNode response = new ObjectMapper().createObjectNode();
        JobManagerTenant jobManagerTenant = batchService.findRunningInstance(AppConstants.PROJECTION_IMPORT_JOB+"_"+mongoSubId,
                AppConstants.RUNNING);
        if (jobManagerTenant != null) {
            response.put("message", "Projection job already running");
        } else {
            BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.PROJECTION_IMPORT);
            int jobCommand = batchService.explorer(batchDefinition.getId());
            if (jobCommand != 0) {
                long fileId = jobHandler.getLastJobInstanceIdByJobName(AppConstants.PROJECTION_IMPORT_JOB) + 1;
                try {
                    String fileName = AppConstants.PROJECTION_STRING +fileId+"_"+ mongoSubId + AppConstants.MIME_TYPE_CSV;
                    storeFile(file, storageContainer, "tenant/" + compKey + AppConstants.PROJECTION_PATH,fileName,
                            Long.valueOf(compKey),mongoSubId);
                    batchService.ProjectionJob(compKey, fileName, header,mongoSubId);
                    response.put("message", "Your file is being processed. It may take several minutes.");
                } catch (Exception e) {
                    response.put("exception", e.getMessage());
                }
            } else {
                response.put("message", "There is already an active running instance of this job, Please try " +
                        "again later");
            }
        }
        return response;
    }

    private void storeFile(MultipartFile file, String container, String directory, String fileName, Long compKey, String mongoSubId)
            throws URISyntaxException, IOException, StorageException {
       String path = storageService.storeInContainer(file, container, directory, fileName, compKey, false);
       docuLibraryRepository.save(DocuLibrary.builder().docuName(fileName).codeRefType("PROJECTION").compKey(compKey)
               .notes("projection csv file").format("csv").uri(path).codeRefId(mongoSubId).build());
    }
}