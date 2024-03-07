package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.AuthDataDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAPIAuthResponse {
    private String token;
    private String csrfToken;
    private User user;
    private String apiResponseMsg;
    private AuthDataDTO data;
}
