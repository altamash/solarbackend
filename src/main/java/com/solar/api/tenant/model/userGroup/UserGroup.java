package com.solar.api.tenant.model.userGroup;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String userGroupName;
    private String userGroupType;
    private boolean status;
    @Column(columnDefinition = "true")
    private boolean isActive;
    private String refType;
    private String refId;
    private String parentId;

//    @OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private List<EntityGroup> entityGroups;

    private String createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(columnDefinition = "false", nullable = false)
    private Boolean isDeleted;
}
