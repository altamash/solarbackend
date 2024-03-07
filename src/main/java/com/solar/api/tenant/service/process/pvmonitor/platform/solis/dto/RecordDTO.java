package com.solar.api.tenant.service.process.pvmonitor.platform.solis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordDTO {

    //  Solis

    private String sno;
    private Double power;
    private Double capacity;
    //private Double dayEnergy;
   // private Double monthEnergy;
   // private Double yearEnergy;

    private String sn;
    private String time;
    private Double pac;
    @JsonProperty("eToday")
    private Double eToday;
    @JsonProperty("eMonth")
    private Double eMonth;
    @JsonProperty("eYear")
    private Double eYear;
    @JsonProperty("eTotal")
    private Double eTotal;
    private Double powerFactor;
    private Long pow1;
    private Long pow2;
    private Long pow3;
    private Long pow4;
    private Long pow5;
    private Long pow6;
    private Long pow7;
    private Long pow8;
    private Long pow9;
    private Long pow10;
    private Long pow11;
    private Long pow12;
    private Long pow13;
    private Long pow14;
    private Long pow15;
    private Long pow16;
    private Long pow17;
    private Long pow18;
    private Long pow19;
    private Long pow20;
    private Long pow21;
    private Long pow22;
    private Long pow23;
    private Long pow24;
    private Long pow25;
    private Long pow26;
    private Long pow27;
    private Long pow28;
    private Long pow29;
    private Long pow30;
    private Long pow31;
    private Long pow32;

}
