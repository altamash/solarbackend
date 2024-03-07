package com.solar.api.saas.mapper.preferences;

import com.solar.api.saas.model.preferences.TenantTheme;

public class TenantThemeMapper {

    public static TenantTheme toTenantTheme(TenantThemeDTO tenantThemeDTO) {
        return TenantTheme.builder()
                .id(tenantThemeDTO.getId())
                .scope(tenantThemeDTO.getScope())
                .userId(tenantThemeDTO.getUserId())
                .themeArea(tenantThemeDTO.getThemeArea())
                .color(tenantThemeDTO.getColor())
                .opacity(tenantThemeDTO.getOpacity())
                .themeType(tenantThemeDTO.getThemeType())
                .fontFamily(tenantThemeDTO.getFontFamily())
                .target(tenantThemeDTO.getTarget())
                .fontSize(tenantThemeDTO.getFontSize())
                .build();
    }

    public static TenantThemeDTO toTenantThemeDTO(TenantTheme tenantTheme) {
        if (tenantTheme == null) {
            return null;
        }
        return TenantThemeDTO.builder()
                .id(tenantTheme.getId())
                .scope(tenantTheme.getScope())
                .userId(tenantTheme.getUserId())
                .themeArea(tenantTheme.getThemeArea())
                .color(tenantTheme.getColor())
                .opacity(tenantTheme.getOpacity())
                .themeType(tenantTheme.getThemeType())
                .fontFamily(tenantTheme.getFontFamily())
                .target(tenantTheme.getTarget())
                .fontSize(tenantTheme.getFontSize())
                .build();
    }
}
