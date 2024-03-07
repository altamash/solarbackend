package com.solar.api.tenant.controller.v1.extended;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.extended.project.activity.task.TaskDetailDTO;
import com.solar.api.tenant.mapper.extended.project.activity.task.TaskHeadDTO;
import com.solar.api.tenant.service.extended.project.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.activity.task.TaskMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("TaskController")
@RequestMapping(value = "/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Task Head ////////////////////////////////////////
    @PostMapping("/head/{activityHeadId}")
    public TaskHeadDTO addTaskHead(@RequestBody TaskHeadDTO taskHeadDTO, @PathVariable Long activityHeadId) {
        return toTaskHeadDTO(taskService.saveTaskHead(toTaskHead(taskHeadDTO),activityHeadId));
    }

    @PutMapping("/head")
    public TaskHeadDTO updateTaskHead(@RequestBody TaskHeadDTO taskHeadDTO) {
        return toTaskHeadDTO(taskService.updateTaskHead(toTaskHead(taskHeadDTO)));
    }

    @GetMapping("/head/{id}")
    public TaskHeadDTO findTaskHeadById(@PathVariable Long id) {
        return toTaskHeadDTO(taskService.findById(id));
    }

    @GetMapping("/head")
    public List<TaskHeadDTO> findAllTaskHeads() {
        return toTaskHeadDTOs(taskService.findAllTaskHeads());
    }

    @GetMapping("/head/findByActivity/{activityId}")
    public List<TaskHeadDTO> findAllTaskHeadsByActivityId(@PathVariable Long activityId) {
        return toTaskHeadDTOs(taskService.findAllTaskHeadsByActivityId(activityId));
    }

    @DeleteMapping("/head/{id}")
    public ObjectNode deleteTaskHead(@PathVariable Long id) {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("msg", taskService.deleteTaskHead(id));
        return messageJson;
    }

    @DeleteMapping("/head")
    public ResponseEntity deleteAllTaskHeads() {
        taskService.deleteAllTaskHeads();
        return new ResponseEntity(HttpStatus.OK);
    }

    // Task Detail ////////////////////////////////////////
    @PostMapping("/detail")
    public TaskDetailDTO addTaskDetail(@RequestBody TaskDetailDTO taskDetailDTO) {
        return toTaskDetailDTO(taskService.saveTaskDetail(toTaskDetail(taskDetailDTO)));
    }

    @PutMapping("/detail")
    public TaskDetailDTO updateTaskDetail(@RequestBody TaskDetailDTO taskDetailDTO) {
        return toTaskDetailDTO(taskService.saveTaskDetail(toTaskDetail(taskDetailDTO)));
    }

    @GetMapping("/detail/{id}")
    public TaskDetailDTO findTaskDetailById(@PathVariable Long id) {
        return toTaskDetailDTO(taskService.findTaskDetailById(id));
    }

    @GetMapping("/detail")
    public List<TaskDetailDTO> findAllTaskDetails() {
        return toTaskDetailDTOs(taskService.findAllTaskDetails());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deleteTaskDetail(@PathVariable Long id) {
        taskService.deleteTaskDetail(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllTaskDetails() {
        taskService.deleteAllTaskDetails();
        return new ResponseEntity(HttpStatus.OK);
    }
}
