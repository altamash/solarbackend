package com.solar.api.tenant.model.stavem;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stavem_through_csg")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StavemThroughCSG {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String prCo;
    private String prGroup;
    private String employee;
    private String firstName;
    private String lastName;
    private String postingDate;
    private String postingType;
    private String jcCompany;
    private String job;
    private String phase;
    private String smCompany;
    private String smWorkOrder;
    private String smScope;
    private String smPayType;
    private String smCostType;
    private String earnCode;
    private String hours;
    private String rate;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
