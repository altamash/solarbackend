package com.solar.api.tenant.mapper.ca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaReferralInfoDTO {
    private Long id;
    private String source;
    private Long representativeId;
    private String promoCode;
    private Long entityId;

    private String agentDesignation;
    private String agentName;
}
