package com.solar.api.tenant.model.extended.project;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "week_schedule_config")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekScheduleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String weekName;
    private String description;
    private Long mon;
    private Long tue;
    private Long wed;
    private Long thu;
    private Long fri;
    private Long sat;
    private Long sun;
    private Long projectId;

}
