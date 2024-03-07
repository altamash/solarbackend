package com.solar.api.saas.model.preferences;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "unit_conversion_rates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitConversionRates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String baseUnit;
    private String conversionUnit;
    private String conversionRate;
    private String conversionFormula;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
