package com.solar.api.tenant.controller.widgetconfiguration;

import com.solar.api.tenant.mapper.widgetconfiguration.UserWidgetDTO;
import com.solar.api.tenant.model.BaseResponse;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.solar.api.tenant.service.widgetconfiguration.WidgetConfigurationService;
@RestController("WidgetConfigurationController")
@RequestMapping(value = "/widgetConfiguration")
public class WidgetConfigurationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetConfigurationController.class);
    private final WidgetConfigurationService widgetConfigurationService;

    public WidgetConfigurationController(WidgetConfigurationService widgetConfigurationService) {
        this.widgetConfigurationService = widgetConfigurationService;
    }

    @ApiOperation("To fetch all endpoints of widgets that are already added in the table")
    @GetMapping(value = "/endpoints")
    public BaseResponse getEndpoints() {
        return widgetConfigurationService.getAllEndPoints();
    }

    @ApiOperation("To fetch all widgets that are already added in the")
    @GetMapping(value = "/moduleWidgets")
    public BaseResponse getModuleWidgets() {
        return widgetConfigurationService.getAllWidgets();
    }

    /*
    * save payload sample
    * {"acctId": 1,"endpointId": 1,"moduleWidgetId": 1,"widgetName":"widget2 test"}
    * update payload sample
    * {"id":10, "acctId": 1, "endpointId": 1,"moduleWidgetId": 1,"widgetName":"widget2 test"}
    *  id = userwidget table pk, acctId = loggedin userid
     * */
    @ApiOperation("save user widget-endpoint association")
    @PostMapping(value = "/addOrUpdateUserWidget")
    public ResponseEntity addOrUpdateMarketPlaceOffer(@RequestBody UserWidgetDTO userWidgetDTO,
                                                      @RequestHeader("Comp-Key") Long compKey) {
        return widgetConfigurationService.saveOrUpdateUserWidget(userWidgetDTO, compKey);
    }

    @GetMapping(value = "/loggedInUserWidgets")
    public BaseResponse getLoggedInUserWidgets( @RequestHeader("Comp-Key") Long compKey, @RequestParam("acctId") Long acctId) {
        return widgetConfigurationService.getLoggedInUserWidgets(acctId,compKey);
    }
}
