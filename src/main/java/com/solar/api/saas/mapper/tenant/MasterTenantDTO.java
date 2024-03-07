package com.solar.api.saas.mapper.tenant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class MasterTenantDTO implements Serializable {

    private Long id;
    private String jwtToken;
    private String dbName;
    private String url;
    private String userName;
    private String email;
    private String passCode;
    private String driverClass;
    private String status;
    private String companyCode;
    private Long companyKey;
    private String companyName;
    private String companyLogo;
    private Integer tenantTier;
    private String type;
    private Set<String> roles;
    private String loginUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String landingText;
    private String landingDescription;
    private List<String> landingImagesUrl;


}
