package com.solar.api.tenant.model.weather;

import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer calendarDayTemperatureMax;
    private Integer calendarDayTemperatureMin;
    private String dayOfWeek;
    private String expirationTimeUtc;
    private String moonPhase;
    private String moonPhaseCode;
    private Integer moonPhaseDay;
    private String moonriseTimeUtc;
    private String moonsetTimeUtc;
    private String narrative;
    private Double qpf;
    private Double qpfSnow;
    private String sunriseTimeUtc;
    private String sunsetTimeUtc;
    private String sunriseTimeLocal;
    private String sunsetTimeLocal;
    private Integer temperatureMax;
    private Integer temperatureMin;
    private String validTimeUtc;
    private String validTimeLocal;
    private Integer cloudCover;
    private String dayOrNight;
    private String daypartName;
    private String narrative2;
    private String precipChance;
    private String precipType;
    private Double qpf2;
    private Double qpfSnow2;
    private Double relativeHumidity;
    private String snowRange;
    private Integer temperature;
    private Integer temperatureHeatIndex;
    private Integer temperatureWindChill;
    private String thunderCategory;
    private Integer thunderIndex;
    private String uvDescription;
    private Integer windDirection;
    private String windPhrase;
    private Integer windSpeed;
    private String wxPhraseLong;
    private String wxPhraseShort;
    private String gardenId;
    private String timeZone;
    private String timeZoneCode;
}
