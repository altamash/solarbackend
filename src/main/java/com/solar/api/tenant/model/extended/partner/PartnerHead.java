package com.solar.api.tenant.model.extended.partner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "partner_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long registerId;
    private String refName;
    private Boolean recordLevelInd;
    private String description;
    private Date registrationDate;
    private String status;

    @JsonIgnore
    @OneToMany(mappedBy = "partnerHead", cascade = CascadeType.MERGE)
    private List<PartnerDetail> partnerDetails;

    private String type;
    private String engagement;
    private Date startDate;
    private Date endDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
