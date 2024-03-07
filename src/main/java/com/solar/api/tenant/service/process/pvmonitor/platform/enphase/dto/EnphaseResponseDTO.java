package com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnphaseResponseDTO {
    @JsonProperty("system_id")
    private Integer systemId;
    private String siteStatus;
//    "pending" -> null
//    "next_report" -> null
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    private List<Stat> stats;
    private Boolean isExportRate;
    private Boolean isImportRate;
//            "savings" -> {ArrayList@28234}  size = 2
    private StatusDetails statusDetails;
//            "battery_details" -> null
    @JsonProperty("update_pending")
    private Boolean updatePending;
//            "storm_guard_events" -> {LinkedHashMap@28236}  size = 0
//            "envoy_reboot_events" -> {LinkedHashMap@28237}  size = 0
    @JsonProperty("latest_power")
    private LatestPower latestPower;
//            "batteryChanges" -> {LinkedHashMap@28239}  size = 0
    @JsonProperty("last_report_date")
    private Integer lastReportDate;
//            "connectionDetails" -> {ArrayList@28241}  size = 1
//            "batteryConfig" -> {LinkedHashMap@28242}  size = 29
    private System system;
//            "loggers" -> {ArrayList@28244}  size = 16


    @Override
    public String toString() {
        return "EnphaseResponseDTO{" +
                "systemId=" + systemId +
                ", siteStatus='" + siteStatus + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", stats=" + stats +
                ", isExportRate=" + isExportRate +
                ", isImportRate=" + isImportRate +
                ", statusDetails=" + statusDetails +
                ", updatePending=" + updatePending +
                ", latestPower=" + latestPower +
                ", lastReportDate=" + lastReportDate +
                ", system=" + system +
                '}';
    }
}
