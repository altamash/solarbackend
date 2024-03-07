package com.solar.api.tenant.mapper.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantConfigDTO {
    private Long id;
    private String parameter;
    private String description;
    private String varType;
    private Long number;
    private String text;
    private LocalDateTime dateTime;
    private String prefix;
    private String postfix;
    private String format;
    private String category;
    private Boolean locked;
    private String allowedRegex;
    private Boolean masked;
    private Long orgID;
    private String alias;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
