package com.solar.api.saas.service.integration.docuSign.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

      /*"access_token": "",
        "api_domain": "",
        "token_type": "Bearer",
        "expires_in": 3600*/

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("api_domain")
    private String apiDomain;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private Integer expiresIn;

}
