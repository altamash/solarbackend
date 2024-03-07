package com.solar.api.saas.model.permission.component;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sub_modules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubModule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String subModuleName;

    @Column(length = 15)
    private String subModuleCode;

    @OneToOne
    @JoinColumn(name = "module_id")
    private Modules modules;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
