package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import com.solar.api.tenant.model.extended.project.activity.task.TaskDetail;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import com.solar.api.tenant.repository.project.ActivityHeadRepository;
import com.solar.api.tenant.repository.project.ResourceAttendanceLogRepository;
import com.solar.api.tenant.repository.project.TaskDetailRepository;
import com.solar.api.tenant.repository.project.TaskHeadRepository;
import com.solar.api.tenant.service.customerSupport.ConversationHeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.activity.task.TaskMapper.toUpdatedTaskDetail;
import static com.solar.api.tenant.mapper.extended.project.activity.task.TaskMapper.toUpdatedTaskHead;


@Service
public class TaskServiceImpl implements TaskService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private TaskHeadRepository taskHeadRepository;
    @Autowired
    private TaskDetailRepository taskDetailRepository;
    @Autowired
    private ActivityHeadRepository activityHeadRepository;
    @Autowired
    private ResourceAttendanceLogRepository resourceAttendanceLogRepository;

    @Autowired
    private ConversationHeadService conversationHeadService;

    @Override
    public TaskHead saveTaskHead(TaskHead taskHead, Long activityId) {
        taskHead.setActivityHead(activityHeadRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ActivityHead.class, activityId)));
        ConversationHead conversationHead = ConversationHead.builder()
                .category("Task")
                .subCategory("Report")
                .priority("Medium")
                .status("Open")
//                .assignee(projectHeadDb.)
                .sourceId(String.valueOf(taskHead.getId()))
                .build();
        try {
            conversationHeadService.add(conversationHead, null, "DOCU");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return taskHeadRepository.save(taskHead);
    }

    @Override
    public TaskHead updateTaskHead(TaskHead taskHead) {
        TaskHead taskHeadDb = findById(taskHead.getId());
        toUpdatedTaskHead(taskHeadDb, taskHead);
        return taskHeadRepository.save(taskHeadDb);
    }

    @Override
    public List<TaskHead> findAllTaskHeads() {
        return taskHeadRepository.findAll();
    }

    @Override
    public List<TaskHead> findAllTaskHeadsByActivityId(Long activityId) {
        List<TaskHead> taskHeads = taskHeadRepository.findByActivityHeadId(activityId);
        if (taskHeads.size()!=0) {
            taskHeads.forEach(task -> {
               Double taskHours = resourceAttendanceLogRepository.hoursByTaskId(task.getId());
               task.setTotalHoursUsed(taskHours != null ? taskHours.longValue(): 0);
            });
        }
        return taskHeads;
    }

    @Override
    public TaskHead findById(Long id) {
        return taskHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(TaskHead.class, id));
    }

    @Override
    public String deleteTaskHead(Long id) {
        TaskHead taskHead = findById(id);
        if (taskHead.getStatus()!=null && taskHead.getStatus().equals("In Plan/Not Started")) {
           List<TaskDetail> taskDetails = taskDetailRepository.findByTaskHead(taskHead);
           if (taskDetails!=null) {
               taskDetailRepository.deleteAll(taskDetails);
           }
            taskHeadRepository.deleteById(id);
          return "Task deleted";
        }
        return "Can't delete the task as its current status is: " + taskHead.getStatus() ;
    }

    @Override
    public void deleteAllTaskHeads() {
        taskHeadRepository.deleteAll();
    }

    @Override
    public TaskDetail saveTaskDetail(TaskDetail taskDetail) {
        return taskDetailRepository.save(taskDetail);
    }

    @Override
    public List<TaskDetail> saveTaskDetails(List<TaskDetail> taskDetails) {
        return taskDetailRepository.saveAll(taskDetails);
    }

    @Override
    public List<TaskDetail> updateTaskDetails(List<TaskDetail> taskDetails) {
        List<TaskDetail> taskDetailUpdated = new ArrayList<>();
        taskDetails.forEach(detail -> {
            TaskDetail taskDetailDb = taskDetailRepository.findById(detail.getId()).get();
            taskDetailUpdated.add(toUpdatedTaskDetail(taskDetailDb, detail));
        });
        return taskDetailRepository.saveAll(taskDetailUpdated);
    }

    @Override
    public TaskDetail findTaskDetailById(Long id) {
        return taskDetailRepository.findById(id).get();
    }

    @Override
    public List<TaskDetail> findAllTaskDetails() {
        return taskDetailRepository.findAll();
    }

    @Override
    public void deleteTaskDetail(Long id) {
        taskDetailRepository.deleteById(id);
    }

    @Override
    public void deleteAllTaskDetails() {
        taskDetailRepository.deleteAll();
    }
}
