package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDailyDTO;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.DataPVDTO;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.DetailDataDTO;
import com.solar.api.tenant.service.process.pvmonitor.platform.solis.dto.DataDTO;
import com.solar.api.tenant.service.process.pvmonitor.platform.solis.dto.PageDTO;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAPIResponse {
    //  Custom field values
    private Long id;
    private Long userId;
    private Long subscriptionId;
    private String subscriptionIdMongo;
    private String site;
    private String inverterNumber;
    private Date dateTime;
    private List<String> inverterNumbers;
    private List<InverterDetailDTO> detailData;

    //  For widget
    private Double sytemSize;
    private Double currentValueToday;
    private Double currentValue;
    private Double currentValueRunning;
    private Double yieldValue;
    private Double yieldValueRunning;
    private Double peakValue;
    private Double dailyYield;
    private Double monthlyYield;
    private Double annualYield;
    private Double grossYield;
    private Double treesPlanted;
    private Double co2Reduction;

    // For graph
    private Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime;
//    private Map<String, Map<Date, MonitorAPIResponseL2>> inverterGridpowerOverTime;
//    private Map<String, Map<Date, MonitorAPIResponseL2>> inverterRelayPowerOverTime;

    //  GET_POWER response from Solax
    private Double ratedPower;
    private Double gridPower;
    private Double relay2Power;
    private Double feedInPower;
    private Double relay1Power;
    private Double batPower1;

    //  GET_CURRENT_DATA response from Solax
    private Double cakeYield;
    private Double totalEpsEnergy;
    private Double todayConsumeEnergy;
    private Double todayFeedInEnergy;
    private Double consumeenergy;
    private Double yearEpsEnergy;
    private Double totalYield;
    private Double monthEpsEnergy;
    private Double monthYield;
    private Double todayYield;
    private Double yearYield;
    private Double feedinenergy;
    private Double todayEpsEnergy;
    private Double gridpower;

    //  GET_REALTIME_INFO response from Solax
    private MonitorAPIResponseL2 result;

    //  GET_SITE_TOTAL_POWER response from Solax
    private List<MonitorAPIResponseL2> object;

    private String apiResponseMsg;

    //  For Solis
    private String sno;
    private Double power; // Current Power
    private String powerStr;
    private Double capacity; // Current Power
    private String capacityStr; // Current Power
    private Double monthEnergy; // Monthly Yield
    private String monthEnergyStr; // Monthly Yield
    private Double yearEnergy; // Total Yield
    private String yearEnergyStr; // Total Yield
    private PageDTO pageDTO;
    private String month;
    private DataDTO data;
//    For GoodWe
    private DetailDataDTO detailedData;
    private List<DetailDataDTO> pacDataDetail;
    private List<DataPVDTO> pvData;
    private List<WidgetCustomResponse> widgetCustomResponseList;
    private  Integer totalPages;
    private Long totalElements;
//    private DetailDataDTO detailedDataList;
    //For GoodWe
//    private String sn;
//    private Double eday;
//    private Double emonth;
//    private Double etotal;
////    private double capacity;
//    private Double pac;
//    private Double y; //yield value for year
    private List<MonitorReadingDTO> monitorReadingDTOs;
    private List<MonitorReadingDailyDTO> bulkDailyRecords;

    private String day;
    private String weekDetail;

}
