package com.solar.api.tenant.mapper.extended.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LinkedUnLinkedDependenciesDTO {

    private Long id;
    private String fieldName;
    //for dep relation check
    private Long projectId;
    private String relatedAt;
    private Long relatedId;
    private String depType;
}
