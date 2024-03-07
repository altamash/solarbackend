package com.solar.api.tenant.service.widgetconfiguration;


import com.solar.api.tenant.mapper.widgetconfiguration.UserWidgetDTO;
import com.solar.api.tenant.model.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface WidgetConfigurationService {
    BaseResponse getAllEndPoints();

    BaseResponse getAllWidgets();

    ResponseEntity saveOrUpdateUserWidget(UserWidgetDTO userWidgetDTO, Long compKey);

    BaseResponse getLoggedInUserWidgets(Long acctId, Long compKey);

}
