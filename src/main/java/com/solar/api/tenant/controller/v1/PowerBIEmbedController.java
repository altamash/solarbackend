// ----------------------------------------------------------------------------
// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.
// ----------------------------------------------------------------------------

package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.service.reporting.powerBI.AzureADService;
import com.solar.api.saas.service.reporting.powerBI.PowerBIService;
import com.solar.api.saas.service.reporting.powerBI.ReportingService;
import com.solar.api.tenant.model.powerBI.EmbedConfig;
import com.solar.api.tenant.model.powerBI.ExportToBody;
import com.solar.api.tenant.model.powerBI.ExportToResponse;
import com.solar.api.tenant.model.powerBI.ReportConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PowerBIEmbedController")
@RequestMapping(value = "/report")
public class PowerBIEmbedController {

    static final Logger logger = LoggerFactory.getLogger(PowerBIEmbedController.class);

    @Autowired
    private ReportingService reportingService;

    /**
     * Home page controller
     *
     * @return Homepage (jsp)
     */
    @GetMapping(path = "/")
    public ModelAndView embedReportHome() {

        // Return homepage JSP view
        return new ModelAndView("EmbedReport");
    }

    /**
     * Embedding details controller
     *
     * @param workspaceId
     * @param reportId
     * @return ResponseEntity<String> body contains the JSON object with embedUrl and embedToken
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws JSONException
     */
    @GetMapping(path = "/getembedinfo/groups/{workspaceId}/reports/{reportId}")
    @ResponseBody
    public ResponseEntity<String> embedInfoController(@PathVariable String workspaceId,
                                                      @PathVariable String reportId) throws JsonMappingException,
            JsonProcessingException, JSONException {

        // Get access token
        String accessToken;
        try {
            accessToken = AzureADService.getAccessToken();
        } catch (ExecutionException | MalformedURLException | RuntimeException ex) {
            // Log error message
            logger.error(ex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());

        } catch (InterruptedException interruptedEx) {
            // Log error message
            logger.error(interruptedEx.getMessage());

            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(interruptedEx.getMessage());
        }

        // Get required values for embedding the report
        try {

            // Get report details
            EmbedConfig reportEmbedConfig = PowerBIService.getEmbedConfig(accessToken, workspaceId,
                    reportId);

            // Convert ArrayList of EmbedReport objects to JSON Array
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < reportEmbedConfig.embedReports.size(); i++) {
                jsonArray.put(reportEmbedConfig.embedReports.get(i).getJSONObject());
            }

            // Return JSON response in string
			JSONObject responseObj = new JSONObject();
//            ObjectNode responseObj = new ObjectMapper().createObjectNode();
            responseObj.put("embedToken", reportEmbedConfig.embedToken.token);
            responseObj.put("embedReports", jsonArray);
            responseObj.put("tokenExpiry", reportEmbedConfig.embedToken.expiration);

//            responseObj.put("accessToken", accessToken);

            String response = responseObj.toString();
            return ResponseEntity.ok(response);

        } catch (HttpClientErrorException hcex) {
            // Build the error message
            StringBuilder errMsgStringBuilder = new StringBuilder("Error: ");
            errMsgStringBuilder.append(hcex.getMessage());

            // Get Request Id
            HttpHeaders header = hcex.getResponseHeaders();
            List<String> requestIds = header.get("requestId");
            if (requestIds != null) {
                for (String requestId : requestIds) {
                    errMsgStringBuilder.append("\nRequest Id: ");
                    errMsgStringBuilder.append(requestId);
                }
            }

            // Error message string to be returned
            String errMsg = errMsgStringBuilder.toString();

            // Log error message
            logger.error(errMsg);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errMsg);

        } catch (RuntimeException rex) {
            // Log error message
            logger.error(rex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rex.getMessage());
        }
    }

    @PostMapping("/groups/{workspaceId}/reports/{reportId}/exportTo")
    @ResponseBody
    public ExportToResponse exportToFile(@RequestBody ExportToBody exportToBody, @PathVariable String workspaceId,
                                         @PathVariable String reportId)
            throws InterruptedException, ExecutionException, MalformedURLException {
        return reportingService.exportToFile(exportToBody, workspaceId, reportId);
    }

    @GetMapping("/groups/{workspaceId}/reports/{reportId}/exports/{exportId}/file")
    public ResponseEntity<byte[]> getFileOfExportToFile(@PathVariable String workspaceId,
                                                        @PathVariable String reportId, @PathVariable String exportId)
            throws InterruptedException, ExecutionException, MalformedURLException {
        byte[] result = reportingService.getFileOfExportToFile(workspaceId, reportId, exportId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @GetMapping("/embedurl/groups/{workspaceId}/reports/{reportId}")
    public ObjectNode getEmbedConfig(@PathVariable String workspaceId, @PathVariable String reportId) throws InterruptedException, ExecutionException, MalformedURLException, JSONException, JsonProcessingException {
        ObjectNode message = new ObjectMapper().createObjectNode();
        List<ReportConfig> embedReports =
                reportingService.getEmbedConfig(workspaceId, reportId).embedReports;
        if (!embedReports.isEmpty()) {
            message.put("embed_url", embedReports.get(0).embedUrl + "&autoAuth=true");
        }
        return message;
    }

    //By Garden
    @GetMapping("/generatePBReport/type/{type}/garden/{subscriptionRateMatrixId}/subscription/{subscriptionId}")
    public ObjectNode generatePBReport(@PathVariable String type, @PathVariable Long subscriptionRateMatrixId,
                                       @PathVariable Long subscriptionId)
            throws InterruptedException, ExecutionException, IOException, URISyntaxException, StorageException {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if (type != null && subscriptionRateMatrixId != null && subscriptionId != null) {
            reportingService.generatePBReport(type, subscriptionRateMatrixId, subscriptionId);
            objectNode.put("message", "TrueUps Generation is in progress");
        } else {
            objectNode.put("error", "Invalid Parameters");
        }
        return objectNode;
    }

//	//Individual
//	@GetMapping("/generatePBReport/{type}/{subscriptionRateMatrixId}/{subscriptionId}")
//	public ResponseEntity<byte[]> generatePBReport(@PathVariable String type, @PathVariable Long subscriptionId)
//			throws InterruptedException, ExecutionException, IOException, URISyntaxException, StorageException {
//		byte[] result = reportingService.generatePBReport(type, subscriptionId);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_PDF);
//		return new ResponseEntity<>(result, headers, HttpStatus.OK);
//	}
}