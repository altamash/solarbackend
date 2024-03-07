package com.solar.api.saas.mapper.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantThemeDTO {

    private Long id;
    private String scope;
    private Long userId;
    private String themeArea;
    private String color;
    private String opacity;
    private String themeType;
    private String fontFamily;
    private String target;
    private String fontSize;
}
