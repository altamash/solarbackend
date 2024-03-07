package com.solar.api.configuration;


import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.process.encryption.PEMUtils;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.*;
import java.util.function.Function;


@Component
public class JwtTokenUtil implements Serializable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = -2550185165626007488L;
    @Autowired
    private UserDetailsService userDetailsService;

    //    @Autowired
//    private SystemAttributeService systemAttributeService;
    @Autowired
    private PEMUtils pemUtils;
    @Autowired
    private MasterTenantService masterTenantService;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getTenantClientIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(JwtConstants.SIGNING_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails, String tenantId, Integer tenantTier, String session) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user", userDetails);
        String privateKeyPEMString = pemUtils.getPEMString("RSA", PrivateKey.class);
        if (!StringUtils.isEmpty(privateKeyPEMString)) {
            claims.put("ppk", Base64.getEncoder().encodeToString(privateKeyPEMString.substring(0, 10).getBytes()));
        }
        claims.put("tier", tenantTier);
        return doGenerateToken(claims, userDetails.getUsername(), tenantId, session);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, String tenantId, String session) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .claim("session", session)
                .setAudience(tenantId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConstants.JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, JwtConstants.SIGNING_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        LOGGER.info("Validation for " + userDetails.getUsername());
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public MasterTenant getMasterTenant(String authorization) {
        MasterTenant masterTenant = null;
        String audience;
        String jwtToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            jwtToken = authorization.substring(7);
            try {
                audience = getTenantClientIdFromToken(jwtToken);
                masterTenant = masterTenantService.findById(Long.valueOf(audience));
                if (null == masterTenant) {
                    LOGGER.error("An error during getting tenant name");
                    throw new BadCredentialsException("Invalid tenant and user.");
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                LOGGER.error("JWT Token has expired", e);
            }
        }
        return masterTenant;
    }
    public Boolean validateToken(String token) {
        String username = null;
        String audience; // tenantOrClientId
        String jwtToken = null;
        jwtToken = token;
        try {
            username = getUsernameFromToken(jwtToken);
            audience = getTenantClientIdFromToken(jwtToken);
            LOGGER.info("Validation for " + username);
            MasterTenant masterTenant = masterTenantService.findById(Long.valueOf(audience));
            if (null == masterTenant) {
                LOGGER.error("An error during getting tenant name");
                throw new BadCredentialsException("Invalid tenant and user.");
            }
            DBContextHolder.setTenantName(masterTenant.getDbName());
            DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("Unable to get JWT Token", e);
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT Token has expired", e);
        } catch (SignatureException e) {
            LOGGER.error("Authentication Failed. Username or Password not valid.", e);
        }catch (Exception e) {
            LOGGER.error("Authentication Failed. Username or Password not valid.", e);
        }
        return false;
    }

    public Map<String, String> extractInformationFromJwt(HttpServletRequest request) {
        try {
            Map<String, String> userMap = new HashMap<>();
            // Get the Authorization header from the request
            String authorizationHeader = request.getHeader("Authorization");
            String session = null;
            // Check if the Authorization header is present and has the expected format
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // Extract the token by removing the "Bearer " prefix
                String jwt = authorizationHeader.substring(7);
                // Decode the JWT

                Claims claims = getAllClaimsFromToken(jwt);
                Object userObj = claims.get("user");
                if (userObj instanceof LinkedHashMap) {
                    // Now you can access user properties from the map
                    userMap.put("id", String.valueOf(((Number) ((LinkedHashMap<?, ?>) userObj).get("id")).intValue()));
                    userMap.put("username", (String) ((LinkedHashMap) userObj).get("username"));
                    userMap.put("email", (String) ((LinkedHashMap) userObj).get("email"));
                }
            }
            return userMap;
        } catch (Exception exception) {
            return null;
        }
    }
}
