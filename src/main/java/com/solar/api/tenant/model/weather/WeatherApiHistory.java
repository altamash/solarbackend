package com.solar.api.tenant.model.weather;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_api_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherApiHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String gardenId;
    private String weatherApi;
    private String status;
    private String message ;

}
