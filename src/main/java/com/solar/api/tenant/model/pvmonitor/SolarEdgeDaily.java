package com.solar.api.tenant.model.pvmonitor;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "solar_edge_daily")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolarEdgeDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String site;
    private String inverterNumber;
    private String subscriptionId;
    private Double annualYield;
    private Double monthlyYield;
    private Double grossYield;
    @Temporal(TemporalType.DATE)
    private Date day;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}
