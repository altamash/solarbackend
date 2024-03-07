package com.solar.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.integration.mongo.response.subscription.Date;
import com.solar.api.tenant.model.ApiAccessLog;
import com.solar.api.tenant.model.JwtRequest;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.ApiAccessLogRepository;
import com.solar.api.tenant.service.UserService;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.Optional;

import org.springframework.http.HttpHeaders;

@Profile({"dev", "stage", "preprod", "prod", "newprod"})
@Component
public class APIAccessInterceptor extends OncePerRequestFilter {
    private String sessionId;

    public void logSessionInfo(String session) {
        this.sessionId = session;
    }

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private ApiAccessLogRepository apiAccessLogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        try {
            chain.doFilter(wrappedRequest, response);
        } finally {
            String session = extractTokenAndSessionFromRequest(request);
            logAccess(wrappedRequest, response, startTime, session);
        }
    }

    private void logAccess(ContentCachingRequestWrapper request, HttpServletResponse response, long startTime, String session) throws SocketException {
        String apiAccessed = request.getMethod() + " " + WebUtils.getRequestUrlPattern();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication != null ? authentication.getName() : null;
        if ("anonymousUser".equals(userName)) {
            if ("POST /signin".equals(apiAccessed)) {
                byte[] buf = request.getContentAsByteArray();
                if (buf.length > 0) {
                    try {
                        String requestBody = new String(buf, 0, buf.length, request.getCharacterEncoding());
                        JwtRequest jwtRequest = new ObjectMapper().readValue(requestBody, JwtRequest.class);
                        userName = jwtRequest.getUserName();
                    } catch (Exception e) {
                        LOGGER.info("error in reading request body");
                    }
                } else {
                    userName = null;
                }
            } else {
                userName = null;
            }
        }


        String clientIp = WebUtils.getClientIp(request);
        String[] clientIpParts = clientIp.split(":");
        String ip = clientIpParts[0];
        String port = clientIpParts.length > 1 ? clientIpParts[1] : null;
        Long userId = null;
        if (DBContextHolder.getTenantName() == null && NumberUtils.isDigits(request.getHeader("Comp-Key"))) {
            Long compKey = Long.valueOf(request.getHeader("Comp-Key"));
            if (compKey != null) {
                MasterTenant tenant = masterTenantService.findById(compKey);
                if (tenant != null) {
                    DBContextHolder.setTenantName(tenant.getDbName());
                    DBContextHolder.setLegacy(tenant.getLegacyBilling());
                }
            }
        }
        if (DBContextHolder.getTenantName() != null) {
            User user = userService.findByUserName(userName);
            userId = user != null ? user.getAcctId() : null;
            LocalDateTime timeOfLogin = null;
            LocalDateTime logOutDateTime = null;
            Boolean forcedLogOut = false;

            if (apiAccessed.contains("signin")) {
                timeOfLogin = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
            }
            if (apiAccessed.contains("/logout/abrupt")) {
                Optional<ApiAccessLog> apiAccessLogOptional = apiAccessLogRepository
                        .findBySessionIsNotNullAndTimeOfLoginIsNotNullAndSessionOrderByTimeOfLoginDesc(session);

                if (apiAccessLogOptional.isPresent()) {
                    ApiAccessLog apiAccessLog = apiAccessLogOptional.get();
                    timeOfLogin = apiAccessLog.getTimeOfLogin();
                }
                logOutDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
                forcedLogOut = Boolean.parseBoolean(request.getHeader("abrupt"));
            }


            String browserName = getBrowserName(request);
            Version browserVersion = getBrowserVersion(request);
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String deviceMacAddress = getDeviceMacAddress();

            saveApiAccessLog(ip, port, userName, userId, request.getHeader("referer"), apiAccessed,
                    System.currentTimeMillis() - startTime, response.getStatus(),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()),
                    timeOfLogin, logOutDateTime, browserVersion, osName, osVersion, forcedLogOut,
                    deviceMacAddress, session, browserName);
        }
    }

    @Async
    void saveApiAccessLog(String ip, String port, String userName, Long userId, String referer,
                          String apiAccessed, Long time, Integer status, LocalDateTime dateTime, LocalDateTime loginTime, LocalDateTime logoutTime, Version browserVersion,
                          String os, String osVersion, Boolean forcedLogout, String deviceMacAddress, String session, String browserName) {
        apiAccessLogRepository.save(ApiAccessLog.builder()
                .clientIp(ip)
                .port(port)
                .userName(userName)
                .userId(userId)
                .referer(referer)
                .platform(WebUtils.getBaseUrl())
                .apiAccessed(apiAccessed)
                .time(time)
                .status(status)
                .accessedAt(dateTime)
                .timeOfLogin(loginTime != null ? loginTime : null)
                .session(session)
                .browserVersion(browserVersion != null ? String.valueOf(browserVersion) : null)
                .browserName(browserName != null && !browserName.equals("UNKNOWN") ? browserName : null)
                .osName(os)
                .osVersion(osVersion != null ? osVersion : null)
                .deviceMacAddress(deviceMacAddress != null ? deviceMacAddress : null)
                .logoutDateTime(logoutTime != null ? logoutTime : null).forcedLogout(forcedLogout)
                .build());
    }

    private String extractTokenAndSessionFromRequest(HttpServletRequest request) {
        try {
            // Get the Authorization header from the request
            String authorizationHeader = request.getHeader("Authorization");
            String session = null;
            // Check if the Authorization header is present and has the expected format
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // Extract the token by removing the "Bearer " prefix
                String jwt = authorizationHeader.substring(7);

                // Decode the JWT
                Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwt);

                session = (String) claims.get("session");
            }

            if (session != null) {
                return session;
            } else {
                // Use getSession(false) to get an existing session or null if it doesn't exist
                HttpSession sessionid = request.getSession(false);
                // Retrieve the session ID from the session
                String storedSessionId = (String) sessionid.getAttribute("sessionId");

                return storedSessionId;
            }
        } catch (Exception exception) {
            // Handle the case when the Authorization header or token is not found
            return null;
        }
    }


    private String getBrowserName(HttpServletRequest request) {
        String userAgentHeader = request.getHeader(HttpHeaders.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);
        return String.valueOf(userAgent.getBrowser());
    }

    private Version getBrowserVersion(HttpServletRequest request) {
        String userAgentHeader = request.getHeader(HttpHeaders.USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);
        return userAgent.getBrowserVersion();
    }

    private String getDeviceMacAddress() {
        String deviceMacAddress = "";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                byte[] hardwareAddress = ni.getHardwareAddress();

                if (hardwareAddress != null) {
                    String[] hexadecimalFormat = new String[hardwareAddress.length];

                    for (int i = 0; i < hardwareAddress.length; i++) {
                        hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
                    }

                    deviceMacAddress = String.join("-", hexadecimalFormat);
                    break; // Stop iterating after the first non-null MAC address
                }
            }
        } catch (SocketException e) {
            // Handle the exception as needed
            e.printStackTrace();
        }
        return deviceMacAddress;
    }

}
