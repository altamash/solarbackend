package com.solar.api.saas.model.preferences;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_themes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String scope;
    private Long userId;
    private String themeArea;
    private String color;
    private String opacity;
    private String themeType;
    private String fontFamily;
    private String target;
    private String fontSize;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
