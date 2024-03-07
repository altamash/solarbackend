package com.solar.api.tenant.model.weather;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "weather_impact_description")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherImpactDescription {
    @Id
    private Long id;
    private String weatherCode;
    private String impact_percentage;
    private String description;
//    private String uri;

}