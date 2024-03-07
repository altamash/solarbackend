package com.solar.api.saas.service.reporting.powerBI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.powerBI.*;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.trueup.TrueUpService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.workspaceReportId}")
    private String workspaceReportId;
    @Value("${app.invoiceReportId}")
    private String invoiceReportId;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private TrueUpService trueUpService;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private UserService userService;
    @Autowired
    private Utility utility;
    @Autowired
    private TenantConfigService tenantConfigService;

//    https://app.powerbi.com/groups/77f174e7-788e-4fe9-9042-cf5d7c1a1ccc/reports/beebdd3c-341b-4934-9d1b-6726c16ad30e/ReportSection
//    https://api.powerbi.com/groups/null/reports/null/ExportTo
//    private final String BASE_URL = "https://api.powerbi.com";
//    private final String GROUP = "/groups/";
//    private final String REPORT = "/reports/";
//    private final String EXPORT = "/exports/";
//    private final String EXPORT_TO_FILE_URL = BASE_URL + GROUP + workspaceReportId + REPORT + invoiceReportId + "/ExportTo";
//    private final String GET_FILE_OF_EXPORT_TO_FILE_URL = BASE_URL + GROUP + REPORT + EXPORT + "/file";
//    private final String ARR_REPORT = "1fc20319-03ae-417e-90c2-9c10bd85e582";
//    private final String VOS_REPORT = "90c6006a-8893-40e1-8cfb-d576707608d5";
//    public static final String PREFIX = "TrueUp";
//    public static final String SUFFIX = ".pdf";

    private final String BASE_URL = "https://api.powerbi.com/v1.0/myorg";
    private final String GROUP = "/groups/{workspaceId}";
    private final String REPORT = "/reports/{reportId}";
    private final String EXPORT = "/exports/{exportId}";
    private final String EXPORT_TO_FILE_URL = BASE_URL + GROUP + REPORT + "/ExportTo";
    private final String GET_FILE_OF_EXPORT_TO_FILE_URL = BASE_URL + GROUP + REPORT + EXPORT + "/file";
    private final String WORKSPACE = "77f174e7-788e-4fe9-9042-cf5d7c1a1ccc";
    /**
     * True Ups
     * Report ids
     * 2021
     */
//    private final String ARR_REPORT = "1fc20319-03ae-417e-90c2-9c10bd85e582";
//    private final String VOS_REPORT = "90c6006a-8893-40e1-8cfb-d576707608d5";
    /**
     * updated on 3rd March, 2022
     * For test Sample for SAM (NOVEL)
     */
//    private final String VOS_REPORT = "37dca069-2481-45d1-ba8f-64f905d011a9";
//    private final String ARR_REPORT = "7fdbe51e-4df1-4f4b-846d-10f8afcc22c4";
    /**
     * True Ups
     * Report ids
     * 2022
     */
