package com.solar.api.tenant.model.extended.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "hr_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HRHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private Long registerId;
    private Long contractId;
    private String reportsTo;
    private Date dateEntered;
    private String empStatus;
    private Long loginUser;
    private Long enteredBy;
    private String employmentType;
    private String referenceSource;
    private String externalReferenceId;
    private String encodedId;

    @JsonIgnore
    @OneToMany(mappedBy = "hrHead", cascade = CascadeType.MERGE)
    private List<HRDetail> hrDetails;

    private String type;
    private String designation;
    private Date startDate;
    private Date endDate;
    private String photoIdType;
    private Long photoId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
