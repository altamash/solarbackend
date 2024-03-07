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
public class UserPasswordHistoryDTO {
    private Long id;
    private Long accountId;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
