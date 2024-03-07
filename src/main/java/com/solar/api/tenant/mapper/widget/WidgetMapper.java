package com.solar.api.tenant.mapper.widget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.widget.DashboardWidget;

import java.util.List;
import java.util.stream.Collectors;

public class WidgetMapper {
    public static DashboardWidget toWidget(WidgetDTO widgetDTO) {
        return DashboardWidget.builder()
                .id(widgetDTO.getWidgetId())
                .name(widgetDTO.getName())
                .configJson(widgetDTO.getConfigJson().asText())
                .build();
    }

    public static WidgetDTO toWidgetDTO(DashboardWidget widget) {
        if (widget == null) {
            return null;
        }
        try {
            return WidgetDTO.builder()
                    .widgetId(widget.getId())
                    .name(widget.getName())
                    .configJson(new ObjectMapper().readValue(widget.getConfigJson(), ObjectNode.class))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<DashboardWidget> toWidgetList(List<WidgetDTO> widgetDTOList) {
        return widgetDTOList.stream().map(WidgetMapper::toWidget).collect(Collectors.toList());
    }

    public static List<WidgetDTO> toWidgetDTOList(List<DashboardWidget> widgetList) {
        return widgetList.stream().map(WidgetMapper::toWidgetDTO).collect(Collectors.toList());
    }

}
