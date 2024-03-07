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
public class UserPreferenceDTO {
    private Long id;
    private String userParam;
    private String description;
    private String paramType;
    private String userType;
    private String category;
    private Long attributeRefId;
    private Boolean tempOverrideEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
