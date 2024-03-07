package com.solar.api.tenant.mapper.contract;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.solar.api.Constants.SOLAR_EDGE;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.Site;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.SiteResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SolarEdgeMapper extends Utility{

    public static HttpResponse<String> getSitesURL(String tokenKey, String size) throws UnirestException {
        return Unirest.get(SOLAR_EDGE.URL.concat(SOLAR_EDGE.SITES)).queryString("size", size)
                .queryString("searchText", "").queryString("sortProperty", "")
                .queryString("sortOrder", "DESC").queryString("api_key", tokenKey).asString();
    }

    public static HttpResponse<String> getSiteEquipmentURL(String tokenKey, String siteId) throws UnirestException {
        return Unirest.get(SOLAR_EDGE.URL.concat(SOLAR_EDGE.EQUIPMENT).concat(siteId).concat("/list"))
                .queryString("api_key", tokenKey).asString();
    }

    public static HttpResponse<String> getAnnualYieldURL(String tokenKey, String siteId, String year) throws UnirestException {
        String startDate = year.concat("-01-01%2000:00:00");
        String endDate = year.concat("-12-31%2023:59:00");

        return Unirest.get(SOLAR_EDGE.URL.concat(SOLAR_EDGE.SITE).concat(siteId).concat(SOLAR_EDGE.METERS).concat("?startTime=").concat(startDate).concat("&endTime=")
                        .concat(endDate)).queryString("meters", "Production").queryString("timeUnit", "MONTH")
                .queryString("api_key", tokenKey).asString();
    }

    public static HttpResponse<String> getMonthlyYieldURL(Date currntDate, String tokenKey, String siteId) throws UnirestException, ParseException {
        String date = getDateString(getStartOfMonth(currntDate), SYSTEM_DATE_FORMAT);
        assert date != null;
        String startDate = date.concat("%2000:00:00");
        String endDate = getDateString(currntDate, SYSTEM_DATE_FORMAT).concat("%20")
                .concat(Objects.requireNonNull(getDateString(currntDate, SOLAR_EDGE.SYSTEM_TIME_FORMAT)));

        return Unirest.get(SOLAR_EDGE.URL.concat(SOLAR_EDGE.SITE).concat(siteId).concat(SOLAR_EDGE.METERS).concat("?startTime=")
                        .concat(startDate).concat("&endTime=").concat(endDate)).queryString("meters", "Production")
                        .queryString("timeUnit", "DAY").queryString("api_key", tokenKey).asString();
    }

    public static HttpResponse<String> getMeterDataURL(String tokenKey, String siteId, Date lastTime) throws UnirestException {
        String startDate = Objects.requireNonNull(getDateString(lastTime, SYSTEM_DATE_FORMAT)).concat("%20")
                .concat(Objects.requireNonNull(getDateString(lastTime, SOLAR_EDGE.SYSTEM_TIME_FORMAT)));
        Date min30Plus = addMinutes(lastTime, 30);
        String endDate = Objects.requireNonNull(getDateString(min30Plus, SYSTEM_DATE_FORMAT)).concat("%20")
                .concat(Objects.requireNonNull(getDateString(min30Plus, SOLAR_EDGE.SYSTEM_TIME_FORMAT)));

        return Unirest.get(SOLAR_EDGE.URL.concat(SOLAR_EDGE.SITE).concat(siteId).concat(SOLAR_EDGE.METERS).concat("?startTime=").concat(startDate).concat("&endTime=")
                        .concat(endDate)).queryString("meters", "Production").queryString("timeUnit", "QUARTER_OF_AN_HOUR")
                .queryString("api_key", tokenKey).asString();
    }

    public static HttpResponse<String> getHistoricYieldURL(String fromDate, String toDate, String tokenKey, String siteId, String timeUnit) throws UnirestException {
//        String date = getDateString(getStartOfMonth(currntDate), SYSTEM_DATE_FORMAT);
//        assert date != null;
//        String startDate = date.concat("%2000:00:00");
//        String endDate = getDateString(currntDate, SYSTEM_DATE_FORMAT).concat("%20")
//                .concat(Objects.requireNonNull(getDateString(currntDate, SOLAR_EDGE.SYSTEM_TIME_FORMAT)));

        return Unirest.get(SOLAR_EDGE.URL.concat(SOLAR_EDGE.SITE).concat(siteId).concat(SOLAR_EDGE.ENERGY).concat("?startDate=")
                        .concat(fromDate).concat("&endDate=").concat(toDate))
//                .queryString("meters", "Production")
                .queryString("timeUnit", timeUnit).queryString("api_key", tokenKey).asString();
    }

    public static HttpResponse<String> getSiteOverviewURL(String tokenKey, String siteId) throws UnirestException {
        return Unirest.get(SOLAR_EDGE.URL.concat(SOLAR_EDGE.SITE).concat(siteId).concat(SOLAR_EDGE.OVERVIEW))
                .queryString("api_key", tokenKey).asString();
    }

    public static List<String> toSiteIds(SiteResponse siteResponse) {
        return siteResponse.getSites().getSite().stream().map(Site::getId).collect(Collectors.toList());
    }

    public static MonitorReading toMonitorReading(String siteId, String inverterNumber) {
        return MonitorReading.builder().site(siteId).inverterNumber(inverterNumber).build();
    }


}
