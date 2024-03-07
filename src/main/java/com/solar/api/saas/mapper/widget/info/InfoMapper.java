package com.solar.api.saas.mapper.widget.info;

import com.solar.api.saas.mapper.widget.WidgetDTO;
import com.solar.api.saas.mapper.widget.WidgetGlobalPermissionDTO;
import com.solar.api.saas.mapper.widget.WidgetURIDTO;
import com.solar.api.saas.model.widget.Widget;
import com.solar.api.saas.model.widget.WidgetGlobalPermission;
import com.solar.api.saas.model.widget.WidgetURI;

import java.util.List;
import java.util.stream.Collectors;

public class InfoMapper {

    // Widget ////////////////////////////////////////////////
    public static Widget toWidget(WidgetDTO widgetDTO) {
        if (widgetDTO == null) {
            return null;
        }
        return Widget.builder()
                .id(widgetDTO.getId())
                .name(widgetDTO.getName())
                .type(widgetDTO.getType())
                .category(widgetDTO.getCategory())
                .helpUrl(widgetDTO.getHelpUrl())
                .build();
    }

    public static WidgetDTO toWidgetDTO(Widget widget) {
        if (widget == null) {
            return null;
        }
        return WidgetDTO.builder()
                .id(widget.getId())
                .name(widget.getName())
                .type(widget.getType())
                .category(widget.getCategory())
                .helpUrl(widget.getHelpUrl())
                .createdAt(widget.getCreatedAt())
                .updatedAt(widget.getUpdatedAt())
                .build();
    }

    public static Widget toUpdatedWidget(Widget widget, Widget widgetUpdate) {
        widget.setName(widgetUpdate.getName() == null ? widget.getName() : widgetUpdate.getName());
        widget.setType(widgetUpdate.getType() == null ? widget.getType() : widgetUpdate.getType());
        widget.setCategory(widgetUpdate.getCategory() == null ? widget.getCategory() : widgetUpdate.getCategory());
        widget.setHelpUrl(widgetUpdate.getHelpUrl() == null ? widget.getHelpUrl() : widgetUpdate.getHelpUrl());
        return widget;
    }

    public static List<Widget> toWidgets(List<WidgetDTO> widgetDTOs) {
        return widgetDTOs.stream().map(w -> toWidget(w)).collect(Collectors.toList());
    }

    public static List<WidgetDTO> toWidgetDTOs(List<Widget> widgets) {
        return widgets.stream().map(w -> toWidgetDTO(w)).collect(Collectors.toList());
    }

    // WidgetURI ////////////////////////////////////////////////
    public static WidgetURI toWidgetURI(WidgetURIDTO widgetURIDTO) {
        if (widgetURIDTO == null) {
            return null;
        }
        return WidgetURI.builder()
                .id(widgetURIDTO.getId())
                .widgetCode(widgetURIDTO.getWidgetCode())
                .input(widgetURIDTO.getInput())
                .callingURL(widgetURIDTO.getCallingURL())
                .retURI(widgetURIDTO.getRetURI())
                .build();
    }

    public static WidgetURIDTO toWidgetURIDTO(WidgetURI widgetURI) {
        if (widgetURI == null) {
            return null;
        }
        return WidgetURIDTO.builder()
                .id(widgetURI.getId())
                .widgetCode(widgetURI.getWidgetCode())
                .input(widgetURI.getInput())
                .callingURL(widgetURI.getCallingURL())
                .retURI(widgetURI.getRetURI())
                .createdAt(widgetURI.getCreatedAt())
                .updatedAt(widgetURI.getUpdatedAt())
                .build();
    }

    public static WidgetURI toUpdatedWidgetURI(WidgetURI widgetURI, WidgetURI widgetURIUpdate) {
        widgetURI.setWidgetCode(widgetURIUpdate.getWidgetCode() == null ? widgetURI.getWidgetCode() :
                widgetURIUpdate.getWidgetCode());
        widgetURI.setInput(widgetURIUpdate.getInput() == null ? widgetURI.getInput() : widgetURIUpdate.getInput());
        widgetURI.setCallingURL(widgetURIUpdate.getCallingURL() == null ? widgetURI.getCallingURL() :
                widgetURIUpdate.getCallingURL());
        widgetURI.setRetURI(widgetURIUpdate.getRetURI() == null ? widgetURI.getRetURI() : widgetURIUpdate.getRetURI());
        return widgetURI;
    }

    public static List<WidgetURI> toWidgetURIs(List<WidgetURIDTO> widgetURIDTOs) {
        return widgetURIDTOs.stream().map(w -> toWidgetURI(w)).collect(Collectors.toList());
    }

    public static List<WidgetURIDTO> toWidgetURIDTOs(List<WidgetURI> widgetURIs) {
        return widgetURIs.stream().map(w -> toWidgetURIDTO(w)).collect(Collectors.toList());
    }

    // WidgetGlobalPermission ////////////////////////////////////////////////
    public static WidgetGlobalPermission toWidgetGlobalPermission(WidgetGlobalPermissionDTO permissionDTO) {
        if (permissionDTO == null) {
            return null;
        }
        return WidgetGlobalPermission.builder()
                .id(permissionDTO.getId())
                .widgetCode(permissionDTO.getWidgetCode())
                .validInput(permissionDTO.getValidInput())
                .nullInputAllowed(permissionDTO.getNullInputAllowed())
                .enabled(permissionDTO.getEnabled())
                .roleId(permissionDTO.getRoleId())
                .userId(permissionDTO.getUserId())
                .build();
    }

    public static WidgetGlobalPermissionDTO toWidgetGlobalPermissionDTO(WidgetGlobalPermission permission) {
        if (permission == null) {
            return null;
        }
        return WidgetGlobalPermissionDTO.builder()
                .id(permission.getId())
                .widgetCode(permission.getWidgetCode())
                .validInput(permission.getValidInput())
                .nullInputAllowed(permission.getNullInputAllowed())
                .enabled(permission.getEnabled())
                .roleId(permission.getRoleId())
                .userId(permission.getUserId())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }

    public static WidgetGlobalPermission toUpdatedWidgetGlobalPermission(WidgetGlobalPermission permission,
                                                                         WidgetGlobalPermission permissionUpdate) {
        permission.setWidgetCode(permissionUpdate.getWidgetCode() == null ? permission.getWidgetCode() :
                permissionUpdate.getWidgetCode());
        permission.setValidInput(permissionUpdate.getValidInput() == null ? permission.getValidInput() :
                permissionUpdate.getValidInput());
        permission.setNullInputAllowed(permissionUpdate.getNullInputAllowed() == null ?
                permission.getNullInputAllowed() : permissionUpdate.getNullInputAllowed());
        permission.setEnabled(permissionUpdate.getEnabled() == null ? permission.getEnabled() :
                permissionUpdate.getEnabled());
        permission.setRoleId(permissionUpdate.getRoleId() == null ? permission.getRoleId() :
                permissionUpdate.getRoleId());
        permission.setUserId(permissionUpdate.getUserId() == null ? permission.getUserId() :
                permissionUpdate.getUserId());

        return permission;
    }

    public static List<WidgetGlobalPermission> toWidgetGlobalPermissions(List<WidgetGlobalPermissionDTO> permissionDTOs) {
        return permissionDTOs.stream().map(p -> toWidgetGlobalPermission(p)).collect(Collectors.toList());
    }

    public static List<WidgetGlobalPermissionDTO> toWidgetGlobalPermissionDTOs(List<WidgetGlobalPermission> permissions) {
        return permissions.stream().map(p -> toWidgetGlobalPermissionDTO(p)).collect(Collectors.toList());
    }
}
