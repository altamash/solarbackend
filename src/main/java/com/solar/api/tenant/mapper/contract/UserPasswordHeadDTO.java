package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPasswordHeadDTO {
    private Long id;
    private Long accountId;
    private String authPlatform;
    private String oAuthCode;
    private String password;
    private LocalDateTime futureExpiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
