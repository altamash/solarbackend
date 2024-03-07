package com.solar.api.tenant.mapper.extended.resources;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class HRHeadDTO {

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

    private List<HRDetailDTO> hrDetails;
    private String type;
    private String designation;
    private Date startDate;
    private Date endDate;
    private String photoIdType;
    private Long photoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
