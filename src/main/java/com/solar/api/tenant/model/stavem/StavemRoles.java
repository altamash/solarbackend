package com.solar.api.tenant.model.stavem;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stavem_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StavemRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employeeName;
    private String phaseId;
    private String phaseName;
    private String roleName;
    private String rateName;
    private String rate;
    private String hours;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
