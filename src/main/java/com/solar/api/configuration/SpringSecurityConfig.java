package com.solar.api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui.html/**", "/swagger-ui/**", "/swagger-ui/index" +
            ".html", "/swagger-ui/index.html**", "/swagger-ui/",
            "/v2/api-docs", "/v3/api-docs/**",
            "/configuration/ui", "/configuration/security",
            "/ws/**", "/index.html", "/webjars/**", "/js/**"
    };

    private static final String[] AUTH_WHITELIST_SAAS_API = {
            "/saas/role", "/saas/userType", "/saas/signup", "/saas/signin", "/document/slider/resources",
            "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui.html/**", "/swagger-ui/**", "/swagger-ui/index" +
            ".html", "/swagger-ui/index.html**", "/swagger-ui/",
            "/v2/api-docs", "/v3/api-docs/**",
            "/configuration/ui", "/configuration/security",
            "/ws/**", "/index.html", "/webjars/**", "/js/**", "/systemAttribute/carousel/channel/**"
    };

    private static final String[] AUTH_WHITELIST_API = {
            "/role", "/userType", "/signup", "/signin","/user/selfRegistration/**", "/user/getAllUserType/**","/user/getCompanyNameLike/**","/user/getRequestADemo","/user/registerYourInterest",
            "/user/getLoginUrlLike/**","/permission/adminRole/level/**","/user/internalUserRegistration/**","/externalLink/resetUserStatus/**","/forgot", "/paymentInfo/**", "/JasperReports/**",
            "/forgotPassword/**", "/company/compkey/**", "/company/companyKey/**", "/company/compkey/v1",
            "/user/callback/docuSign",
            "/report/getembedinfo", "https://app.powerbi.com/**",
            "/swagger-resources/**", "/swagger-ui.html", "/swagger-ui.html/**", "/swagger-ui/**", "/swagger-ui/index" +
            ".html", "/swagger-ui/index.html**", "/swagger-ui/",
            "/v2/api-docs", "/v3/api-docs/**",
            "/configuration/ui", "/configuration/security",
            "/ws/**", "/index.html", "/webjars/**", "/js/**", "/systemAttribute/carousel/channel/**", "/submitInterest/**","/user/setUserPassword/**","/user/generatePasswordEmail","/user/forgetPassword","/externalLink/tokenSessionExpiryVerification/**","/user/updateUserPassword/**",
            "/tenantConfig/getByParameter/**","/projectWrapper/showAcquisitionTemplate/**","/portalAttributeValue/portalAttributeById/**","/acquisition/v2/registerYourInterest","/etl/processETL/**","/validateToken",
    };

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPointBean() throws Exception {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(1)
    public static class SAASConfigurationAdaptor extends WebSecurityConfigurerAdapter {

        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Autowired
        @Qualifier("tenantDetailsService")
        private UserDetailsService jwtTenantDetailsService;

        @Autowired
        private JwtMasterRequestFilter jwtRequestFilter;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Bean("saasAuthenticationManager")
        @Primary
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(jwtTenantDetailsService).passwordEncoder(passwordEncoder);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(AUTH_WHITELIST);
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .antMatcher("/saas/**")
                    .cors().and()
                    .csrf().disable()
                    .httpBasic().disable()
                    .formLogin().disable()
                    .authorizeRequests()
                    .antMatchers(AUTH_WHITELIST_SAAS_API).permitAll()
                    .anyRequest().authenticated().and().
                    exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        }

    }

    @Configuration
    @Order(2)
    public static class TenantConfigurationAdaptor extends WebSecurityConfigurerAdapter {

        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Autowired
        @Qualifier("userDetailsService")
        private UserDetailsService jwtUserDetailsService;

        @Autowired
        private JwtRequestFilter jwtRequestFilter;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Bean("tenantAuthenticationManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();

        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(AUTH_WHITELIST);
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
//                    .antMatcher("/**")
                    .cors().and()
                    .csrf().disable()
                    .httpBasic().disable()
                    .formLogin().disable()
                    .authorizeRequests()
                    .antMatchers(AUTH_WHITELIST_API).permitAll()
                    .anyRequest().authenticated().and().
                    exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
}
