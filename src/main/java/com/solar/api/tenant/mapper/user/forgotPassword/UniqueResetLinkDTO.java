package com.solar.api.tenant.mapper.user.forgotPassword;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UniqueResetLinkDTO {

    private Long id;
    private Long tenantId;
    private Long userAccount;
    private Long adminAccount;
    private String uniqueText;
    private LocalDateTime generatedOn;
    private Boolean usedIndicator;
}
