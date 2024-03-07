package com.solar.api.tenant.mapper.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationDTO {

    /**
     * This class specifically used for admin to change customer passwords
     */
    public String password;
    public Long adminId;
    public Long userId;
    public String token;
}
