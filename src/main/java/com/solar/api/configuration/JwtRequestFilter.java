package com.solar.api.configuration;

import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.tenant.service.userDetails.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserDetailsService jwtUserDetailsService;
    private final MasterTenantService masterTenantService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(UserDetailsServiceImpl jwtUserDetailsService,
                            MasterTenantService masterTenantService,
                            JwtTokenUtil jwtTokenUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.masterTenantService = masterTenantService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String audience; // tenantOrClientId
        String jwtToken = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                audience = jwtTokenUtil.getTenantClientIdFromToken(jwtToken);
                MasterTenant masterTenant = masterTenantService.findById(Long.valueOf(audience));
                if (null == masterTenant) {
                    LOGGER.error("An error during getting tenant name");
                    throw new BadCredentialsException("Invalid tenant and user.");
                }
                DBContextHolder.setTenantName(masterTenant.getDbName());
                DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
//                this.currentDb = masterTenant.getDbName();
            } catch (IllegalArgumentException e) {
                LOGGER.error("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                LOGGER.error("JWT Token has expired", e);
            } catch (SignatureException e) {
                LOGGER.error("Authentication Failed. Username or Password not valid.", e);
            }
        } else {
            setDbContextFromHeader(request);
            LOGGER.warn("JWT Token does not begin with Bearer String for request URI " + request.getRequestURI());
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    private MasterTenant setDbContextFromHeader(HttpServletRequest request) {
        if (request.getHeader(SaasSchema.COMPANY_KEY_HEADER) != null && !request.getHeader(SaasSchema.COMPANY_KEY_HEADER).isEmpty()) {
            Long compKey = Long.parseLong(request.getHeader(SaasSchema.COMPANY_KEY_HEADER));
            if (compKey != null) {
                return masterTenantService.setCurrentDb(compKey);
            }
        }
        return null;
    }
}
