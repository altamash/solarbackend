package com.solar.api.tenant.model.extended.pallet;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rack_space_maps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RackSpaceMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;
    private String areaCode;
    private String block;
    private String lane;
    private String side;
    private Long height;
    private Long depth;
    private Long externalRefId;
    private String status;
    private String reservedFor;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
