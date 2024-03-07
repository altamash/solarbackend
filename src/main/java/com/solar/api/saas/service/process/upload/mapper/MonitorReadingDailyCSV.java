package com.solar.api.saas.service.process.upload.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorReadingDailyCSV {
//    private String rowNumber;

//    private String action;
    @JsonProperty("projected")
    private Double projected;
    @JsonProperty("years")
    private String years;
    @JsonProperty("months")
    private String months;
    @JsonProperty("days")
    private String days;
    @JsonProperty("quarters")
    private String quarters;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "MonitorReadingDailyCSV{" +
//                "rowNumber='" + rowNumber + '\'' +
//                ", action='" + action + '\'' +
                ", projected=" + projected +
                ", years='" + years + '\'' +
                ", months='" + months + '\'' +
                ", days='" + days + '\'' +
                ", quarters='" + quarters + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
