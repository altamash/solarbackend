package com.solar.api.saas.model.extended;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "components_by_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentsByRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long componentId;
    private String roleId;
    private String permissions;
    private String approverRole;
    private Double minimumThreshold;
    private Double maximumThreshold;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