//    private final String ARR_REPORT = "843c020c-90d9-46a1-89c2-18a0d50f122d";
//    private final String VOS_REPORT = "ff5fdc75-2031-4942-b752-2fbcbefcefeb";
    /**
     * True Ups
     * Report ids
     * 2022
     * ec6000
     */
    private final String ARR_REPORT = "843c020c-90d9-46a1-89c2-18a0d50f122d";
    private final String VOS_REPORT = "ff5fdc75-2031-4942-b752-2fbcbefcefeb";
    public static final String PREFIX = "TrueUp";
    public static final String SUFFIX = ".pdf";

    @Override
    public String test() {
        return BASE_URL + GROUP + workspaceReportId + REPORT + invoiceReportId + "/ExportTo";
    }

    @Override
    public ExportToResponse exportToFile(ExportToBody exportToBody, String workspaceId, String reportId)
            throws InterruptedException, ExecutionException, MalformedURLException {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        headers.put("Authorization", Arrays.asList("Bearer " + AzureADService.getAccessToken()));
        headers.put("Accept", Arrays.asList("*/*"));
        ResponseEntity<ExportToResponse> response = WebUtils.submitRequest(HttpMethod.POST, EXPORT_TO_FILE_URL,
                exportToBody, headers, ExportToResponse.class, workspaceId, reportId);
        return response.getBody();
    }

    @Override
    public byte[] getFileOfExportToFile(String workspaceId, String reportId, String exportId) throws InterruptedException, ExecutionException,
            MalformedURLException {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Arrays.asList("Bearer " + AzureADService.getAccessToken()));
        ResponseEntity<byte[]> response = null;
        try {
            response = WebUtils.submitRequest(HttpMethod.GET, GET_FILE_OF_EXPORT_TO_FILE_URL, null, headers,
                    byte[].class, workspaceId, reportId, exportId);
        } catch (HttpClientErrorException e) {
            LOGGER.warn(e.getMessage());
            return e.getResponseBodyAsString().getBytes();
        }
        return response.getBody();
    }

    @Override
    public EmbedConfig getEmbedConfig(String workspaceId, String reportId,
                                      String... additionalDatasetIds) throws JsonProcessingException, JSONException,
            InterruptedException, ExecutionException, MalformedURLException {
        String accessToken = AzureADService.getAccessToken();
        return PowerBIService.getEmbedConfig(accessToken, workspaceId, reportId);
    }

    @Async
    @Override
    public void generatePBReport(String type, Long subscriptionRateMatrixId, Long subscriptionId) {
        int threadSleep = 5;
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("startTime", System.currentTimeMillis());
        requestMessage.put("type", type);
        requestMessage.put("garden_id", subscriptionRateMatrixId);
        requestMessage.put("subscription_id", subscriptionId);
        String reportId = null;
        if (type.equals(AppConstants.CSGF)) {
            reportId = ARR_REPORT;
        } else {
            reportId = VOS_REPORT;
        }
        if (subscriptionId == -1) {
            List<CustomerSubscription> customerSubscriptions =
                    subscriptionService.findActiveBySubscriptionRateMatrixId(subscriptionRateMatrixId);
            JobManagerTenant jobManagerTenant = jobManagerTenantService.add(EJobName.GENERATE_TRUE_UPS.toString(),
                    requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
            String finalReportId = reportId;
            customerSubscriptions.forEach(customerSubscription -> {
                saveTrueUp(customerSubscription, type, finalReportId, subscriptionRateMatrixId, threadSleep);
            });
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        } else {
            CustomerSubscription customerSubscription =
                    subscriptionService.findCustomerSubscriptionById(subscriptionId);
            JobManagerTenant jobManagerTenant = jobManagerTenantService.add(EJobName.GENERATE_TRUE_UPS.toString(),
                    requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
            saveTrueUp(customerSubscription, type, reportId, subscriptionRateMatrixId, threadSleep);
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        }
    }

    private void saveTrueUp(CustomerSubscription customerSubscription, String type, String reportId, long subscriptionRateMatrixId, int threadSleep) {
//        if (trueUpService.getBySubscriptionId(customerSubscription.getId()) == null) {
        if (type.equals(customerSubscription.getSubscriptionType())) {
            try {
                ExportToResponse exportToResponse = exportToFile(getExportToBody("customer_subscription/id eq " + customerSubscription.getId(), AppConstants.PDF), workspaceReportId, reportId);
                TimeUnit.SECONDS.sleep(threadSleep);
                byte[] bytes = new byte[0];
                boolean validPdf = false;
                while (!validPdf) {
                    validPdf = true;
                    try {
                        new PdfTextExtractor(new PdfReader(bytes)).getTextFromPage(1);
                    } catch (Exception e) {
                        validPdf = false;
                    }
                    TimeUnit.SECONDS.sleep(threadSleep);
                    bytes = getFileOfExportToFile(workspaceReportId, reportId, exportToResponse.getId());
                }
                MultipartFile multipartFile = new MockMultipartFile(PREFIX + SUFFIX,
                        PREFIX + SUFFIX, AppConstants.CONTENT_TYPE, bytes);
                trueUpService.saveOrUpdate(multipartFile, customerSubscription.getId(),
                        subscriptionRateMatrixId, type);
            } catch (InterruptedException | ExecutionException | StorageException | URISyntaxException |
                     IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
//        }
    }

    private ExportToBody getExportToBody(String filter, String format) {
        ExportToReportLevelFilter exportToReportLevelFilters = new ExportToReportLevelFilter();
        exportToReportLevelFilters.setFilter(filter);
        List<ExportToReportLevelFilter> filters = new ArrayList<>();
        filters.add(exportToReportLevelFilters);
        ExportToPowerBIReportConfiguration exportToPowerBIReportConfiguration =
                new ExportToPowerBIReportConfiguration();
        exportToPowerBIReportConfiguration.setReportLevelFilters(filters);
        ExportToBody exportToBody = new ExportToBody();
        exportToBody.setFormat(format);
        exportToBody.setPowerBIReportConfiguration(exportToPowerBIReportConfiguration);
        return exportToBody;
    }

    @Override
    public String generatePBInvoiceReport(BillingHead billingHead, String premiseNumber) {
        int threadSleep = 0;
        try {
            threadSleep = Integer.parseInt(tenantConfigService.findByParameter(AppConstants.PDFGeneratePowerBiDelay).get().getText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            /*"filter": "customer_invoice/customer_no eq '1002-1001-304533298' and customer_invoice/BillingMonthFormatted eq 'Apr-20'"*/
            String reportId = getInvoiceId(billingHead.getSubscriptionId());
            String customerNumber = billingHead.getUserAccountId() + "-" + billingHead.getSubscriptionId() + "-" + premiseNumber;
            String billingMonthFormatted = new SimpleDateFormat("MMM-yy").format(new SimpleDateFormat("MM-yyyy").parse(billingHead.getBillingMonthYear()));
            ExportToResponse exportToResponse = exportToFile(
                    getExportToBody("customer_invoice/customer_no eq '" + customerNumber +
                            "' and customer_invoice/BillingMonthFormatted eq '" + billingMonthFormatted + "'", AppConstants.PDF),
                    workspaceReportId, reportId);

            TimeUnit.SECONDS.sleep(threadSleep);

            byte[] bytes = new byte[0];
            boolean validPdf = false;
            while (!validPdf) {
                validPdf = true;
                try {
                    new PdfTextExtractor(new PdfReader(bytes)).getTextFromPage(1);
                } catch (Exception e) {
                    validPdf = false;
                }
                TimeUnit.SECONDS.sleep(5);
                bytes = getFileOfExportToFile(workspaceReportId, reportId, exportToResponse.getId());
            }
            String url = Utility.uploadToStorage(storageService, bytes, appProfile, "tenant/" + utility.getCompKey() +
                    "/report/invoice/output_format/pdf", billingHead.getInvoice().getId()
                    + " - " + userService.findById(billingHead.getUserAccountId()).getFirstName().trim()
                    + " " + userService.findById(billingHead.getUserAccountId()).getLastName().trim()
                    + ", " + billingHead.getBillingMonthYear() + ".pdf", utility.getCompKey(), true);
            return url;
        } catch (InterruptedException | ExecutionException | IOException | ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Get report IDs dynamically
     *
     * @param subscriptionId
     * @return
     */
    private String getInvoiceId(Long subscriptionId) {
        return portalAttributeSAASService.findByAttributeValue(subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription(AppConstants.INVOICE_REPORT_ID
                , subscriptionService.findCustomerSubscriptionById(subscriptionId)).getValue()).getSystemValue();
    }

}
