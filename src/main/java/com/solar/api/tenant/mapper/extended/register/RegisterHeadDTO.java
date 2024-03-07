package com.solar.api.tenant.mapper.extended.register;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterHeadDTO {

    private Long id;
    private String refName;
    private Boolean recordLevelInd;
    private String description;
    private String status;
    private Date createDate;
    private Long subRefPage;
    private Long subRefId;
    private List<RegisterDetailDTO> registerDetails;
    private Long regModuleId;
    private RegisterHierarchyDTO registerHierarchy;
    private String blocks;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
