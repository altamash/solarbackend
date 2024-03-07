package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.project.activity.ActivityHeadDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhaseDTO {

    private Long id;
    private Long registerHierarchyId;
    private Long parentPhaseId;
    private String externalReferenceId;
    private String phaseName;
    private String level;
    private ProjectHeadDTO projectHead;
    private Set<ActivityHeadDTO> activityHeads;
}
