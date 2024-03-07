package com.solar.api.tenant.mapper.extended.partner;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class PartnerHeadDTO {

    private Long partnerId;
    private Long registerId;
    private String refName;
    private String description;
    private Date registrationDate;
    private String status;
    private Boolean recordLevelInd;
    private List<PartnerDetailDTO> partnerDetails;
    private String type;
    private String engagement;
    private Date startDate;
    private Date endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
