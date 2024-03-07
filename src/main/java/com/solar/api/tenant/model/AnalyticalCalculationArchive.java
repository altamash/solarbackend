package com.solar.api.tenant.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "analytical_calculations_archive")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticalCalculationArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private Long subscriptionId;
    private String scope;
    private String analysis;
    private Double oldValue;
    private Double currentValue;
    private Long movingAverage;
    private Date lastUpdatedDatetime;
}
