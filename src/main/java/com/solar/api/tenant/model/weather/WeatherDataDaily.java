package com.solar.api.tenant.model.weather;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data_daily",indexes = {@Index(columnList = "validTimeLocal")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDataDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceType;
    private LocalDateTime createdAt;
    private Integer cloudCover;
    private String dayOfWeek;
    private String dayOrNight;
    private String expirationTimeUtc;
    private Integer iconCode;
    private Integer iconCodeExtend;
    private Integer precipChance;
    private String precipType;
    private Float pressureMeanSeaLevel;
    private Double qpf;
    private Double qpfSnow;
    private Integer relativeHumidity;
    private Integer temperature;
    private Integer temperatureDewPoint;
    private Integer temperatureFeelsLike;
    private Integer temperatureHeatIndex;
    private Integer temperatureWindChill;
    private String uvDescription;
    private Integer uvIndex;
    private String validTimeLocal;
    private String validTimeUtc;
    private Float visibility;
    private Integer windDirection;
    private String windDirectionCardinal;
    private Float windGust;
    private Integer windSpeed;
    private String wxPhraseLong;
    private String wxPhraseShort;
    private Integer wxSeverity;
    private String gardenId;
}
