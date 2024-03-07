package com.solar.api.tenant.model.pvmonitor;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "mrd_quarters_wise")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorReadingQuarterWise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private String subscriptionIdMongo;
    private Double yield;
    private String inverterNumber;

}
