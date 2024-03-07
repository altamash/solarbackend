package com.solar.api.saas.service.reporting.powerBI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.powerBI.EmbedConfig;
import com.solar.api.tenant.model.powerBI.ExportToBody;
import com.solar.api.tenant.model.powerBI.ExportToResponse;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public interface ReportingService {

    String test();

    /**
     * https://docs.microsoft.com/en-us/rest/api/power-bi/reports/exporttofile
     * https://api.powerbi.com/v1.0/myorg/groups/{workspaceId}/reports/{reportId}/ExportTo
     * {
     * "format": "PDF",
     * "PowerBIReportConfiguration" : {
     * "reportLevelFilters": [
     * {
     * "filter": "billing_head/invoice_id eq 7784"
     * }
     * ]
     * }
     * }
     *
     * @param exportToBody
     * @param workspaceId
     * @param reportId
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws MalformedURLException
     */
    ExportToResponse exportToFile(ExportToBody exportToBody, String workspaceId, String reportId) throws InterruptedException, ExecutionException, MalformedURLException;

    /**
     * https://docs.microsoft.com/en-us/rest/api/power-bi/reports/getfileofexporttofile
     * https://api.powerbi.com/v1.0/myorg/groups/{workspaceId}/reports/{reportId}/exports/{exportId}/file
     *
     * @param workspaceId
     * @param reportId
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws MalformedURLException
     */
    byte[] getFileOfExportToFile(String workspaceId, String reportId, String exportId) throws InterruptedException,
            ExecutionException,
            MalformedURLException;

    EmbedConfig getEmbedConfig(String workspaceId, String reportId,
                               String... additionalDatasetIds) throws JsonProcessingException, JSONException,
            InterruptedException, ExecutionException, MalformedURLException;

    /**
     * @param type
     * @param subscriptionRateMatrixId
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     * @throws URISyntaxException
     * @throws StorageException
     */
    void generatePBReport(String type, Long subscriptionRateMatrixId, Long subscriptionId) throws InterruptedException, ExecutionException, IOException, URISyntaxException, StorageException;

    String generatePBInvoiceReport(BillingHead billingHead, String premiseNumber);

//    byte[] generatePBReport(String type, Long subscriptionId) throws InterruptedException, ExecutionException,
//    IOException, URISyntaxException, StorageException;

}
