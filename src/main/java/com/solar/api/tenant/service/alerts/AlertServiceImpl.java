package com.solar.api.tenant.service.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.solar.api.Constants;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.tenant.model.AlertLog;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.repository.AlertLogRepository;
import com.solar.api.tenant.repository.TenantConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static com.solar.api.Constants.ALERTS.*;

@Service
public class AlertServiceImpl implements AlertService {

    protected final Logger LOGGER = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Value("${app.solarAmpsBaseUrl}")
    private String solarAmpsBaseUrl;

    private final String PLACEHOLDER_VALUES_JSON = "placeholderValuesJson";

    private final MasterTenantService masterTenantService;
    private final AlertLogRepository alertLogRepository;
    private final TenantConfigRepository tenantConfigRepository;

    public AlertServiceImpl(MasterTenantService masterTenantService, AlertLogRepository alertLogRepository,
                            TenantConfigRepository tenantConfigRepository) {
        this.masterTenantService = masterTenantService;
        this.alertLogRepository = alertLogRepository;
        this.tenantConfigRepository = tenantConfigRepository;
    }

    @Override
    public BaseResponse superSendEmailTrigger(TenantConfig tenantConfig, String subject, String emailToList,
                                              String emailCCList, String emailBCCList, Map<String, String> json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return superSendEmailTrigger(tenantConfig, subject, emailToList, emailCCList, emailBCCList,
                    mapper.writeValueAsString(json));
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseResponse superSendEmailTrigger(TenantConfig tenantConfig, String subject, String emailToList,
                                              String emailCCList, String emailBCCList, String placeHolderJSON) {
        enteringLogs(tenantConfig.getId(), subject, emailToList, emailCCList, emailBCCList, placeHolderJSON);
        MultiValueMap<String, Object> placeholderValuesJson = null;
        try {
            String url = getSuperSendURL(tenantConfig.getId(), subject, emailToList, emailCCList, emailBCCList);
            placeholderValuesJson = getJson(placeHolderJSON);
            ResponseEntity<BaseResponse> baseResponse = WebUtils.submitRequestFormData(HttpMethod.POST, url,
                    placeholderValuesJson, getHeaders(), BaseResponse.class);
            getExitingLogs(tenantConfig.getId(), subject,
                    emailToList, emailCCList, emailBCCList, placeHolderJSON, baseResponse.getStatusCode());
            savePlaceholderValuesJson(tenantConfig, "SENT", subject, emailToList, emailCCList, emailBCCList, placeholderValuesJson);
            return baseResponse.getBody();
        } catch (Exception e) {
            getExceptionLogs(e.getMessage(), tenantConfig.getId(), subject,
                    emailToList, emailCCList, emailBCCList, placeHolderJSON);
            try {
                savePlaceholderValuesJson(tenantConfig, e.getMessage(), subject, emailToList, emailCCList, emailBCCList, placeholderValuesJson);
            } catch (JsonProcessingException jpe) {
                LOGGER.error(jpe.getMessage(), jpe);
            }
        }
        return null;
    }

    private void savePlaceholderValuesJson(TenantConfig tenantConfig, String status, String subject, String emailsTo, String emailCCs,
                                           String emailBCCs, MultiValueMap<String, Object> placeholderValuesJson) throws JsonProcessingException {
        Map<String, String> recipient = new HashMap<>();
        recipient.put("to", emailsTo.replaceAll("tos=", "").replaceAll("&", ", "));
        recipient.put("cc", emailCCs != null ? emailCCs.replaceAll("ccs=", "").replaceAll("&", ", ") : null);
        recipient.put("bcc", emailBCCs != null ? emailBCCs.replaceAll("bccs=", "").replaceAll("&", ", ") : null);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        alertLogRepository.save(AlertLog.builder()
                .tenantConfig(tenantConfig)
                .status(status.length() > 255 ? status.substring(0, 255) : status)
                .subject(subject)
                .recipients(writer.writeValueAsString(recipient))
                .valuesJson(placeholderValuesJson.get(PLACEHOLDER_VALUES_JSON) != null ?
                        (String) placeholderValuesJson.get(PLACEHOLDER_VALUES_JSON).get(0) : null)
                .build());
    }

    public MultiValueMap<String, Object> getJson(String placeHolderJSON) throws JsonProcessingException {
        MultiValueMap<String, Object> json = new LinkedMultiValueMap<>();
        json.add(PLACEHOLDER_VALUES_JSON, placeHolderJSON);
        return json;
    }

    private Map<String, List<String>> getHeaders() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Comp-Key", Arrays.asList(String.valueOf(masterTenantService.findByDbName(DBContextHolder.getTenantName()).getCompanyKey())));
        return headers;
    }

    private String getSuperSendURL(Long tenantConfigId, String subject, String tos, String ccs, String bcc) {
        Optional<TenantConfig> brandId = tenantConfigRepository.findByParameter(Constants.WEATHER_API.BrandId);
        String finalSubject = subject.isEmpty() ? "" : "&" + "subject=" + subject + "&";
        String finalCCs = ccs.isEmpty() ? "" : ccs + "&";
        String finalBCCs = bcc.isEmpty() ? "" : bcc + "&";
        return solarAmpsBaseUrl
                + SOLARAMP_QUEUE_API + "?"
                + "tenantConfigId=" + tenantConfigId
                + finalSubject
                + tos
                + finalCCs
                + finalBCCs
                + (brandId.isPresent() ? "&brandId=" + brandId.get().getText() : "");
    }

    private void getExceptionLogs(String message, Long id, String subject, String array, String array1, String array2, String placeHolderJSON) {
        LOGGER.error("EXCEPTION {} in superSendEmailTrigger() with tenantConfigId {}, subject {}, emailToList {}, " +
                        "emailCCList {}, emailBCCList {}, placeHolderJSON {}", message, id, subject,
                array, array1, array2, placeHolderJSON);
    }

    private void getExitingLogs(Long id, String subject, String array, String array1, String array2, String placeHolderJSON, HttpStatus statusCode) {
        LOGGER.info("Exiting superSendEmailTrigger() with tenantConfigId {}, subject {}, emailToList {}, " +
                        "emailCCList {}, emailBCCList {}, placeHolderJSON {} || response status {}", id, subject,
                array, array1, array2, placeHolderJSON, statusCode);
    }

    private void enteringLogs(Long id, String subject, String array, String array1, String array2, String placeHolderJSON) {
        LOGGER.info("Entering superSendEmailTrigger() with tenantConfigId {}, subject {}, emailToList {}, " +
                        "emailCCList {}, emailBCCList {}, placeHolderJSON {}", id, subject, array, array1, array2, placeHolderJSON);
    }
}
