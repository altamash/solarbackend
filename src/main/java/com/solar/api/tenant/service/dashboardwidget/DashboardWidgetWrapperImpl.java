package com.solar.api.tenant.service.dashboardwidget;

import com.solar.api.exception.InvalidValueException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.repository.widget.SAASWidgetRepository;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.widget.WidgetMapper;
import com.solar.api.tenant.model.widget.DashboardWidget;
import com.solar.api.tenant.repository.widget.TenantWidgetRepository;
import com.solar.api.tenant.service.dashboardwidget.factory.WidgetFactory;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DashboardWidgetWrapperImpl implements DashboardWidgetWrapper {
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final TenantWidgetRepository tenantWidgetRepository;
    private final SAASWidgetRepository saasWidgetRepository;
    private final WidgetFactory widgetFactory;

    public DashboardWidgetWrapperImpl(TenantWidgetRepository tenantWidgetRepository,
                                      SAASWidgetRepository saasWidgetRepository, WidgetFactory widgetFactory) {
        this.tenantWidgetRepository = tenantWidgetRepository;
        this.saasWidgetRepository = saasWidgetRepository;
        this.widgetFactory = widgetFactory;
    }

    @Override
    public BaseResponse<Object> getWidgetsStructureListAndIds() {
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .data(WidgetMapper.toWidgetDTOList(tenantWidgetRepository.findAll()))
                .build();
    }

    @Override
    public BaseResponse<Object> getWidgetDataById(Long widgetId, Long companyKey, String param) {
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .data(widgetFactory.getWidget(widgetId, companyKey, param))
                .build();
    }

    @Override
    public BaseResponse<Object> addWidget(Long widgetId) {
        com.solar.api.saas.model.widget.DashboardWidget dashboardWidget = saasWidgetRepository.findById(widgetId)
                .orElseThrow(() -> new NotFoundException(com.solar.api.saas.model.widget.DashboardWidget.class, widgetId));
        try {
            return BaseResponse.builder()
                    .code(HttpStatus.CREATED.value())
                    .data(tenantWidgetRepository.save(DashboardWidget.builder()
                            .id(widgetId)
                            .name(dashboardWidget.getName())
                            .configJson(dashboardWidget.getWidgetType().getConfigJson())
                            .widgetTypeId(dashboardWidget.getWidgetType().getId())
                            .build()))
                    .build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InvalidValueException(e.getMessage());
        }
    }
}
