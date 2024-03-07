package com.solar.api.tenant.model.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordedData {

    private String inverterNumber;
    private BigDecimal min30;
    private BigDecimal min25;
    private BigDecimal min20;
    private BigDecimal min15;
    private BigDecimal min10;
    private BigDecimal min5;
    private BigDecimal min0;
    private BigDecimal startOfDay;
    private BigDecimal startOfMonth;
    private BigDecimal startOfYear;
    private String durl;
    private Double peakValue;
    private Long userId;
    private String subscriptionId;
    private Date lastTime;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}
