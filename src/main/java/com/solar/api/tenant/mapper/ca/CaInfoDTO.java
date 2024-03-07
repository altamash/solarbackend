package com.solar.api.tenant.mapper.ca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaInfoDTO {
    private String salesAgent;
    private Long id;
    private Long zipcode;
    private String softCheck;
    private String status;
    private String region;
    private Long acctId;
    private String caStage;
    private String phoneNum;
    }
