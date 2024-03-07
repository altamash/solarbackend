package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.activity.task.TaskDetail;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskDetailRepository extends JpaRepository<TaskDetail, Long> {
    List<TaskDetail> findByTaskHead(TaskHead taskHead);
}
