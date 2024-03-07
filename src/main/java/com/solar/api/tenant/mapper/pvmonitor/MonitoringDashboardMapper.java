package com.solar.api.tenant.mapper.pvmonitor;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.pvmonitor.*;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetDataDTO;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetDataResult;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetSiteDataDTO;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetWeatherDetailDataResult;
import com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MonitoringDashboardMapper {
    public static WidgetDataDTO toWidgetDataDTO(WidgetDataResult widgetDataResult, WidgetWeatherDetailDataResult widgetWeatherDetailDataResult,DataDTO dataDTO) {
      return  WidgetDataDTO.builder()
                .refId(widgetDataResult!= null? (widgetDataResult.getRefId()!=null? widgetDataResult.getRefId():null) : null)
                .refType(widgetDataResult!= null?(widgetDataResult.getRefType()!=null?widgetDataResult.getRefType():null) :null)
                .systemSize(widgetDataResult!= null? (widgetDataResult.getSystemSize() != null? widgetDataResult.getSystemSize():null):null)
                .address(widgetDataResult!= null?(widgetDataResult.getAddress() != null? widgetDataResult.getAddress() :null):null)
                .mp(widgetDataResult!= null?(widgetDataResult.getMp()!= null? widgetDataResult.getMp() :null):null)
                .state(widgetDataResult!= null?(widgetDataResult.getState()!= null? widgetDataResult.getState():null) :null)
                .googleCoordinates(widgetDataResult!= null? (widgetDataResult.getGoogleCoordinates()!=null? widgetDataResult.getGoogleCoordinates():null): null)
                .geoLat(widgetDataResult!= null?(widgetDataResult.getGeoLat()!=null ? widgetDataResult.getGeoLat():null):null)
                .geoLong(widgetDataResult!= null? (widgetDataResult.getGeoLong()!= null? widgetDataResult.getGeoLong():null):null)
                .installationType(widgetDataResult!= null? (widgetDataResult.getInstallationType() !=null? widgetDataResult.getInstallationType():null):null)
               .gardenType(widgetDataResult!= null?(widgetDataResult.getGardenType()!=null? widgetDataResult.getGardenType():null):null)
               .weather(widgetWeatherDetailDataResult!= null? widgetWeatherDetailDataResult.getWeather(): "No Weather Found")
                .subscriptionCount(dataDTO!=null ? dataDTO.getSubCount() : 0)
                .build();
    }
    public static List<WidgetDataDTO> toWidgetDataDTOs(List<WidgetDataResult> widgetDataResults, List<WidgetWeatherDetailDataResult> widgetWeatherDetailDataResults, List<DataDTO> dataDTOS) {
            return widgetDataResults
                    .stream()
                    .map(widgetDataResult -> {
                        // Use stream operations to find the matching WidgetWeatherDetailDataResult
                        WidgetWeatherDetailDataResult matchingWeather = widgetWeatherDetailDataResults
                                .stream()
                                .filter(weatherDetailDataResult -> weatherDetailDataResult.getRefId().equals(widgetDataResult.getRefId()))
                                .findFirst()
                                .orElse(null);
                        DataDTO matchingGarden = dataDTOS
                                .stream()
                                .filter(gardenDataResult -> gardenDataResult.getRefId().equals(widgetDataResult.getRefId()))
                                .findFirst()
                                .orElse(null);

                        return toWidgetDataDTO(widgetDataResult, matchingWeather,matchingGarden);
                    })
                    .collect(Collectors.toList());
        }

    }
