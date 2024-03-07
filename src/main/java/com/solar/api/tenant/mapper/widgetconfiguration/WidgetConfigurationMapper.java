package com.solar.api.tenant.mapper.widgetconfiguration;

import com.solar.api.tenant.model.widgetconfiguration.Endpoint;
import com.solar.api.tenant.model.widgetconfiguration.ModuleWidget;
import com.solar.api.tenant.model.widgetconfiguration.UserWidget;

import java.util.List;
import java.util.stream.Collectors;

public class WidgetConfigurationMapper {

    public static EndPointsDTO toEndPointsDTO(Endpoint endpoint) {
        if (endpoint == null) {
            return null;
        }
        return EndPointsDTO.builder()
                .id(endpoint.getId())
                .name(endpoint.getName())
                .description(endpoint.getDescription())
                .url(endpoint.getUrl())
                .build();
    }

    public static ModuleWidgetDTO toModuleWidgetDTO(ModuleWidget moduleWidget) {
        if (moduleWidget == null) {
            return null;
        }
        return ModuleWidgetDTO.builder()
                .id(moduleWidget.getId())
                .widgetColor(moduleWidget.getWidgetColor())
                .widgetIcon(moduleWidget.getWidgetIcon())
                .widgetSize(moduleWidget.getWidgetSize())
                .widgetName(moduleWidget.getWidgetName())
                .widgetUri(moduleWidget.getWidgetUri())
                .build();
    }

    public static ModuleWidget toModuleWidget(ModuleWidgetDTO moduleWidgetDTO) {
        if (moduleWidgetDTO == null) {
            return null;
        }
        return ModuleWidget.builder()
                .id(moduleWidgetDTO.getId())
                .widgetColor(moduleWidgetDTO.getWidgetColor())
                .widgetIcon(moduleWidgetDTO.getWidgetIcon())
                .widgetSize(moduleWidgetDTO.getWidgetSize())
                .widgetName(moduleWidgetDTO.getWidgetName())
                .widgetUri(moduleWidgetDTO.getWidgetUri())
                .build();
    }

    public static Endpoint toEndPoint(EndPointsDTO endPointsDTO) {
        if (endPointsDTO == null) {
            return null;
        }
        return Endpoint.builder()
                .id(endPointsDTO.getId())
                .name(endPointsDTO.getName())
                .description(endPointsDTO.getDescription())
                .url(endPointsDTO.getUrl())
                .build();
    }
    public static UserWidget toUserWidget(UserWidgetDTO userWidgetDTO) {
        if (userWidgetDTO == null) {
            return null;
        }
        return UserWidget.builder()
                .widgetName(userWidgetDTO.getWidgetName())
               // .moduleWidget(toModuleWidget(userWidgetDTO.getModuleWidgetDTO()))
               // .endpoint(toEndPoint(userWidgetDTO.getEndPointsDTO()))
                .acctId(userWidgetDTO.getAcctId())
                .build();
    }

    public static List<UserWidgetDTO> toUserWidgetDTOList(List<UserWidget> userWidgets) {
        return userWidgets.stream().map(WidgetConfigurationMapper::toUserWidgetDTO).collect(Collectors.toList());
    }

    private static UserWidgetDTO toUserWidgetDTO(UserWidget userWidget) {
        if (userWidget == null) {
            return null;
        }
        return UserWidgetDTO.builder()
               .widgetName(userWidget.getWidgetName())
               .acctId(userWidget.getAcctId())
               .moduleWidgetDTO(toModuleWidgetDTO(userWidget.getModuleWidget()))
               .endPointsDTO(toEndPointsDTO(userWidget.getEndpoint()))
               .build();
    }

    public static List<EndPointsDTO> toEndPointList(List<Endpoint> endpoints) {
        return endpoints.stream().map(WidgetConfigurationMapper::toEndPointsDTO).collect(Collectors.toList());
    }

    public static List<ModuleWidgetDTO>  toModuleWidgetList(List<ModuleWidget> moduleWidgets) {
        return moduleWidgets.stream().map(WidgetConfigurationMapper::toModuleWidgetDTO).collect(Collectors.toList());
    }
}
