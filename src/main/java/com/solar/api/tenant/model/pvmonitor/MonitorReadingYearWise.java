package com.solar.api.tenant.model.pvmonitor;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "mrd_year_wise")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorReadingYearWise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private String subscriptionIdMongo;
    private Double yield;
    private String inverterNumber;

    public MonitorReadingYearWise(Double yield, String day){
        this.yield = yield;
        this.day = day;
    }
}
