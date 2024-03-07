package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.project.*;
import com.solar.api.tenant.model.extended.project.EngagementRateGroups;
import com.solar.api.tenant.model.extended.project.EngagementRole;
import com.solar.api.tenant.model.extended.project.ProjectHead;
import com.solar.api.tenant.model.extended.project.ProjectResourceEngagement;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import com.solar.api.tenant.model.extended.resources.HRHead;
import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;
import com.solar.api.tenant.model.stavem.StavemRoles;
import com.solar.api.tenant.repository.StavemRolesRepository;
import com.solar.api.tenant.repository.project.ProjectResourceEngagementRepository;
import com.solar.api.tenant.repository.project.ResourceAttendanceLogRepository;
import com.solar.api.tenant.repository.project.TaskHeadRepository;
import com.solar.api.tenant.service.extended.resources.HRHeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectResourceEngagementServiceImpl implements ProjectResourceEngagementService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectResourceEngagementRepository projectResourceEngagementRepository;
    @Autowired
    private ResourceAttendanceLogRepository resourceAttendanceLogRepository;
    @Autowired
    private StavemRolesRepository stavemRolesRepository;
    @Autowired
    private EngagementRateGroupsService engagementRateGroupsService;
    @Autowired
    private EngagementRoleService engagementRoleService;
    @Autowired
    private HRHeadService hrHeadService;
    @Autowired
    private TaskHeadRepository taskHeadRepository;
    @Autowired
    private ProjectService projectService;

    @Override
    public ProjectResourceEngagement save(ProjectResourceEngagement projectResourceEngagement) {
        return projectResourceEngagementRepository.save(projectResourceEngagement);
    }

    @Override
    public List<ProjectResourceEngagement> findAllByProjectId(Long projectId) {
        List<ProjectResourceEngagement> projectResourceEngagements = projectResourceEngagementRepository.findAllByProjectId(projectId);
        projectResourceEngagements.forEach(resource -> {
            if (resource.getResourceId() != null) {
                HRHead hrHead = hrHeadService.findById(resource.getResourceId());
                String resName = (hrHead.getFirstName() != null ? hrHead.getFirstName() : "") +
                        (hrHead.getMiddleName() != null ? hrHead.getMiddleName() : "") +
                        (hrHead.getLastName() != null ? hrHead.getLastName() : "");
                resource.setName(resName);
            }
            if (resource.getEngagementRateGroupId() != null) {
                EngagementRateGroups engagementRateGroups = engagementRateGroupsService.findById(resource.getEngagementRateGroupId());
                resource.setEngagementRateGroups(ProjectMapper.toEngagementRateGroupsDTO(engagementRateGroups));
            }
        });
        return projectResourceEngagements;
    }

    @Override
    public List<ProjectResourceEngagement> findAllByTaskId(Long taskId) {
        return projectResourceEngagementRepository.findAllByTaskId(taskId);
    }

    @Override
    public List<ProjectAssociatedResourceDTO> findResourceByProjectId(Long projectId) {

        List<ProjectResourceEngagement> projectResourceEngagements = findAllByProjectId(projectId);
        List<ProjectAssociatedResourceDTO> projectAssociatedResourceDTOList = new ArrayList<>();

        for (ProjectResourceEngagement projectResourceEngagement : projectResourceEngagements) {

            if (projectResourceEngagement.getResourceId() != null) {

                HRHead hrHead = hrHeadService.findById(projectResourceEngagement.getResourceId());
                List<RolesAssociatedWithResourceDTO> roles = new ArrayList<>();
                List<StavemRoles> stavemRoles = stavemRolesRepository.findByEmployeeName(hrHead.getName());
                if (!stavemRoles.isEmpty()) {

                    stavemRoles.forEach(stavemRoles1 -> {
                        EngagementRole engagementRole = engagementRoleService.findByExternalRoleId(stavemRoles1.getPhaseId());

                        if (engagementRole != null) {
                            RolesAssociatedWithResourceDTO rolesAssociatedWithResourceDTO = RolesAssociatedWithResourceDTO.builder()
                                    .externalRoleId(engagementRole.getExternalRoleId())
                                    .engagementRoleId(engagementRole.getId())
                                    .roleName(stavemRoles1.getPhaseName())
                                    .build();
                            roles.add(rolesAssociatedWithResourceDTO);
                        }
                    });
                }
                if (hrHead != null) {
                    ProjectAssociatedResourceDTO projectAssociatedResourceDTOExists = projectAssociatedResourceDTOList.isEmpty() ? null : projectAssociatedResourceDTOList.stream().filter(projectAssociatedResourceDTO ->
                            hrHead.getId() == projectAssociatedResourceDTO.getResourceId()).findFirst().orElse(null);
                    if (projectAssociatedResourceDTOExists == null) {
                        Double hours = resourceAttendanceLogRepository.hoursByEmployeeIdAndProjectId(hrHead.getId(), projectResourceEngagement.getProjectId());
                        ProjectAssociatedResourceDTO projectAssociatedResourceDTO = ProjectAssociatedResourceDTO.builder()
                                .projectId(projectResourceEngagement.getProjectId())
                                .projectEngagementId(projectResourceEngagement.getId())
                                .resourceId(hrHead.getId())
                                .resourceName(hrHead.getName())
                                .resourceType(hrHead.getType())
                                .encodedId(hrHead.getEncodedId())
                                .roles(roles)
                                .clockIn(null)
                                .clockOut(null)
                                .hours(hours)
                                .build();
                        projectAssociatedResourceDTOList.add(projectAssociatedResourceDTO);
                    }
                }
            }
        }
        return projectAssociatedResourceDTOList;
    }

    @Override
    public ProjectAssociatedResourceDTO findOneResourceByProjectId(Long projectEngagementId) {
        ProjectResourceEngagement projectResourceEngagement = findById(projectEngagementId);
        HRHead hrHead = hrHeadService.findById(projectResourceEngagement.getResourceId());
        List<RolesAssociatedWithResourceDTO> roles = new ArrayList<>();
        List<StavemRoles> stavemRoles = stavemRolesRepository.findByEmployeeName(hrHead.getName());
        if (!stavemRoles.isEmpty()) {

            stavemRoles.forEach(stavemRoles1 -> {
                EngagementRole engagementRole = engagementRoleService.findByExternalRoleId(stavemRoles1.getPhaseId());

                if (engagementRole != null) {
                    RolesAssociatedWithResourceDTO rolesAssociatedWithResourceDTO = RolesAssociatedWithResourceDTO.builder()
                            .externalRoleId(engagementRole.getExternalRoleId())
                            .engagementRoleId(engagementRole.getId())
                            .roleName(stavemRoles1.getPhaseName())
                            .build();
                    roles.add(rolesAssociatedWithResourceDTO);
                }
            });
            //TODO: Add new roles for any employee
        } else {
            EngagementRole engagementRole = engagementRoleService.findByExternalRoleId("0001");
            RolesAssociatedWithResourceDTO rolesAssociatedWithResourceDTO = RolesAssociatedWithResourceDTO.builder()
                    .externalRoleId(engagementRole.getExternalRoleId())
                    .engagementRoleId(engagementRole.getId())
                    .roleName(engagementRole.getRoleName())
                    .build();
            roles.add(rolesAssociatedWithResourceDTO);
        }
        return ProjectAssociatedResourceDTO.builder()
                .projectId(projectResourceEngagement.getProjectId())
                .projectEngagementId(projectResourceEngagement.getId())
                .engagementRoleId(projectResourceEngagement.getEngagementRoleId())
                .resourceId(projectResourceEngagement.getResourceId())
                .resourceName(hrHead.getName())
                .resourceType(hrHead.getType())
                .roles(roles)
                .build();
    }

    @Override
    public List<ProjectResourceEngagement> findByProjectIdAndResourceId(Long projectId, Long resourceId) {
        return projectResourceEngagementRepository.findByProjectIdAndResourceId(projectId, resourceId);
    }

    @Override
    public ProjectResourceEngagement findByProjectIdAndResourceIdAndTaskId(Long projectId, Long resourceId, Long taskId) {
        return projectResourceEngagementRepository.findByProjectIdAndResourceIdAndTaskId(projectId, resourceId, taskId);
    }

    @Override
    public ProjectResourceEngagement findByProjectIdAndResourceIdAndEngagementRoleId(Long projectId, Long resourceId, Long engagementRoleId) {
        return projectResourceEngagementRepository.findByProjectIdAndResourceIdAndEngagementRoleId(projectId, resourceId, engagementRoleId);
    }

    @Override
    public ProjectResourceEngagement findByProjectIdAndResourceIdAndEngagementRoleIdAndTaskId(Long projectId, Long resourceId, Long engagementRoleId, Long taskId) {
        return projectResourceEngagementRepository.findByProjectIdAndResourceIdAndEngagementRoleIdAndTaskId(projectId, resourceId, engagementRoleId, taskId);
    }

    @Override
    public List<ProjectAssociatedResourceDTO> findResourceByTaskId(Long taskId) {
        List<ProjectResourceEngagement> projectResourceEngagements = findAllByTaskId(taskId);
        List<ProjectAssociatedResourceDTO> projectAssociatedResourceDTOList = new ArrayList<>();

        for (ProjectResourceEngagement projectResourceEngagement : projectResourceEngagements) {

            if (projectResourceEngagement.getTaskId() != null && projectResourceEngagement.getResourceId() != null && projectResourceEngagement.getEngagementRoleId() != null) {

                HRHead hrHead = hrHeadService.findById(projectResourceEngagement.getResourceId());
                EngagementRole engagementRole = null;
                engagementRole = engagementRoleService.findByExternalRoleId(String.valueOf(projectResourceEngagement.getEngagementRoleId()));
                if (engagementRole == null) {
                    engagementRole = engagementRoleService.findById(projectResourceEngagement.getEngagementRoleId());
                }
                Optional<TaskHead> taskHead = taskHeadRepository.findById(projectResourceEngagement.getTaskId());

                if (hrHead != null && taskHead.isPresent() && engagementRole != null) {

                    Double hours = resourceAttendanceLogRepository.hoursByRole(hrHead.getId(), Long.valueOf(engagementRole.getExternalRoleId()), taskId);
                    List<CheckInCheckOutDTO> checkInCheckOutDTOs = getAttendances(hrHead.getId(), taskHead.get().getId());
                    ProjectAssociatedResourceDTO projectAssociatedResourceDTO = ProjectAssociatedResourceDTO.builder()
                            .projectId(projectResourceEngagement.getProjectId())
                            .projectEngagementId(projectResourceEngagement.getId())
                            .engagementRoleId(engagementRole.getId())
                            .resourceId(hrHead.getId())
                            .taskId(projectResourceEngagement.getTaskId())
                            .resourceName(hrHead.getName())
                            .resourceType(hrHead.getType())
                            .roleName(engagementRole.getRoleName())
                            .checkInCheckOutDTOList(checkInCheckOutDTOs)
                            .clockIn(String.valueOf(1))
                            .clockOut(String.valueOf(1))
                            .hours(hours == null ? 0 : hours)
                            .build();
                    projectAssociatedResourceDTOList.add(projectAssociatedResourceDTO);
                }
            }
        }
        return projectAssociatedResourceDTOList;
    }

    @Override
    public List<ProjectAssociatedResourceDTO> findTaskByResourceId(Long projectId, Long resourceId) {
        List<ProjectResourceEngagement> projectResourceEngagements = findByProjectIdAndResourceId(projectId, resourceId);
        List<ProjectAssociatedResourceDTO> projectAssociatedResourceDTOList = new ArrayList<>();
        for (ProjectResourceEngagement projectResourceEngagement : projectResourceEngagements) {
            if (projectResourceEngagement.getTaskId() != null) {
                HRHead hrHead = hrHeadService.findById(projectResourceEngagement.getResourceId());
                EngagementRole engagementRole = engagementRoleService.findById(projectResourceEngagement.getEngagementRoleId());
                Optional<TaskHead> taskHead = taskHeadRepository.findById(projectResourceEngagement.getTaskId());
                Double hours = resourceAttendanceLogRepository.hoursByRole(hrHead.getId(), Long.valueOf(engagementRole.getExternalRoleId()), taskHead.get().getId());
                List<CheckInCheckOutDTO> checkInCheckOutDTOs = getAttendances(hrHead.getId(), taskHead.get().getId());

                if (hrHead != null && taskHead.isPresent()) {
                    ProjectAssociatedResourceDTO projectAssociatedResourceDTO = ProjectAssociatedResourceDTO.builder()
                            .projectId(projectResourceEngagement.getProjectId())
                            .projectEngagementId(projectResourceEngagement.getId())
                            .engagementRoleId(engagementRole.getId())
                            .resourceId(hrHead.getId())
                            .taskId(projectResourceEngagement.getTaskId())
                            .taskDetails(taskHead.get().getDescription() == null ? "none" : taskHead.get().getDescription())
                            .taskStatus(taskHead.get().getStatus() == null ? "none" : taskHead.get().getStatus())
                            .taskSummary(taskHead.get().getSummary() == null ? "none" : taskHead.get().getSummary())
                            .resourceName(hrHead.getName())
                            .resourceType(hrHead.getType())
                            .roleName(engagementRole.getRoleName())
                            .checkInCheckOutDTOList(checkInCheckOutDTOs)
                            .clockIn(String.valueOf(1))
                            .clockOut(String.valueOf(1))
                            .hours(hours == null ? 0 : hours)
                            .build();
                    projectAssociatedResourceDTOList.add(projectAssociatedResourceDTO);
                }
            }
        }
        return projectAssociatedResourceDTOList;
    }

    private List<CheckInCheckOutDTO> getAttendances(Long employeeId, Long taskId) {
        List<ResourceAttendanceLog> resourceAttendanceLogs = resourceAttendanceLogRepository.findByEmployeeIdAndTaskIdAndWorkDate(employeeId,
                taskId, LocalDateTime.now().toLocalDate().toString());
        List<CheckInCheckOutDTO> checkInCheckOutDTOList = new ArrayList<>();
        for (ResourceAttendanceLog resourceAttendanceLog : resourceAttendanceLogs) {
            CheckInDTO checkInDTO = CheckInDTO.builder()
                    .employeeId(resourceAttendanceLog.getEmployeeId())
                    .projectId(resourceAttendanceLog.getProjectId())
                    .taskId(resourceAttendanceLog.getTaskId())
                    .systemRoleId(resourceAttendanceLog.getSystemRoleId())
                    .resourceAttendanceLogId(resourceAttendanceLog.getId())
                    .time(Timestamp.valueOf(resourceAttendanceLog.getTimeIn()))
                    .build();
            CheckOutDTO checkOutDTO = CheckOutDTO.builder()
                    .employeeId(resourceAttendanceLog.getEmployeeId())
                    .projectId(resourceAttendanceLog.getProjectId())
                    .taskId(resourceAttendanceLog.getTaskId())
                    .systemRoleId(resourceAttendanceLog.getSystemRoleId())
                    .resourceAttendanceLogId(resourceAttendanceLog.getId())
                    .time(Timestamp.valueOf(resourceAttendanceLog.getTimeOut()))
                    .build();
            checkInCheckOutDTOList.add(CheckInCheckOutDTO.builder()
                    .checkIns(checkInDTO)
                    .checkOuts(checkOutDTO)
                    .build());
        }
        return checkInCheckOutDTOList;
    }

    @Override
    public ProjectResourceEngagement update(ProjectResourceEngagement projectResourceEngagement) {

        if (projectResourceEngagement.getTaskId() != null) {
            ProjectResourceEngagement finalProjectResourceEngagement = projectResourceEngagement;
            taskHeadRepository.findById(projectResourceEngagement.getTaskId()).orElseThrow(() -> new NotFoundException(TaskHead.class, finalProjectResourceEngagement.getTaskId()));
            engagementRoleService.findById(projectResourceEngagement.getEngagementRoleId());
            ProjectResourceEngagement projectResourceEngagementDb = findById(projectResourceEngagement.getId());
            projectResourceEngagement = ProjectMapper.toUpdatedProjectResourceEngagement(projectResourceEngagementDb, projectResourceEngagement);
            return save(projectResourceEngagement);

        }
        ProjectResourceEngagement projectResourceEngagementDb = findById(projectResourceEngagement.getId());
        projectResourceEngagement = ProjectMapper.toUpdatedProjectResourceEngagement(projectResourceEngagementDb, projectResourceEngagement);
        return save(projectResourceEngagement);

    }

    @Override
    public ProjectResourceEngagement findById(Long id) {
        return projectResourceEngagementRepository.findById(id).orElseThrow(() -> new NotFoundException(ProjectResourceEngagement.class, id));
    }

    @Override
    public List<ProjectResourceEngagement> findAllProjectByResourceId(Long loginId) {
        HRHead hrHead = hrHeadService.findByLoginUser(loginId);
        if (hrHead != null) {
            List<ProjectResourceEngagement> projectResourceEngagements = projectResourceEngagementRepository.findAllByResourceId(hrHead.getId());
            /*if(projectResourceEngagements == null) {
                throw new NotFoundException("Resource not found : " + hrHead.getId());
            }*/
            projectResourceEngagements.forEach(resourceEngagement -> {
                ProjectHead projectHead = projectService.findById(resourceEngagement.getProjectId());
                projectHead.setTotalHoursUsed(resourceAttendanceLogRepository.hoursByEmployeeIdAndProjectId(resourceEngagement.getResourceId(), resourceEngagement.getProjectId()));
                resourceEngagement.setProjectHeadDTO(ProjectMapper.toProjectHeadDTO(projectHead));
            });
            return projectResourceEngagements;
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteById(Long id) {
        projectResourceEngagementRepository.deleteById(id);
    }

    @Override
    public List<ProjectResourceEngagement> findAllByProjectIdAndDesignation(Long projectId, String designation) {
        return projectResourceEngagementRepository.findAllByProjectIdAndDesignation(projectId,designation);
    }
}
