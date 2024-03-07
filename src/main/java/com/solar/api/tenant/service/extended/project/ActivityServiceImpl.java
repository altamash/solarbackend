package com.solar.api.tenant.service.extended.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.extended.project.ProjectDependencies;
import com.solar.api.tenant.model.extended.project.activity.ActivityDetail;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import com.solar.api.tenant.model.extended.project.activity.ActivityHeadDeleted;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import com.solar.api.tenant.repository.project.*;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.customerSupport.ConversationHeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.activity.ActivityMapper.toUpdatedActivityDetail;
import static com.solar.api.tenant.mapper.extended.project.activity.ActivityMapper.toUpdatedActivityHead;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private ActivityHeadRepository activityHeadRepository;
    @Autowired
    private ActivityDetailRepository activityDetailRepository;
    @Autowired
    private FinancialAccrualRepository financialAccrualRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectDependenciesRepository projectDependenciesRepository;
    @Autowired
    private ActivityHeadDeletedRepository activityHeadDeletedRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PhaseService phaseService;
    @Autowired
    private ConversationHeadService conversationHeadService;

    @Override
    public ActivityHead saveActivityHead(ActivityHead activityHead, Long projectHeadId) {
        activityHead.setProjectHead(projectService.findById(projectHeadId));
        activityHead.setProjectId(activityHead.getProjectHead().getId());
        activityHead.setPhase(phaseService.findById(activityHead.getPhaseId()));
        if (!activityHead.getActivityDetails().isEmpty()) {
            activityDetailRepository.saveAll(activityHead.getActivityDetails());
        }
        ActivityHead activityHeadReturn = activityHeadRepository.save(activityHead);
        ConversationHead conversationHead = ConversationHead.builder()
                .category("Activity")
                .subCategory("Report")
                .priority("Medium")
                .status("Open")
//                .assignee(projectHeadDb.)
                .sourceId(String.valueOf(activityHeadReturn.getId()))
                .build();
        try {
            conversationHeadService.add(conversationHead, null, "DOCU");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return activityHeadReturn;
    }

    @Override
    public ActivityHead updateActivityHead(ActivityHead activityHead) {
        ActivityHead activityHeadDb = findById(activityHead.getId());
        activityHeadDb = toUpdatedActivityHead(activityHeadDb, activityHead);
        return activityHeadRepository.save(activityHeadDb);
    }

    @Override
    public List<ActivityHead> findAllActivityHeads() {
        return activityHeadRepository.findAll();
    }

    @Override
    public List<ActivityHead> findAllActivityByProjectId(Long projectId) {
        return activityHeadRepository.findByProjectHeadId(projectId);
    }



    @Override
    public ObjectNode deleteActivityHead(Long id, String comments) {

        ObjectNode response = new ObjectMapper().createObjectNode();
        ActivityHead activityHead = findById(id);
        List<TaskHead> taskHeads = taskService.findAllTaskHeadsByActivityId(id);
        List<ProjectDependencies> activityPD = projectDependenciesRepository.getAllActivityByActivityIdAndRelatedId(id);

        if (taskHeads.size() != 0) {
            return response.put("error", "Activity can not be deleted due to having a parent relationship.");

        } else if (activityPD.size() != 0) {
            return response.put("error", "Activity can not be deleted due to created dependency.");

        } else if (activityHead != null && activityHead.getAssigneeId() != null) {
            return response.put("error", "Activity can not be deleted due to inventory / resource assigned to it.");

        } else {
            activityHeadDeletedRepository.save(ActivityHeadDeleted.builder()
                    .activityId(id)
                    .projectId(activityHead.getProjectHead().getId())
                    .comments(comments)
                    .updatedBy(userService.getLoggedInUser().getAcctId())
                    .updatedAt(LocalDateTime.now())
                    .build());
            activityHeadRepository.deleteById(id);
            return response.put("message", "Activity has been deleted.");
        }
    }

    @Override
    public void deleteAllActivityHeads() {
        activityHeadRepository.deleteAll();
    }

    @Override
    public ActivityHead findById(Long id) {
        ActivityHead activityHead =
                activityHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(ActivityHead.class, id));
        /*activityHead.getActivityDetails().forEach(detail->{
            MeasureDefinition measureDefinitionDb = measureDefinitionService.findById(detail.getMeasureCodeId());
            detail.setMeasureDefinition(measureDefinitionDb);
        });*/
        return activityHead;
    }

    @Override
    public List<ActivityDetail> saveActivityDetails(List<ActivityDetail> activityDetails) {
        return activityDetailRepository.saveAll(activityDetails);
    }

    @Override
    public ActivityDetail saveActivityDetail(ActivityDetail activityDetail) {
        return activityDetailRepository.save(activityDetail);
    }

    @Override
    public List<ActivityDetail> updateActivityDetails(List<ActivityDetail> activityDetails) {
        List<ActivityDetail> activityDetailUpdated = new ArrayList<>();
        activityDetails.forEach(detail -> {
            ActivityDetail activityDetailDb = activityDetailRepository.findById(detail.getId()).get();
            activityDetailUpdated.add(toUpdatedActivityDetail(activityDetailDb, detail));
        });
        return activityDetailRepository.saveAll(activityDetailUpdated);
    }

    @Override
    public ActivityDetail findActivityDetailById(Long id) {
        return activityDetailRepository.findById(id).orElseThrow(() -> new NotFoundException(ActivityDetail.class, id));
    }

    @Override
    public List<ActivityDetail> findAllActivityDetails() {
        return activityDetailRepository.findAll();
    }

    @Override
    public void deleteActivityDetail(Long id) {
        activityDetailRepository.deleteById(id);
    }

    @Override
    public void deleteAllActivityDetails() {
        activityDetailRepository.deleteAll();
    }

    @Override
    public Double getTotalHours(Long employeeId, Long projectId) {
        Double totalHrs = financialAccrualRepository.getSumOfAccruedAmount(projectId,employeeId);
        return totalHrs!= null ? totalHrs: 0.0;
    }
}
