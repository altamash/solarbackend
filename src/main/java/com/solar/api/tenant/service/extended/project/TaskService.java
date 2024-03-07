package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.project.activity.task.TaskDetail;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;

import java.util.List;

public interface TaskService {

    TaskHead saveTaskHead(TaskHead taskHead, Long activityId);

    TaskHead updateTaskHead(TaskHead taskHeadD);

    List<TaskHead> findAllTaskHeads();

    List<TaskHead> findAllTaskHeadsByActivityId(Long activityId);

    TaskHead findById(Long id);

    String deleteTaskHead(Long id);

    void deleteAllTaskHeads();

    ///////TaskDetail/////
    TaskDetail saveTaskDetail(TaskDetail taskDetail);

    List<TaskDetail> saveTaskDetails(List<TaskDetail> taskDetails);

    List<TaskDetail> updateTaskDetails(List<TaskDetail> taskDetails);

    TaskDetail findTaskDetailById(Long id);

    List<TaskDetail> findAllTaskDetails();

    void deleteTaskDetail(Long id);

    void deleteAllTaskDetails();
}
