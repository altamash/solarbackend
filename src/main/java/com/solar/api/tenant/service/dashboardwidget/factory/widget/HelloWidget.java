package com.solar.api.tenant.service.dashboardwidget.factory.widget;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.dashboardwidget.DashboardWidgetService;
import com.solar.api.tenant.service.dashboardwidget.param.Params;
import com.solar.api.tenant.service.userDetails.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class HelloWidget implements Widget {

    private final UserService userService;
    private final DashboardWidgetService dashboardWidgetService;

    public HelloWidget(UserService userService, DashboardWidgetService dashboardWidgetService) {
        this.userService = userService;
        this.dashboardWidgetService = dashboardWidgetService;
    }

    @Override
    public WidgetDTO getWidgetData(Long widgetId, Long compKey, Params params) {
        WidgetDTO widgetDTO = getWidgetDTO(widgetId, compKey);
        ObjectNode details = (ObjectNode) widgetDTO.getConfigJson().get("details");
        details.put("heading", "Hello!");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            User user = userService.findByUserName(((UserDetailsImpl) principal).getUsername());
            details.put("subheading", user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : ""));
        }
        BaseResponse response = dashboardWidgetService.getWelcomeWidgetData(compKey);
        details.put("text", (String) response.getData());
        return widgetDTO;
    }
}
