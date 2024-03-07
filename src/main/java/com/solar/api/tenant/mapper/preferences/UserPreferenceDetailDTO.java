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
public class UserPreferenceDetailDTO {
    private Long id;
    private Long userId;
    private Long userPreferenceId;
    private String text;
    private Long tempParamOverrideId;
    private Long num;
    private LocalDateTime date;
    private String format;
    private Long docId;
    private String icon;
    private Byte[] image;
    private String activeChannel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
