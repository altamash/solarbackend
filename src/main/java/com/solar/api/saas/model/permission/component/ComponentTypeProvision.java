package com.solar.api.saas.model.permission.component;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "component_type_provision")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentTypeProvision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String compReference;
    private Boolean ra; // read all
    private Boolean r; // read
    private Boolean w; // write
    private Boolean e; // execute
    private Boolean d; // delete
    private Boolean u; // update

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
