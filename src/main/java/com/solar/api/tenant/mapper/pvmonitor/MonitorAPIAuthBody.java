package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.AuthDataDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAPIAuthBody {
    List<Long> userIds;
    List<String> projectIds;
    List<String> variantIds;
    List<Long> subscriptionIds;
    List<String> subscriptionIdsMongo;
    // Solax
    private String userName;
    private Long userId;
    private String userPass;
//    private String currentTime;
    private String token;
    private String tokenId;
    private String sn;
    private String siteId;
    private String time;
    private String fromDateTime;
    private String toDateTime;
    // Solis
    private String userInfo;
    private String passWord;
    private Integer yingZhenType;
    private String language;
    Map<String,String> filteredRateCodes;
   //GoodWe
    private AuthDataDTO dataDTO;
    private String account;
    private String pwd;
    private String inverterNumber;

    private String startYear;
    private String endYear;
    private String yearMonth;

    private String variantId;

    private boolean instantaneousCall;

}
