package com.solar.api.saas.mapper.permission.navigation;

import com.solar.api.tenant.model.permission.navigation.NavigationUserMap;

import java.util.List;
import java.util.stream.Collectors;

public class NavigationElementMapperMapper {

    public static NavigationUserMap toNavigationUserMap(NavigationUserMapDTO navigationUserMapDTO) {
        if (navigationUserMapDTO == null) {
            return null;
        }
        return NavigationUserMap.builder()
                .id(navigationUserMapDTO.getId())
                .navMapId(navigationUserMapDTO.getNavMapId())
                .activeNavIndicator(navigationUserMapDTO.getActiveNavIndicator())
                .favIndicator(navigationUserMapDTO.getFavIndicator())
                .icoUri(navigationUserMapDTO.getIcoUri())
                .build();
    }

    public static NavigationUserMapDTO toNavigationUserMapDTO(NavigationUserMap navigationUserMap) {
        if (navigationUserMap == null) {
            return null;
        }
        return NavigationUserMapDTO.builder()
                .id(navigationUserMap.getId())
                .navMapId(navigationUserMap.getNavMapId())
                .userId(navigationUserMap.getUser().getAcctId())
                .activeNavIndicator(navigationUserMap.getActiveNavIndicator())
                .favIndicator(navigationUserMap.getFavIndicator())
                .icoUri(navigationUserMap.getIcoUri())
                .build();
    }

    public static List<NavigationUserMap> toNavigationUserMaps(List<NavigationUserMapDTO> navigationUserMapDTOs) {
        return navigationUserMapDTOs.stream().map(n -> toNavigationUserMap(n)).collect(Collectors.toList());
    }

    public static List<NavigationUserMapDTO> toNavigationUserMapDTOs(List<NavigationUserMap> navigationUserMaps) {
        return navigationUserMaps.stream().map(n -> toNavigationUserMapDTO(n)).collect(Collectors.toList());
    }
}
