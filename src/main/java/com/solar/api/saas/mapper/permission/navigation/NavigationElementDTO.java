package com.solar.api.saas.mapper.permission.navigation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.model.permission.navigation.NavigationElement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NavigationElementDTO {

    private Long id;
    private String navName;
    private String displayName;
    private Long parent;
    private NavigationElement parentNavElement;
    private Boolean enabled;
    private String activeNavIndicator;
    private String channel;
    private String navUri;
    private String icoUri;
    private List<NavigationElementDTO> subElements;
}
