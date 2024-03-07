package com.solar.api.tenant.model.pvmonitor;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mrd_day_wise")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorReadingDayWise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private String subscriptionIdMongo;
    private Double yield;
    private String inverterNumber;
}
