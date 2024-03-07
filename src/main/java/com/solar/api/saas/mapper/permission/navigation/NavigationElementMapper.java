package com.solar.api.saas.mapper.permission.navigation;

import com.solar.api.saas.model.permission.navigation.NavigationElement;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NavigationElementMapper {

    public static NavigationElement toNavigationElement(NavigationElementDTO navigationElementDTO) {
        if (navigationElementDTO == null) {
            return null;
        }
        return NavigationElement.builder()
                .id(navigationElementDTO.getId())
                .navName(navigationElementDTO.getNavName())
                .displayName(navigationElementDTO.getDisplayName())
                .parent(navigationElementDTO.getParentNavElement())
                .enabled(navigationElementDTO.getEnabled())
                .activeNavIndicator(navigationElementDTO.getActiveNavIndicator())
                .channel(navigationElementDTO.getChannel())
                .navUri(navigationElementDTO.getNavUri())
                .icoUri(navigationElementDTO.getIcoUri())
                .build();
    }

    public static NavigationElementDTO toNavigationElementDTO(NavigationElement navigationElement) {
        if (navigationElement == null) {
            return null;
        }
        return NavigationElementDTO.builder()
                .id(navigationElement.getId())
                .navName(navigationElement.getNavName())
                .displayName(navigationElement.getDisplayName())
                .parent(navigationElement.getParent() != null ? navigationElement.getParent().getId() : null)
                .enabled(navigationElement.getEnabled())
                .activeNavIndicator(navigationElement.getActiveNavIndicator())
                .channel(navigationElement.getChannel())
                .navUri(navigationElement.getNavUri())
                .icoUri(navigationElement.getIcoUri())
                .subElements(navigationElement.getSubElements() != null ? toNavigationElementDTOs(navigationElement.getSubElements()) : Collections.EMPTY_LIST)
                .build();
    }

    public static List<NavigationElement> toNavigationElements(List<NavigationElementDTO> navigationElementDTOs) {
        return navigationElementDTOs.stream().map(n -> toNavigationElement(n)).collect(Collectors.toList());
    }

    public static List<NavigationElementDTO> toNavigationElementDTOs(List<NavigationElement> navigationElements) {
        return navigationElements.stream().map(n -> toNavigationElementDTO(n)).collect(Collectors.toList());
    }
}
