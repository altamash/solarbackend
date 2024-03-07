package com.solar.api.tenant.controller.v1.extended;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.extended.project.activity.ActivityDetailDTO;
import com.solar.api.tenant.mapper.extended.project.activity.ActivityHeadDTO;
import com.solar.api.tenant.service.extended.project.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.activity.ActivityMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ActivityController")
@RequestMapping(value = "/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // Activity Head ////////////////////////////////////////
    @PostMapping("/head/{projectHeadId}")
    public ActivityHeadDTO addActivityHead(@RequestBody ActivityHeadDTO activityHeadDTO, @PathVariable Long projectHeadId) {
        return toActivityHeadDTO(activityService.saveActivityHead(toActivityHead(activityHeadDTO), projectHeadId));
    }

    @PutMapping("/head")
    public ActivityHeadDTO updateActivityHead(@RequestBody ActivityHeadDTO activityHeadDTO) {
        return toActivityHeadDTO(activityService.updateActivityHead(toActivityHead(activityHeadDTO)));
    }

    @GetMapping("/head/{id}")
    public ActivityHeadDTO findActivityHeadById(@PathVariable Long id) {
        return toActivityHeadDTO(activityService.findById(id));
    }

    @GetMapping("/head/byProjectId/{id}")
    public List<ActivityHeadDTO> findAllActivityByProjectId(@PathVariable Long id){
        return toActivityHeadDTOs(activityService.findAllActivityByProjectId(id));
    }

    @GetMapping("/head")
    public List<ActivityHeadDTO> findAllActivityHeads() {
        return toActivityHeadDTOs(activityService.findAllActivityHeads());
    }

    @DeleteMapping("/head/{id}/{comments}")
    public ObjectNode deleteActivityHead(@PathVariable Long id, @PathVariable String comments) {
        return activityService.deleteActivityHead(id, comments);
    }

    @DeleteMapping("/head")
    public ResponseEntity deleteAllActivityHeads() {
        activityService.deleteAllActivityHeads();
        return new ResponseEntity(HttpStatus.OK);
    }

    // ActivityDetail ////////////////////////////////////////
    @PostMapping("/detail")
    public ActivityDetailDTO addActivityDetail(@RequestBody ActivityDetailDTO activityDetailDTO) {
        return toActivityDetailDTO(activityService.saveActivityDetail(toActivityDetail(activityDetailDTO)));
    }

    @PutMapping("/detail")
    public ActivityDetailDTO updateActivityDetail(@RequestBody ActivityDetailDTO activityDetailDTO) {
        return toActivityDetailDTO(activityService.saveActivityDetail(toActivityDetail(activityDetailDTO)));
    }

    @GetMapping("/detail/{id}")
    public ActivityDetailDTO findActivityDetailById(@PathVariable Long id) {
        return toActivityDetailDTO(activityService.findActivityDetailById(id));
    }

    @GetMapping("/detail")
    public List<ActivityDetailDTO> findAllActivityDetails() {
        return toActivityDetailDTOs(activityService.findAllActivityDetails());
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity deleteActivityDetail(@PathVariable Long id) {
        activityService.deleteActivityDetail(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/detail")
    public ResponseEntity deleteAllActivityDetails() {
        activityService.deleteAllActivityDetails();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/head/totalHours/{employeeId}/{projectId}")
    public ObjectNode getTotalHours(@PathVariable Long employeeId, @PathVariable Long projectId ) {
        Double totalHrs = activityService.getTotalHours(employeeId,projectId);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("totalHours",totalHrs);
        return objectNode;
    }
}
