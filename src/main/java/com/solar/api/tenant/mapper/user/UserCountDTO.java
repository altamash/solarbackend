package com.solar.api.tenant.mapper.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCountDTO {

    private Long count;
    private int month;
    private String monthName;

    public UserCountDTO(Long count, int month) {
        this.count = count;
        this.month = month;
    }
}
