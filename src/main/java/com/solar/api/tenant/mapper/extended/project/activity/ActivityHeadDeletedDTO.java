package com.solar.api.tenant.mapper.extended.project.activity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ActivityHeadDeletedDTO {

    private Long id;
    private Long activityId;
    private Long projectId;
    private String comments;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
