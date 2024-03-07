package com.solar.api.tenant.model.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entity_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "entity_id")
    private com.solar.api.tenant.model.contract.Entity entity;
    @Column(columnDefinition = "false", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "functional_role_id")
    private FunctionalRoles functionalRoles;

//    @OneToMany(mappedBy = "entityRole")
//    private List<EntityGroup> entityGroups;


    private boolean status;
    private String createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
