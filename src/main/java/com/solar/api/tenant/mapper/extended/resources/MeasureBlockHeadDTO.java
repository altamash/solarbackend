package com.solar.api.tenant.mapper.extended.resources;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeasureBlockHeadDTO {

    private Long id;
    private String blockName;//field name
    private Long regModuleId;
    private Boolean locked;
    private String deleteInd;
    private Date deleteSchDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
