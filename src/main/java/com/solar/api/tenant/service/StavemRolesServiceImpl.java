package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.extended.project.EngagementRole;
import com.solar.api.tenant.model.extended.project.Phase;
import com.solar.api.tenant.model.extended.project.ProjectHead;
import com.solar.api.tenant.model.extended.project.ProjectResourceEngagement;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import com.solar.api.tenant.model.extended.resources.HRHead;
import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;
import com.solar.api.tenant.model.stavem.StavemRoles;
import com.solar.api.tenant.model.stavem.StavemThroughCSG;
import com.solar.api.tenant.repository.StavemRolesRepository;
import com.solar.api.tenant.repository.project.*;
import com.solar.api.tenant.service.extended.project.EngagementRoleService;
import com.solar.api.tenant.service.extended.project.ProjectService;
import com.solar.api.tenant.service.extended.resources.HRHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StavemRolesServiceImpl implements StavemRolesService {

    @Autowired
    StavemThroughCSGService stavemThroughCSGService;

    @Autowired
    PhaseRepository phaseRepository;

    @Autowired
    HRHeadService hrHeadService;

    @Autowired
    EngagementRoleService engagementRoleService;

    @Autowired
    ProjectResourceEngagementRepository projectResourceEngagementRepository;

    @Autowired
    ProjectService projectService;

    @Autowired
    StavemRolesRepository stavemRolesRepository;

    @Autowired
    ActivityHeadRepository activityHeadRepository;

    @Autowired
    TaskHeadRepository taskHeadRepository;

    @Autowired
    PrRateDefinitionRepository prRateDefinitionRepository;

    @Autowired
    ResourceAttendanceLogRepository resourceAttendanceLogRepository;


    @Override
    public List<StavemRoles> saveAll(List<StavemRoles> stavemRoles) {
        return stavemRolesRepository.saveAll(stavemRoles);
    }

    @Override
    public List<StavemRoles> findByEmployeeName(String employeeName) {
        return stavemRolesRepository.findByEmployeeName(employeeName);
    }

    @Override
    public List<StavemRoles> getAll() {
        return stavemRolesRepository.findAll();
    }

    @Override
    public StavemRoles findByEmployeeNameAndPhaseId(String employeeName, String phaseId) {
        return stavemRolesRepository.findByEmployeeNameAndPhaseId(employeeName, phaseId);
    }

    @Override
    public ObjectNode dumpProjectData() {
        ObjectNode message = new ObjectMapper().createObjectNode();
        List<Long> ResourceId = new ArrayList<>();
        List<Long> EngagementId = new ArrayList<>();
        List<String> EngagementRoleName = new ArrayList<>();
        List<Long> PhaseId = new ArrayList<>();
        List<String> ExternalPhaseId = new ArrayList<>();


        List<StavemThroughCSG> stavemThroughCSGS = stavemThroughCSGService.getAll();
        for (StavemThroughCSG stavemThroughCSG : stavemThroughCSGS) {
            if (stavemThroughCSG.getEmployee() != null && stavemThroughCSG.getPhase() != null) {
                ProjectHead projectHead = projectService.findByExternalReferenceId(stavemThroughCSG.getJob());
                HRHead hrHead = hrHeadService.findByExternalReferenceId(stavemThroughCSG.getEmployee());
                EngagementRole engagementRole = engagementRoleService.findByExternalRoleId(stavemThroughCSG.getPhase());
                StavemRoles stavemRolesByEmployeeAndPhase = findByEmployeeNameAndPhaseId(hrHead.getName(), engagementRole.getExternalRoleId());
                if (stavemRolesByEmployeeAndPhase != null) {
//                    List<EngagementRole> engagementRoles = engagementRoleService.findByExternalRoleIdAndRoleName(stavemRolesByEmployeeAndPhase.getPhaseId(), stavemRolesByEmployeeAndPhase.getPhaseName());
//                    if (!engagementRoles.isEmpty()) {

                    //Save task
                    TaskHead taskHeadSave = TaskHead.builder()
                            .description("Stavem Import DataDump")
                            .phase(engagementRole.getRoleName())
                            .build();
                    TaskHead taskHead = taskHeadRepository.save(taskHeadSave);

//                        ProjectResourceEngagement engagementRoleExist = projectResourceEngagementService.findByProjectIdAndResourceIdAndTaskId(
//                                projectHead.getId(), hrHead.getId(), taskHead.getId());
//                            if (engagementRoleExist == null) {
//                    PrRateDefinition prRateDefinition = PrRateDefinition.builder()
//                            .rate(stavemThroughCSG.getRate())
//                            .name(stavemRolesByEmployeeAndPhase.getRateName())
//                            .build();
//                    prRateDefinitionRepository.save(prRateDefinition);

                    //Save Attendance Log
                    ResourceAttendanceLog resourceAttendanceLog = ResourceAttendanceLog.builder()
                            .employeeId(hrHead.getId())
                            .projectId(projectHead.getId())
                            .externalRoleId(engagementRole.getExternalRoleId())
                            .taskId(taskHead.getId())
                            .hours(stavemThroughCSG.getHours())
                            .workDate(stavemThroughCSG.getPostingDate())
                            .build();
                    resourceAttendanceLogRepository.save(resourceAttendanceLog);

                    ProjectResourceEngagement projectResourceEngagementExist = projectResourceEngagementRepository.findByProjectIdAndResourceIdAndEngagementRoleIdAndTaskId(
                            projectHead.getId(), hrHead.getId(), engagementRole.getId(), taskHead.getId());
                    ProjectResourceEngagement projectResourceEngagement = new ProjectResourceEngagement();
                    if(projectResourceEngagementExist == null){
                        //Save Project Engagement
                        projectResourceEngagement = ProjectResourceEngagement.builder()
                                .projectId(projectHead.getId())
                                .resourceId(hrHead.getId())
                                .engagementRoleId(engagementRole.getId())
                                .taskId(taskHead.getId())
                                .build();
                        projectResourceEngagementRepository.save(projectResourceEngagement);
                    }
//                            } else {
//                                if (engagementRoleExist.getEngagementRoleId() != engagementRoles.get(0).getId()) {
//                                    //Save PrRateDefinition
//                                    PrRateDefinition prRateDefinition = PrRateDefinition.builder()
//                                            .rate(stavemThroughCSG.getRate())
//                                            .name(stavemRolesByEmployeeAndPhase.getRateName())
//                                            .build();
//                                    prRateDefinitionRepository.save(prRateDefinition);
//
//                                    //Save Attendance Log
//                                    ResourceAttendanceLog resourceAttendanceLog = ResourceAttendanceLog.builder()
//                                            .employeeId(hrHead.getId())
//                                            .projectId(projectHead.getId())
//                                            .taskId(taskHead.getId())
//                                            .hours(stavemThroughCSG.getHours())
//                                            .build();
//                                    resourceAttendanceLogRepository.save(resourceAttendanceLog);
//
//                                    //Save Project Engagement
//                                    ProjectResourceEngagement projectResourceEngagement = ProjectResourceEngagement.builder()
//                                            .projectId(projectHead.getId())
//                                            .resourceId(hrHead.getId())
//                                            .engagementRoleId(engagementRoles.get(0).getId())
//                                            .taskId(taskHead.getId())
//                                            .build();
//                                    projectResourceEngagementService.save(projectResourceEngagement);
//                                } else {
//                                    ResourceId.add(hrHead.getId());
//                                    EngagementId.add(engagementRoles.get(0).getId());
//                                    EngagementRoleName.add(engagementRoles.get(0).getRoleName());
//                                    PhaseId.add(phase.getId());
//                                    ExternalPhaseId.add(phase.getExternalReferenceId());
//                                    message.put("ResourceId", String.valueOf(ResourceId.toArray()));
//                                    message.put("EngagementId", String.valueOf(EngagementId.toArray()));
//                                    message.put("EngagementRoleName", String.valueOf(EngagementRoleName.toArray()));
//                                    message.put("PhaseId", String.valueOf(PhaseId.toArray()));
//                                    message.put("ExternalPhaseId", String.valueOf(ExternalPhaseId.toArray()));
//                                }
                }
            } else {
                message.put("Data Not Found ", "Employee Id: " + stavemThroughCSG.getEmployee() + " Role Name: " + stavemThroughCSG.getPhase());
            }
//                    } else{
//                        message.put("EnagementRole Not Found ", "Phase Id: " + phase.getId()+"Role Name: " + stavemRolesByEmployeeAndPhase.getRoleName());
//                    }
//                } else {
//                    message.put("EnagementRole Not Found ", "Phase Id: " + phase.getId()+"Role Name: " + stavemRolesByEmployeeAndPhase.getRoleName());
//                }
        }
        return message;
    }

    @Override
    public ObjectNode dumpAttendanceLogs() {
        ObjectNode message = new ObjectMapper().createObjectNode();

        List<StavemThroughCSG> stavemThroughCSGS = stavemThroughCSGService.getAll();
        for (StavemThroughCSG stavemThroughCSG : stavemThroughCSGS) {
            HRHead hrHead = hrHeadService.findByExternalReferenceId(stavemThroughCSG.getEmployee());
            List<ResourceAttendanceLog> resourceAttendanceLog = resourceAttendanceLogRepository.findByEmployeeIdAndExternalRoleIdAndHours(hrHead.getId(),
                    stavemThroughCSG.getPhase(), stavemThroughCSG.getHours());
            if (!resourceAttendanceLog.isEmpty()) {
                resourceAttendanceLog.get(0).setWorkDate(stavemThroughCSG.getPostingDate());
                resourceAttendanceLogRepository.save(resourceAttendanceLog.get(0));
            }
        }
        message.put("message", "Data Dumped");

        return message;
    }

    @Override
    public ObjectNode dumpPhases() {
        ObjectNode message = new ObjectMapper().createObjectNode();
        final int[] count = {0};
        List<StavemRoles> stavemRoles = getAll();
        stavemRoles.forEach(stavemRole -> {
            EngagementRole engagementRole = EngagementRole.builder()
                    .externalRoleId(stavemRole.getPhaseId())
                    .roleName(stavemRole.getPhaseName())
                    .build();
            engagementRoleService.saveOrUpdate(engagementRole);
            count[0] = count[0] + 1;
        });
        message.put("message", count[0] + "Values Dumped");
        return message;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public ObjectNode dumpEngagementRoles() {

        List<StavemRoles> stavemRoles = getAll();
        for (StavemRoles stavemRole : stavemRoles) {
            if (stavemRole.getRoleName() != null) {
                Phase phase = phaseRepository.findByExternalReferenceId(stavemRole.getPhaseId());
                EngagementRole engagementRole = EngagementRole.builder()
                        .externalRoleId(phase.getExternalReferenceId())
                        .roleName(phase.getPhaseName())
                        .build();
                engagementRoleService.saveOrUpdate(engagementRole);
            }
        }
        ObjectNode message = new ObjectMapper().createObjectNode();
        message.put("message", "Data binding in progress");
        return message;
    }
}