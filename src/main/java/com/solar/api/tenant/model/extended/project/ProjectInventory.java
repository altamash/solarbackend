package com.solar.api.tenant.model.extended.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solar.api.tenant.mapper.extended.assetHead.AssetHeadDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "project_inventories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private Long assetId;
    private String modelNumber;
    private Long listId;
    private Long quantity;
    private String status;
    private Long taskId;
    private String location;
    private Date installDate;
    private Date activationDate;
    private String inOperation;
    private Date expirationDate;
    private String relatedProject;
    private String currency;
    /*@Transient
    private List<String> assetSerialNumbers;
*/
    @Transient
    private String assetSerialNumber;
    @Transient
    private Long serialNumberCount;
    @JsonIgnore
    @OneToMany(mappedBy = "projectInventory", cascade = CascadeType.MERGE)
    private List<ProjectInventorySerial> projectInventorySerials;

    @Transient
    private AssetHeadDTO assetHeadDTO;
    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
