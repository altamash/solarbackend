package com.solar.api.tenant.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_config")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String parameter;
    private String description;
    private String varType;
    private Long number;
    private String text;
    private LocalDateTime dateTime;
    private String prefix;
    private String postfix;
    private String format;
    private String category;
    private Boolean locked;
    private String allowedRegex;
    private Boolean masked;
    private Long orgID;
    private String alias;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean isVisible;
}
