package com.solar.api.saas.mapper.permission.navigation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NavigationUserMapDTO {

    private Long id;
    private Long navMapId;
    private Long userId;
    private String activeNavIndicator;
    private Boolean favIndicator;
    private Long icoUri;
}
