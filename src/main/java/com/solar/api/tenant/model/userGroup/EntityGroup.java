package com.solar.api.tenant.model.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entity_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean status;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "entity_role_id")
    private EntityRole entityRole;
    @Column(columnDefinition = "false", nullable = false)
    private Boolean isDeleted;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_group_id")
    private UserGroup userGroup;

    private String createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
