package com.solar.api.tenant.mapper.extended.project;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDependenciesViewListDTO {

    List<ActivityHead> activityHeadList;
    List<TaskHead> taskHeadList;

}
