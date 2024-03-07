package com.solar.api.saas.service.integration.docuSign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.service.integration.docuSign.dto.request.TemplateData;
import com.solar.api.saas.service.integration.docuSign.dto.response.AccessTokenResponse;
import com.solar.api.saas.service.integration.docuSign.dto.response.CreateTemplateResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ApplicationScope
@Component
public class DataExchangeDocuSign {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.docuSignRefreshToken}")
    private String refreshToken;
    private static String AUTH_PREFIX = "Zoho-oauthtoken ";
    private static final String ACCESS_TOKEN_URL = "https://accounts.zoho.com/oauth/v2/token?refresh_token={refresh_token}" +
            "&client_id={client_id}&client_secret={client_secret}&redirect_uri={redirect_uri}&grant_type={grant_type}";
    private static final String CLIENT_ID = "1000.LQWLW78DS4EFV682AVXHS19BSCQ4VH";
    private static final String CLIENT_SECRET = "c2df0149817bc824d2ea1856e5ac7b9ab7014c51e3";
    private static final String REDIRECT_URI = "https%3A%2F%2Fsign.zoho.com";
    private static final String GRANT_TYPE = "refresh_token";
    private static final String BASE_URL = "https://sign.zoho.com/api/v1";
    private static final String TEMPLATES_URL = BASE_URL + "/templates";
    private static final String CREATE_DOCUMENT_URL = TEMPLATES_URL + "/%s/createdocument";
    private static final String REQUESTS_URL = BASE_URL + "/requests";
    private static final String GET_DOCUMENT_DETAILS_URL = REQUESTS_URL + "/%s";
    private static final String GET_DOCUMENT_PDF_URL = GET_DOCUMENT_DETAILS_URL + "/pdf";

    public AccessTokenResponse getAccessTokenViaRefreshToken() {
        AccessTokenResponse accessTokenResponse = null;
        try {
            ResponseEntity<AccessTokenResponse> response =
            WebUtils.submitRequest(HttpMethod.POST, ACCESS_TOKEN_URL, null, new HashMap<>(), AccessTokenResponse.class,
                    refreshToken, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, GRANT_TYPE);
            accessTokenResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return accessTokenResponse;
    }

    public CreateTemplateResponse createTemplate(MultipartFile file, TemplateData data, String auth) {
        WebUtils.HttpResponse<CreateTemplateResponse> response = null;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", AUTH_PREFIX + auth);
            Map<String, String> textData = new HashMap<>();
            textData.put("data", new ObjectMapper().writeValueAsString(data));
            Map<String, MultipartFile> files = new HashMap<>();
            files.put("file", file);
            response = WebUtils.submitFormDataRequest(TEMPLATES_URL, headers, getHttpEntity(textData, files),
                    CreateTemplateResponse.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response.getData();
    }

    public CreateTemplateResponse createDocument(String templateId, TemplateData data, String auth) {
        CreateTemplateResponse templateResponse = null;
        try {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("data", new ObjectMapper().writeValueAsString(data));
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Authorization", Arrays.asList(AUTH_PREFIX + auth));
            ResponseEntity<CreateTemplateResponse> response =
                    WebUtils.submitFormDataRequest(HttpMethod.POST, String.format(CREATE_DOCUMENT_URL, templateId), map, headers, CreateTemplateResponse.class);
            templateResponse = response.getBody();
        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
            templateResponse = CreateTemplateResponse.builder()
                    .message(e.getMessage())
                    .code(400)
                    .build();
        }
        return templateResponse;
    }

    public CreateTemplateResponse getDocumentDetails(String requestId, String auth) {
        CreateTemplateResponse templateResponse = null;
        try {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Authorization", Arrays.asList(AUTH_PREFIX + auth));
            ResponseEntity<CreateTemplateResponse> response =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(GET_DOCUMENT_DETAILS_URL, requestId),
                            null, headers, CreateTemplateResponse.class);
            templateResponse = response.getBody();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return templateResponse;
    }

    public byte[] getDocumentPDF(String requestId, String auth) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Arrays.asList(AUTH_PREFIX + auth));
        headers.put("Content-Type", Arrays.asList("application/pdf;charset=UTF-8"));
        ResponseEntity<byte[]> response = null;
            response = WebUtils.submitRequest(HttpMethod.GET, String.format(GET_DOCUMENT_PDF_URL, requestId),
                    null, headers, byte[].class);
        return response.getBody();
    }

    public static void main(String[] a) throws IOException {
        DataExchangeDocuSign dataExchange = new DataExchangeDocuSign();
        String accessToken = dataExchange.getAccessTokenViaRefreshToken().getAccessToken();
//        dataExchange.multipartpost(accessToken);
//        CreateTemplateResponse createTemplateResponse = dataExchange.getDocumentDetails("286676000000091689", "Zoho-oauthtoken "+ accessToken);
//        System.out.println(createTemplateResponse);
        dataExchange.byteToFile(dataExchange.getDocumentPDF("286676000000091641", accessToken));
    }

    public void multipartpost(String accessToken) {
        String data = "{" +
                "  \"templates\": {" +
                "    \"template_name\": \"CustAcqTemplate_solar6\"," +
                "    \"expiration_days\": 1," +
                "    \"is_sequential\": true," +
                "    \"reminder_period\": 8," +
                "    \"email_reminders\": false," +
                "    \"actions\": [" +
                "      {" +
                "        \"action_type\": \"SIGN\"," +
                "        \"signing_order\": 0," +
                "        \"recipient_name\": \"\"," +
                "        \"role\": \"1\"," +
                "        \"recipient_email\": \"\"," +
                "        \"recipient_phonenumber\": \"\"," +
                "        \"recipient_countrycode\": \"\"," +
                "        \"private_notes\": \"Please get back to us for further queries\"," +
                "        \"verify_recipient\": true," +
                "        \"verification_type\": \"EMAIL\"," +
                "        \"verification_code\": \"\"" +
                "      }" +
                "    ]" +
                "  }" +
                "}";

        WebUtils.HttpResponse response = null;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization","Zoho-oauthtoken "+ accessToken);
            Map<String, String> textData = new HashMap<>();
            textData.put("data", data);
            Map<String, MultipartFile> files = new HashMap<>();
            File file = new File("C:\\workspace\\SolarInformatics\\solarbackend\\src\\main\\resources\\withzohoSigntags_Mutual.pdf");
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "application/pdf", IOUtils.toByteArray(input));
            files.put("file", multipartFile);
            response = WebUtils.submitFormDataRequest("https://sign.zoho.com/api/v1/templates", headers,
                    getHttpEntity(textData, files), CreateTemplateResponse.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        System.out.println(response.getData());
    }

    private HttpEntity getHttpEntity(Map<String, String> textData, Map<String, MultipartFile> files) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        textData.entrySet().forEach(data -> builder.addTextBody(data.getKey(), data.getValue(), ContentType.TEXT_PLAIN));
        files.entrySet().forEach(file -> {
            try {
                builder.addBinaryBody(file.getKey(),
                        file.getValue().getInputStream(),
                        ContentType.APPLICATION_OCTET_STREAM,
                        file.getValue().getOriginalFilename());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return builder.build();
    }

    private void byteToFile(byte[] bytes) {
        try {
            FileUtils.writeByteArrayToFile(new File(
                        "C:\\workspace\\SolarInformatics\\solarbackend\\src\\main\\resources\\file.pdf"), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
