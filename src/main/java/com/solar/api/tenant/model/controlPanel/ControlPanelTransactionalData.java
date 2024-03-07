package com.solar.api.tenant.model.controlPanel;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cp_txn_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ControlPanelTransactionalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inverterStatus;
    private String dailyProduction;
    private String inverterHealth;
    private String variantHealth;
    private String errors;
    private String faults;
    private String alerts;
    private String currentTemp;
    private String humidity;
    private String percipitation;
    private Long inverterId;
    private Long variantId;
    private Long locId;
}
