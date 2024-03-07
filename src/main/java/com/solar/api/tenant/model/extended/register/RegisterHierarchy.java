package com.solar.api.tenant.model.extended.register;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "register_hierarchy", uniqueConstraints = @UniqueConstraint(columnNames = {"code", "parentId"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterHierarchy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer level;
    private String name;
    @Column(length = 10)
    private String code;
    private Integer sequenceNo;
    private String alias;
    private String description;
    private String category;
    private String parent;
    private Long parentId;
    private Boolean registered;

    @Transient
    private List<RegisterHierarchy> subHierarchies;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
