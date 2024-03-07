package com.solar.api.tenant.model.extended.register;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "register_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String refName;
    private Boolean recordLevelInd;
    private String description;
    private String status;
    private Date createDate;

    @JsonIgnore
    @OneToMany(mappedBy = "registerHead", cascade = CascadeType.MERGE)
    private List<RegisterDetail> registerDetails;
    private Long regModuleId;

    @Transient
    RegisterHierarchy registerHierarchy;
    @Transient
    private String blocks;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
