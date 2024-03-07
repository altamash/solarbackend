package com.solar.api.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.saas.controller.v1.SAASAuthController;
import com.solar.api.saas.module.com.solar.batch.service.SolrenviewServiceImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WebUtils.class);

    private final static String LOCALHOST_IPV4 = "127.0.0.1";
    private final static String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        if (!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }

    public static String getRequestMethod() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getMethod();
    }

    public static String getRequestUrlPattern() {
        return (String) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
    }

    public static String getBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    public static String getControllerName() {
        return ((HandlerMethod) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).getBeanType().getSimpleName();
    }

    public static <T> ResponseEntity<T> submitRequest(HttpMethod httpMethod, String url, Object requestBody, Map<String,
            List<String>> headers, Class responseType, Object... uriVariables) {
        LOGGER.info("Entering submitRequest: {}", url);
        // Request header
        HttpHeaders reqHeader = new HttpHeaders();
        headers.entrySet().forEach(header -> reqHeader.put(header.getKey(), header.getValue()));
        // HTTP entity object - holds header and body
        HttpEntity<String> reqEntity;
        if (requestBody != null) {
            reqEntity = new HttpEntity(requestBody, reqHeader);
        } else {
            reqEntity = new HttpEntity<>(reqHeader);
        }
        RestTemplate restTemplate = new RestTemplate();
        LOGGER.info("Entering submitRequest with parameters: {}, {}, {}, {}, {}", url, httpMethod, reqEntity, responseType,
                uriVariables);
//        restTemplate.exchange(url, httpMethod, reqEntity, responseType, uriVariables);
        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, reqEntity, responseType,
                uriVariables);
        LOGGER.info("Exiting submitRequest: {}", response);
        return response;
    }

    public static <T> ResponseEntity<T> submitRequestFormData(HttpMethod httpMethod, String url, Object requestBody, Map<String,
            List<String>> headers, Class responseType, Object... uriVariables) {
        LOGGER.info("Entering submitRequest: {}", url);
        // Request header
        HttpHeaders reqHeader = new HttpHeaders();
        headers.entrySet().forEach(header -> reqHeader.put(header.getKey(), header.getValue()));
        reqHeader.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        reqHeader.set("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
        // HTTP entity object - holds header and body
        HttpEntity<String> reqEntity;
        if (requestBody != null) {
            reqEntity = new HttpEntity(requestBody, reqHeader);
        } else {
            reqEntity = new HttpEntity<>(reqHeader);
        }
        RestTemplate restTemplate = new RestTemplate();
        LOGGER.info("Entering submitRequest with parameters: {}, {}, {}, {}, {}", url, httpMethod, reqEntity, responseType,
                uriVariables);
//        restTemplate.exchange(url, httpMethod, reqEntity, responseType, uriVariables);
        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, reqEntity, responseType,
                uriVariables);
        LOGGER.info("Exiting submitRequest: {}", response);
        return response;
    }

    public static <T> ResponseEntity<T> submitFormDataRequest(HttpMethod httpMethod, String url,
                                                              MultiValueMap<String, String> request,
                                                              Map<String, List<String>> headers,
                                                              Class responseType, Object... uriVariables) {
        // Request header
        HttpHeaders reqHeader = new HttpHeaders();
        headers.entrySet().forEach(header -> reqHeader.put(header.getKey(), header.getValue()));
        // HTTP entity object - holds header and body
        HttpEntity<MultiValueMap<String, String>> reqEntity;
        if (request != null) {
            reqEntity = new HttpEntity(request, reqHeader);
        } else {
            reqEntity = new HttpEntity<>(reqHeader);
        }
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.exchange(url, httpMethod, reqEntity, responseType, uriVariables);
        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, reqEntity, responseType,
                uriVariables);
        HttpHeaders responseHeader = response.getHeaders();
        return response;
    }

    public static <T> ResponseEntity<T> submitFormDataRequest(String url, MultiValueMap<String, Object> request, Map<String,
            List<String>> headers, Class responseType) {
        HttpHeaders reqHeader = new HttpHeaders();
        headers.entrySet().forEach(header -> reqHeader.put(header.getKey(), header.getValue()));
        reqHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
//        reqHeader.set("Content-Type", "multipart/form-data");
        reqHeader.set("Content-Type", "application/pdf");
//        reqHeader.set("User-Agent", "test");
//        reqHeader.set("Accept", "text/plain");
//        reqHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<MultiValueMap<String, Object>> reqEntity;
        if (request != null) {
            reqEntity = new HttpEntity<>(request, reqHeader);
        } else {
            reqEntity = new HttpEntity<>(reqHeader);
        }
        return new RestTemplate().postForEntity(url, reqEntity, responseType);
    }

    public static HttpResponse submitFormDataRequest(String url, Map<String, String> headers,
                                                     org.apache.http.HttpEntity httpEntity,
                                                     Class responseType) throws IOException {
        LOGGER.info("Entering submitFormDataRequest: {} , Data: {}", url, httpEntity);
        HttpResponse responseEntity = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        headers.entrySet().forEach(header -> httpPost.setHeader(header.getKey(), header.getValue()));
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        System.out.println("POST Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        httpClient.close();
        LOGGER.info("Exiting submitFormDataRequest: {} , Data: {}, response {}", url, httpEntity, response);
        return HttpResponse.builder()
                .statusCode(httpResponse.getStatusLine().getStatusCode())
                .data(new ObjectMapper().readValue(response.toString(), responseType))
                .build();
    }

    @Getter
    @Setter
    @Builder
    public static class HttpResponse<T> {
        private int statusCode;
        private String reasonPhrase;
        private T data;
    }

    public static void main(String[] args) {
        List<String> controllerNames = getControllerNames(SAASAuthController.class);
        controllerNames.forEach(controllerName -> System.out.println(controllerName));
    }

    private static List getControllerNames(Class markerClass) {
        List<String> controllerNames = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(markerClass.getPackage().getName())) {
            controllerNames.add(beanDefinition.getBeanClassName());
        }
        return controllerNames;
    }
}
