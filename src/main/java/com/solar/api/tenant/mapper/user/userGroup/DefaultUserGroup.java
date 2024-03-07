package com.solar.api.tenant.mapper.user.userGroup;

import com.solar.api.tenant.model.extended.FunctionalRoles;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "default_user_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultUserGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_entity_id")
    private com.solar.api.tenant.model.contract.Entity entity;
    @ManyToOne
    @JoinColumn(name = "fk_function_role_id")
    private FunctionalRoles functionalRoles;
    private String status;
    @Transient
    private Long fkFunctionRoleId;
    @Transient
    private Long fkEntityId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String createdBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String updatedBy;
}
