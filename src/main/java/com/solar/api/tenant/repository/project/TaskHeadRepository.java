package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHeadRepository extends JpaRepository<TaskHead, Long> {

    List<TaskHead> findByActivityHeadId(Long activityId);
    List<TaskHead> findByActivityHeadIdIn(List<Long> activityIds);

}
