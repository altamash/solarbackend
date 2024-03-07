package com.solar.api.tenant.service.extended.project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.employee.EmployeeDetailDTO;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.project.*;
import com.solar.api.tenant.mapper.extended.project.activity.ActivitySummaryViewDTO;
import com.solar.api.tenant.mapper.extended.project.activity.task.TaskSummaryViewDTO;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementEntityTile;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementFilterDTO;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementPaginationTile;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementTile;
import com.solar.api.tenant.model.customerSupport.ConversationHead;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.project.*;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import com.solar.api.tenant.model.extended.resources.HRHead;
import com.solar.api.tenant.repository.project.*;
import com.solar.api.tenant.service.customerSupport.ConversationHeadService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.extended.resources.HRHeadService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.userGroup.EntityGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.project.ProjectMapper.toUpdatedProjectDetail;
import static com.solar.api.tenant.mapper.extended.project.ProjectMapper.toUpdatedProjectHead;
import static com.solar.api.tenant.mapper.extended.project.activity.ActivityMapper.toUpdatedActivityHead;
import static com.solar.api.tenant.mapper.extended.project.activity.task.TaskMapper.toUpdatedTaskHead;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectHeadRepository projectHeadRepository;
    @Autowired
    private ProjectDetailRepository projectDetailRepository;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private ActivityHeadRepository activityHeadRepository;
    @Autowired
    private TaskHeadRepository taskHeadRepository;
    @Autowired
    private ProjectDependenciesRepository projectDependenciesRepository;
    @Autowired
    private ProjectResourceEngagementRepository projectResourceEngagementRepository;
    @Autowired
    private HRHeadService hrHeadService;
    @Autowired
    private ConversationHeadService conversationHeadService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private EntityGroupService entityGroupService;
    @Autowired
    private PortalAttributeOverrideService portalAttributeOverrideService;

    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Gson gson = new Gson();

    /**
     * Project Head
     */
    @Override
    public ProjectHead saveProjectHead(ProjectHead projectHead) {
        ProjectHead projectHeadDb = projectHeadRepository.save(projectHead);
        List<ProjectDetail> projectDetailsUpd;
        if (projectHeadDb.getProjectDetails().size() != 0) {
            projectDetailsUpd = new ArrayList<>();
            projectHeadDb.getProjectDetails().forEach(projectDetail -> {
                if (projectDetail.getMeasureCodeId() != null) {
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb =
                            measureDefinitionOverrideService.findById(projectDetail.getMeasureCodeId());
                    projectDetail.setProjectHead(projectHeadDb);
                    projectDetail.setMeasureCodeId(projectDetail.getMeasureCodeId());
                    projectDetail.setMeasure(projectDetail.getMeasure());
                    projectDetail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                    projectDetailsUpd.add(projectDetail);
                }
            });
            if (projectDetailsUpd.size() != 0) {
                projectDetailRepository.saveAll(projectDetailsUpd);
            }
        }
        ConversationHead conversationHead = ConversationHead.builder()
                .category("Project")
                .subCategory("Report")
                .priority("Medium")
                .status("Open")
//                .assignee(projectHeadDb.)
                .sourceId(String.valueOf(projectHeadDb.getId()))
                .build();
        try {
            conversationHeadService.add(conversationHead, null, "DOCU");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return projectHeadDb;
    }

    @Override
    public ProjectHead updateProjectHead(ProjectHead projectHead) {
        ProjectHead projectHeadDb = findById(projectHead.getId());
        ProjectHead projectHeadDb2 = toUpdatedProjectHead(projectHeadDb, projectHead);
        List<ProjectDetail> projectDetails = new ArrayList<>();
        if (projectHead.getProjectDetails().size() != 0) {
            projectHead.getProjectDetails().forEach(detail -> {
                ProjectDetail projectDetailDB = projectDetailRepository.findById(detail.getId()).get();
                projectDetailDB.setProjectHead(projectHeadDb2);
                projectDetailDB = toUpdatedProjectDetail(projectDetailDB, detail);
                projectDetails.add(projectDetailDB);
            });
            projectHeadDb2.setProjectDetails(new HashSet(projectDetails));
        }

        ProjectHead projectHeadFinal = projectHeadRepository.save(projectHeadDb2);
        List<ActivityHead> activityHeads = activityHeadRepository.findByProjectHeadId(projectHead.getId());
        List<Long> activityIds = activityHeads.stream().map(ActivityHead::getId).collect(Collectors.toList());
        List<TaskHead> taskHeadsDb = taskHeadRepository.findByActivityHeadIdIn(activityIds);

        if (projectHead.getIsActivityLevel()) {
            List<ActivityHead> activityHeadsUpdate = new ArrayList<>();
            for (ActivityHead activityHeadDb : activityHeads) {
                ActivityHead activityHead = ActivityHead.builder()
                        .estStartDate(projectHeadDb.getEstStartDate())
                        .estEndDate(projectHeadDb.getEstEndDate())
                        .build();
                activityHeadDb = toUpdatedActivityHead(activityHeadDb, activityHead);
                activityHeadsUpdate.add(activityHeadDb);
            }

            if (activityHeadsUpdate != null) {
                activityHeadRepository.saveAll(activityHeadsUpdate);
            }
        }

        if (projectHead.getIsTaskLevel()) {
            if (taskHeadsDb != null) {
                List<TaskHead> taskHeadsUpdate = new ArrayList<>();
                for (TaskHead taskHeadDb : taskHeadsDb) {
                    TaskHead taskHead = TaskHead.builder()
                            .estStartDate(projectHeadDb.getEstStartDate())
                            .estEndDate(projectHeadDb.getEstEndDate())
                            .build();
                    taskHeadDb = toUpdatedTaskHead(taskHeadDb, taskHead);
                    taskHeadsUpdate.add(taskHeadDb);
                }

                if (taskHeadsUpdate != null) {
                    taskHeadRepository.saveAll(taskHeadsUpdate);
                }
            }
        }
        return projectHeadFinal;
    }

    @Override
    public ProjectHead findById(Long id) {
        ProjectHead projectHead = projectHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(ProjectHead.class, id));
        if (projectHead.getProjectDetails().size() != 0) {
            projectHead.getProjectDetails().forEach(detail -> {
                MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureCodeId());
                detail.setMeasureCodeId(detail.getMeasureCodeId());
                detail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                detail.setMeasure(measureDefinitionTenantDb.getMeasure());
            });
        }
        return projectHead;
    }

    @Override
    public ProjectHead findByExternalReferenceId(String value) {
        return projectHeadRepository.findByExternalReferenceId(value);
    }

    @Override
    public List<ProjectHead> findAllProjectHead() {
        return projectHeadRepository.findAll();
    }

    @Override
    public List<ProjectHead> findAllProjectHeadWithActivityAndTask(List<Long> projectIds) {
        List<ProjectHead> projectHeads = null;

        if (projectIds != null) {
            projectHeads = projectHeadRepository.findAllByIdIn(projectIds);
        } else {
            projectHeads = projectHeadRepository.findAll();
        }

        projectHeads.forEach(project -> {
            project.setBgColor(AppConstants.ProjectBGColors.projectDefault);
            List<ProjectDependencies> pdForProject = projectDependenciesRepository.getAllProjectByProjectIdAndRelatedId(project.getId());
            project.setIsDependent(pdForProject.size() != 0 ? true : false);
            if ("Completed".equals(project.getStatus())) {
                project.setBgColor(AppConstants.ProjectBGColors.onCompletion);
            }

            List<ActivityHead> activityHeads = activityHeadRepository.findByProjectHeadId(project.getId());
            if (!activityHeads.isEmpty()) {
                activityHeads.forEach(activity -> {
                    List<ProjectDependencies> pdForActivity = projectDependenciesRepository.getAllActivityByActivityIdAndRelatedId(activity.getId());
                    activity.setIsDependent(pdForActivity.size() != 0 ? true : false);

                    List<TaskHead> taskHeads = taskHeadRepository.findByActivityHeadId(activity.getId());
                    activity.setBgColor(AppConstants.ProjectBGColors.activityDefault);
                    if ("Completed".equals(activity.getStatus())) {
                        activity.setBgColor(AppConstants.ProjectBGColors.onCompletion);
                    }
                    if (!taskHeads.isEmpty()) {
                        taskHeads.forEach(task -> {
                            List<ProjectDependencies> pdForTask = projectDependenciesRepository.getAllTaskByTaskIdAndRelatedId(task.getId());
                            task.setIsDependent(pdForTask.size() != 0 ? true : false);
                            task.setBgColor(AppConstants.ProjectBGColors.taskDefault);
                            if ("Completed".equals(task.getStatus())) {
                                task.setBgColor(AppConstants.ProjectBGColors.onCompletion);
                            }
                        });
                    }
                    activity.setTaskHeads(taskHeads.size() != 0 ? new HashSet(taskHeads) : Collections.emptySet());
                    activity.setActivityDetails(null);
                });
                project.setProjectDetails(null);
                project.setActivityHeads(activityHeads.size() != 0 ? new HashSet(activityHeads) : Collections.emptySet());
            }
        });
        return projectHeads;
    }

    @Override
    public List<ProjectHead> findAllByProjectHeadId(Long id) {
        List<ProjectHead> projectHeads = projectHeadRepository.findAllById(id);
        projectHeads.forEach(projectHead -> {
            if (projectHead.getProjectDetails().size() != 0) {
                projectHead.getProjectDetails().forEach(detail -> {
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureCodeId());
                    detail.setMeasureCodeId(detail.getMeasureCodeId());
                    detail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                    detail.setMeasure(measureDefinitionTenantDb.getMeasure());
                });
            }
        });
        return projectHeads;
    }

    @Override
    public List<ProjectHead> findAllByRegisterId(Long registerId) {
        List<ProjectHead> projectHeads = projectHeadRepository.findAllByRegisterId(registerId);
        projectHeads.forEach(projectHead -> {
            if (projectHead.getProjectDetails().size() != 0) {
                projectHead.getProjectDetails().forEach(detail -> {
                    MeasureDefinitionTenantDTO measureDefinitionTenantDb = measureDefinitionOverrideService.findById(detail.getMeasureCodeId());
                    detail.setMeasureCodeId(detail.getMeasureCodeId());
                    detail.setMeasureDefinitionTenant(measureDefinitionTenantDb);
                    detail.setMeasure(measureDefinitionTenantDb.getMeasure());
                });
            }
        });
        return projectHeads;
    }

    @Override
    public ProjectHead findByIdFetchAll(Long projectId) {
        return projectHeadRepository.findByIdFetchAll(projectId);
    }

    @Override
    public void deleteProjectHead(Long id) {
        projectHeadRepository.deleteById(id);
    }

    @Override
    public void deleteAllProjectHead() {
        projectHeadRepository.deleteAll();
    }


    /**
     * Project Detail
     */

    @Override
    public ProjectDetail saveProjectDetail(ProjectDetail projectDetail) {
        return projectDetailRepository.save(projectDetail);
    }

    /*@Override
    public ProjectDetail updateProjectDetail(ProjectDetail projectDetail) {
        return projectDetailRepository.save(projectDetail);
    }*/

    @Override
    public ProjectDetail findProjectDetailById(Long id) {
        return projectDetailRepository.findById(id).orElseThrow(() -> new NotFoundException(ProjectDetail.class, id));
    }

    @Override
    public List<ProjectDetail> updateProjectDetails(List<ProjectDetail> projectDetails, ProjectHead projectHead) {
        List<ProjectDetail> projectDetailUpdated = new ArrayList<>();
        ProjectHead projectHeadDb = findById(projectHead.getId());
        if (projectDetails.size() != 0) {
            projectDetails.forEach(detail -> {
                ProjectDetail projectDetailDb = findProjectDetailById(detail.getId());
                projectDetailDb.setProjectHead(projectHeadDb);
                projectDetailDb = toUpdatedProjectDetail(projectDetailDb, detail);
                projectDetailUpdated.add(projectDetailDb);
            });
        }
        return projectDetailRepository.saveAll(projectDetailUpdated);
    }


    @Override
    public List<ProjectDetail> findAllProjectDetail() {
        return projectDetailRepository.findAll();
    }

    @Override
    public void deleteProjectDetail(Long id) {
        projectDetailRepository.deleteById(id);
    }

    @Override
    public void deleteAllProjectDetail() {
        projectDetailRepository.deleteAll();
    }

    @Override
    public List<ProjectDependencies> saveProjectDependencies(List<ProjectDependencies> projectDependencies) {
        List<ProjectDependencies> pdSaving = new ArrayList<>();

        if (projectDependencies.size() != 0) {
            List<Long> projectIdsSelected = projectDependencies.stream()
                    .map(ProjectDependencies::getParentId).collect(Collectors.toList());

            if (projectIdsSelected.size() != 0) {
                List<ProjectHead> projectHeads = findAllProjectHeadWithActivityAndTask(projectIdsSelected.stream().distinct().collect(Collectors.toList()));

                for (ProjectHead ph : projectHeads) {
                    List<ProjectDependencies> pdAll = projectDependencies.stream().filter(parent -> parent.getParentId().equals(ph.getId())).collect(Collectors.toList());
                    pdAll.sort(Comparator.comparing(ProjectDependencies::getSequenceId));

                    for (ProjectDependencies pd : pdAll) {
                        if (pd.getTaskId() != null) {
                            // fetch its activity
                            if (pdSaving.size() == 0) {
                                pdSaving.add(pd);
                            } else {
                                ph.getActivityHeads()
                                        .forEach(a -> {
                                            if (a.getTaskHeads().stream().filter(th -> th.getId().equals(pd.getTaskId())).findFirst().isPresent() && pdSaving.size() != 0) {
                                                Optional<ProjectDependencies> pdAlreadyAdded = pdSaving.stream().filter(act -> act.getActivityId() != null && act.getActivityId().equals(a.getId())).findFirst();
                                                if (!pdAlreadyAdded.isPresent()) {
                                                    ProjectDependencies projectDependenciesFound = projectDependenciesRepository.findByTaskIdAndRelatedAtAndRelatedId(pd.getTaskId(), pd.getRelatedAt(), pd.getRelatedId());
                                                    if (projectDependenciesFound != null) {
                                                        throw new AlreadyExistsException(ProjectDependencies.class, "Task Id: ", pd.getTaskId().toString() + " Related Id: " + pd.getRelatedId());
                                                    }
                                                    pdSaving.add(pd);
                                                    //break loop;
                                                }
                                            }
                                        });
                            }
                        } else if (pd.getActivityId() != null) {
                            List<Long> taskIds = ph.getActivityHeads().stream().filter(ac -> ac.getId().equals(pd.getActivityId()))
                                    .flatMap(ac -> ac.getTaskHeads().stream()).map(TaskHead::getId).collect(Collectors.toList());
                            if (pdSaving.size() != 0) {
                                Optional<ProjectDependencies> pdAlreadyAdded = pdSaving.stream().filter(act -> act.getTaskId() != null && taskIds.contains(act.getTaskId())).findFirst();
                                if (!pdAlreadyAdded.isPresent()) {
                                    ProjectDependencies projectDependenciesFound = projectDependenciesRepository.findByActivityIdAndRelatedAtAndRelatedId(pd.getActivityId(), pd.getRelatedAt(), pd.getRelatedId());
                                    if (projectDependenciesFound != null) {
                                        throw new AlreadyExistsException(ProjectDependencies.class, "Activity Id: ", pd.getActivityId().toString() + " Related Id: " + pd.getRelatedId());
                                    }
                                    pdSaving.add(pd);
                                }
                            } else {
                                pdSaving.add(pd);
                            }
                        } else if (pd.getPreDepType().equalsIgnoreCase(Constants.PROJECT_DEPENDENCIES_TYPE.RELATED) && pd.getProjectId() == null) {
                            throw new NullPointerException("Project Id shouldn't be null");

                        } else if (pd.getPreDepType().equalsIgnoreCase(Constants.PROJECT_DEPENDENCIES_TYPE.RELATED) && pd.getProjectId() != null) {
                            pdSaving.add(pd);

                        } else if (pd.getProjectId() != null) {
                            if (pdSaving.size() == 0) {
                                pdSaving.add(pd);
                            } else {
                                List<Long> activityIds = ph.getActivityHeads().stream().map(ActivityHead::getId).collect(Collectors.toList());
                                Optional<ProjectDependencies> pdAlreadyAdded = pdSaving.stream().filter(act -> act.getActivityId() != null && activityIds.contains(act.getActivityId())).findFirst();

                                if (!pdAlreadyAdded.isPresent()) {
                                    List<Long> taskIds = ph.getActivityHeads().stream()
                                            .flatMap(ac -> ac.getTaskHeads().stream())
                                            .map(TaskHead::getId).collect(Collectors.toList());

                                    Optional<ProjectDependencies> pdTaskExists = pdSaving.stream().filter(act -> act.getTaskId() != null && taskIds.contains(act.getTaskId())).findFirst();
                                    if (!pdTaskExists.isPresent()) {
                                        ProjectDependencies projectDependenciesFound = projectDependenciesRepository.findByProjectIdAndRelatedAtAndRelatedId(pd.getProjectId(), pd.getRelatedAt(), pd.getRelatedId());
                                        if (projectDependenciesFound != null) {
                                            throw new AlreadyExistsException(ProjectDependencies.class, "Project Id: ", pd.getProjectId().toString() + " Related Id: " + pd.getRelatedId());
                                        }
                                        pdSaving.add(pd);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return projectDependenciesRepository.saveAll(pdSaving);
    }

    @Override
    public List<ProjectDependencies> updateProjectDependencies(List<ProjectDependencies> projectDependenciesList) {

        List<ProjectDependencies> updates = new ArrayList<>();
        projectDependenciesList.forEach(pd -> {

            if (pd.getActivityId() != null) {
                ProjectDependencies projectDependenciesFound = projectDependenciesRepository.findByActivityIdAndRelatedAtAndRelatedId(pd.getActivityId(), pd.getRelatedAt(), pd.getRelatedId());
                if (projectDependenciesFound != null && !projectDependenciesFound.getId().equals(pd.getId())) {
                    throw new AlreadyExistsException(ProjectDependencies.class, "Activity Id: ", pd.getActivityId().toString() + " Related Id: " + pd.getRelatedId());
                }
            } else if (pd.getTaskId() != null) {
                ProjectDependencies projectDependenciesFound = projectDependenciesRepository.findByTaskIdAndRelatedAtAndRelatedId(pd.getTaskId(), pd.getRelatedAt(), pd.getRelatedId());
                if (projectDependenciesFound != null && !projectDependenciesFound.getId().equals(pd.getId())) {
                    throw new AlreadyExistsException(ProjectDependencies.class, "Task Id: ", pd.getTaskId().toString() + " Related Id: " + pd.getRelatedId());
                }
            } else if (pd.getProjectId() != null) {
                ProjectDependencies projectDependenciesFound = projectDependenciesRepository.findByProjectIdAndRelatedAtAndRelatedId(pd.getProjectId(), pd.getRelatedAt(), pd.getRelatedId());
                if (projectDependenciesFound != null && !projectDependenciesFound.getId().equals(pd.getId())) {
                    throw new AlreadyExistsException(ProjectDependencies.class, "Project Id: ", pd.getProjectId().toString() + " Related Id: " + pd.getRelatedId());
                }
            }

            ProjectDependencies pdToSave = ProjectDependencies.builder().id(pd.getId())
                    .projectId(pd.getProjectId())
                    .activityId(pd.getActivityId())
                    .taskId(pd.getTaskId())
                    .relatedAt(pd.getRelatedAt()).relatedId(pd.getRelatedId())
                    .precedence(pd.getPrecedence()).preDepType(pd.getPreDepType()).build();

            updates.add(pdToSave);
        });
        return projectDependenciesRepository.saveAll(updates);
    }

    //TODO: LinkedDependencies
    @Override
    public List<ProjectHead> findAllProjectDependenciesById(Long id, String fieldName) {
        List<ProjectHead> projectHeads = findAllProjectHeadWithActivityAndTask(null);
        Set<Long> matchedProjectIds = new HashSet<>();
        List<Long> activityMatchIds = new ArrayList<>();
        List<Long> taskMatchIds = new ArrayList<>();

        if (fieldName.equalsIgnoreCase(AppConstants.PROJECT_LEVEL)) {
            List<ProjectDependencies> projectDependencies = projectDependenciesRepository.getAllProjectByProjectIdAndRelatedId(id);
            for (ProjectHead pr : projectHeads) {
                projectDependencies.forEach(pd -> {
                    if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_PROJECT.equalsIgnoreCase(pd.getRelatedAt())) {
                        if (pd.getActivityId() != null && pr.getActivityHeads() != null) {
                            //pr.getActivityHeads().removeIf(ac -> !pd.getActivityId().equals(ac.getId()));
                            pr.getActivityHeads().stream().forEach(ac -> {
                                if (ac.getId().equals(pd.getActivityId())) {
                                    ac.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.REVERSE);
                                    ac.setDependentId(pd.getId());
                                    ac.setPreDepType(pd.getPreDepType());
                                    ac.setDependencyType(pd.getDependencyType() != null ? pd.getDependencyType() : "");
                                    matchedProjectIds.add(pr.getId());
                                    activityMatchIds.add(ac.getId());
                                    return;
                                }
                            });
                        } else if (pd.getProjectId() != null) {
                            if (id.equals(pd.getRelatedId()) && pr.getActivityHeads() != null) {
                                //pr.getActivityHeads().removeIf(ac -> !pd.getActivityId().equals(ac.getId()));
                                pr.getActivityHeads().stream().forEach(ac -> {
                                    if (ac.getId().equals(pd.getActivityId())) {
                                        ac.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.REVERSE);
                                        ac.setDependentId(pd.getId());
                                        ac.setPreDepType(pd.getPreDepType());
                                        ac.setDependencyType(pd.getDependencyType() != null ? pd.getDependencyType() : "");
                                        matchedProjectIds.add(pr.getId());
                                        activityMatchIds.add(ac.getId());
                                        return;
                                    }
                                });
                            } else {
                                if (pr.getId().equals(pd.getRelatedId())) {
                                    pr.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.FORWARD);
                                    pr.setDependentId(pd.getId());
                                    pr.setPreDepType(pd.getPreDepType());
                                    pr.setDependencyType(pd.getDependencyType() != null ? pd.getDependencyType() : "");
                                    matchedProjectIds.add(pr.getId());
                                    return;
                                }
                            }
                        }
                    } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_ACTIVITY.equalsIgnoreCase(pd.getRelatedAt())) {
                        if (pr.getActivityHeads() != null) {
                            //pr.getActivityHeads().removeIf(ac -> !pd.getRelatedId().equals(ac.getId()));
                            pr.getActivityHeads().stream().forEach(ac -> {
                                if (ac.getId().equals(pd.getRelatedId())) {
                                    ac.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.FORWARD);
                                    ac.setDependentId(pd.getId());
                                    ac.setPreDepType(pd.getPreDepType());
                                    ac.setDependencyType(pd.getDependencyType() != null ? pd.getDependencyType() : "");
                                    matchedProjectIds.add(pr.getId());
                                    activityMatchIds.add(ac.getId());
                                    return;
                                }
                            });
                        }
                    }
                });
            }
        } else if (fieldName.equalsIgnoreCase(AppConstants.ACTIVITY_LEVEL)) {
            List<ProjectDependencies> activityDependencies = projectDependenciesRepository.getAllActivityByActivityIdAndRelatedId(id);
            for (ProjectHead pr : projectHeads) {
                activityDependencies.forEach(act -> {
                    if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_ACTIVITY.equalsIgnoreCase(act.getRelatedAt())) {
                        if (act.getActivityId() != null) {
                            if (id.equals(act.getRelatedId()) && pr.getActivityHeads() != null) {
                                //pr.getActivityHeads().removeIf(ac -> !act.getActivityId().equals(ac.getId()));
                                pr.getActivityHeads().stream().forEach(ac -> {
                                    if (ac.getId().equals(act.getActivityId())) {
                                        ac.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.REVERSE);
                                        ac.setDependentId(act.getId());
                                        ac.setPreDepType(act.getPreDepType());
                                        ac.setDependencyType(act.getDependencyType() != null ? act.getDependencyType() : "");
                                        matchedProjectIds.add(pr.getId());
                                        activityMatchIds.add(ac.getId());
                                        return;
                                    }
                                });
                            } else {
                                if (pr.getActivityHeads() != null) {
                                    //pr.getActivityHeads().removeIf(ac -> !act.getRelatedId().equals(ac.getId()));
                                    pr.getActivityHeads().stream().forEach(ac -> {
                                        if (ac.getId().equals(act.getRelatedId())) {
                                            ac.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.FORWARD);
                                            ac.setDependentId(act.getId());
                                            ac.setPreDepType(act.getPreDepType());
                                            ac.setDependencyType(act.getDependencyType() != null ? act.getDependencyType() : "");
                                            matchedProjectIds.add(pr.getId());
                                            activityMatchIds.add(ac.getId());
                                            return;
                                        }
                                    });
                                }
                            }
                        } else if (act.getProjectId() != null) {
                            if (pr.getId().equals(act.getProjectId())) {
                                pr.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.REVERSE);
                                pr.setDependentId(act.getId());
                                pr.setPreDepType(act.getPreDepType());
                                pr.setDependencyType(act.getDependencyType() != null ? act.getDependencyType() : "");
                                matchedProjectIds.add(pr.getId());
                            }
                        } else if (act.getTaskId() != null && pr.getActivityHeads() != null) {
                            pr.getActivityHeads().stream().flatMap(ac -> ac.getTaskHeads().stream())
                                    .forEach(th -> {
                                        if (th.getId().equals(act.getTaskId())) {
                                            th.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.REVERSE);
                                            th.setDependentId(act.getId());
                                            th.setPreDepType(act.getPreDepType());
                                            th.setDependencyType(act.getDependencyType() != null ? act.getDependencyType() : "");
                                            matchedProjectIds.add(pr.getId());
                                            activityMatchIds.add(th.getActivityHead().getId());
                                            taskMatchIds.add(th.getId());
                                            //return;
                                        }
                                    });
/*                            if (activityIdMatched.get() != null) {
                                pr.getActivityHeads().removeIf(ac -> !activityIdMatched.get().equals(ac.getId()));
                                pr.getActivityHeads().stream().filter(ac -> ac.getTaskHeads().removeIf(th -> !act.getTaskId().equals(th.getId()))).collect(Collectors.toList());
                            }*/
                        }

                    } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_TASK.equalsIgnoreCase(act.getRelatedAt())) {
                        if (pr.getActivityHeads() != null) {
                            pr.getActivityHeads().stream().flatMap(ac -> ac.getTaskHeads().stream())
                                    .forEach(th -> {
                                        if (th.getId().equals(act.getRelatedId())) {
                                            th.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.FORWARD);
                                            th.setDependentId(act.getId());
                                            th.setPreDepType(act.getPreDepType());
                                            th.setDependencyType(act.getDependencyType() != null ? act.getDependencyType() : "");
                                            matchedProjectIds.add(pr.getId());
                                            activityMatchIds.add(th.getActivityHead().getId());
                                            taskMatchIds.add(th.getId());
                                            //return;
                                        }
                                    });
/*                            if (activityIdMatched.get() != null) {
                                pr.getActivityHeads().removeIf(ac -> !activityIdMatched.get().equals(ac.getId()));
                                pr.getActivityHeads().stream().filter(ac -> ac.getTaskHeads().removeIf(th -> !act.getRelatedId().equals(th.getId()))).collect(Collectors.toList());
                            }*/
                        }

                    } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_PROJECT.equalsIgnoreCase(act.getRelatedAt())) {
                        if (pr.getId().equals(act.getRelatedId())) {
                            pr.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.FORWARD);
                            pr.setDependentId(act.getId());
                            pr.setPreDepType(act.getPreDepType());
                            pr.setDependencyType(act.getDependencyType() != null ? act.getDependencyType() : "");
                            matchedProjectIds.add(pr.getId());
                        }
                    }
                });
            }

        } else if (fieldName.equalsIgnoreCase(AppConstants.TASK_LEVEL)) {
            List<ProjectDependencies> taskDependencies = projectDependenciesRepository.getAllTaskByTaskIdAndRelatedId(id);
            for (ProjectHead pr : projectHeads) {
                taskDependencies.forEach(task -> {
                    if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_ACTIVITY.equalsIgnoreCase(task.getRelatedAt())) {
                        if (pr.getActivityHeads() != null) {
                            //pr.getActivityHeads().removeIf(ac -> !task.getRelatedId().equals(ac.getId()));
                            pr.getActivityHeads().stream().forEach(ac -> {
                                if (ac.getId().equals(task.getRelatedId())) {
                                    ac.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.FORWARD);
                                    ac.setDependentId(task.getId());
                                    ac.setPreDepType(task.getPreDepType());
                                    ac.setDependencyType(task.getDependencyType() != null ? task.getDependencyType() : "");
                                    matchedProjectIds.add(pr.getId());
                                    activityMatchIds.add(ac.getId());
                                    return;
                                }
                            });
                        }
                    } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_TASK.equalsIgnoreCase(task.getRelatedAt())) {
                        if (task.getTaskId() != null) {
                            if (pr.getActivityHeads() != null && task.getRelatedId().equals(id)) {
                                //AtomicReference<Long> activityIdMatched = new AtomicReference<>();
                                pr.getActivityHeads().stream().flatMap(ac -> ac.getTaskHeads().stream())
                                        .forEach(th -> {
                                            if (th.getId().equals(task.getTaskId())) {
                                                th.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.REVERSE);
                                                th.setDependentId(task.getId());
                                                th.setPreDepType(task.getPreDepType());
                                                th.setDependencyType(task.getDependencyType() != null ? task.getDependencyType() : "");
                                                matchedProjectIds.add(pr.getId());
                                                activityMatchIds.add(th.getActivityHead().getId());
                                                taskMatchIds.add(th.getId());
                                                //return;
                                            }
                                        });
/*                                if (activityIdMatched.get() != null) {
                                    pr.getActivityHeads().removeIf(ac -> !activityIdMatched.get().equals(ac.getId()));
                                    pr.getActivityHeads().stream().filter(ac -> ac.getTaskHeads().removeIf(th -> !task.getTaskId().equals(th.getId()))).collect(Collectors.toList());
                                }*/
                            } else {
                                if (pr.getActivityHeads() != null) {
                                    pr.getActivityHeads().stream().flatMap(ac -> ac.getTaskHeads().stream())
                                            .forEach(th -> {
                                                if (th.getId().equals(task.getRelatedId())) {
                                                    th.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.FORWARD);
                                                    th.setDependentId(task.getId());
                                                    th.setPreDepType(task.getPreDepType());
                                                    th.setDependencyType(task.getDependencyType() != null ? task.getDependencyType() : "");
                                                    matchedProjectIds.add(pr.getId());
                                                    activityMatchIds.add(th.getActivityHead().getId());
                                                    taskMatchIds.add(th.getId());
                                                    //return;
                                                }
                                            });
/*                                    if (activityIdMatched.get() != null) {
                                        pr.getActivityHeads().removeIf(ac -> !activityIdMatched.get().equals(ac.getId()));
                                        pr.getActivityHeads().stream().filter(ac -> ac.getTaskHeads().removeIf(th -> !task.getRelatedId().equals(th.getId()))).collect(Collectors.toList());
                                    }*/
                                }
                            }
                        } else {
                            //activity
                            if (pr.getActivityHeads() != null) {
                                //pr.getActivityHeads().removeIf(ac -> !task.getActivityId().equals(ac.getId()));
                                pr.getActivityHeads().stream().forEach(ac -> {
                                    if (ac.getId().equals(task.getActivityId())) {
                                        ac.setDirection(Constants.PROJECT_DEPENDENCIES_DIRECTIONS.REVERSE);
                                        ac.setDependentId(task.getId());
                                        ac.setPreDepType(task.getPreDepType());
                                        ac.setDependencyType(task.getDependencyType() != null ? task.getDependencyType() : "");
                                        matchedProjectIds.add(pr.getId());
                                        activityMatchIds.add(ac.getId());
                                        return;
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
        System.out.println(projectHeads);
        List<ProjectHead> projectHeadFinal = projectHeads != null ? projectHeads.stream().filter(p -> matchedProjectIds.contains(p.getId())).collect(Collectors.toList()) : null;
        if (projectHeadFinal != null && taskMatchIds.size() != 0) {
            removeTasks(projectHeadFinal, taskMatchIds);
        }
        if (projectHeadFinal != null && activityMatchIds.size() != 0) {
            removeActivities(projectHeadFinal, activityMatchIds);
        }
        return projectHeadFinal;
    }

    @Override
    public List<ProjectDependencies> findAllProjectDependenciesByProjectId(Long projectId) {

        ProjectHead projectHead = projectHeadRepository.findById(projectId).get();
        List<ProjectDependencies> projectDependencies = new ArrayList<>();

        if (projectHead != null) {

            List<ProjectDependencies> projectDependenciesForProject = projectDependenciesRepository.findAllByRelatedAtAndRelatedIdIn(EProjectDependency.RELATED_PROJECT.getAction(), Arrays.asList(projectId));
            List<ActivityHead> activityHeads = activityHeadRepository.findByProjectHeadId(projectId);
            List<Long> activityIds = activityHeads != null ? activityHeads.stream().map(ActivityHead::getId).collect(Collectors.toList()) : Collections.emptyList();
            List<TaskHead> taskHeads = taskHeadRepository.findByActivityHeadIdIn(activityIds);

            if (activityHeads != null) {
                projectDependenciesForProject.forEach(pr -> {
                    pr.setRelatedIdName(projectHead.getProjectName());
                    if (pr.getActivityId() != null) {
                        ActivityHead activityName = activityHeads.stream().filter(a -> a.getId().equals(pr.getActivityId())).collect(Collectors.toList()).size() != 0 ?
                                activityHeads.stream().filter(a -> a.getId().equals(pr.getActivityId())).collect(Collectors.toList()).get(0) : null;
                        pr.setActivityName(activityName != null ? activityName.getSummary() : null);
                    } else if (pr.getProjectId() != null) {
                        pr.setProjectName(projectHead.getProjectName());
                    } else if (pr.getTaskId() != null) {
                        TaskHead taskName = null;
                        if (taskHeads != null) {
                            taskName = taskHeads.stream().filter(t -> t.getId().equals(pr.getTaskId())).collect(Collectors.toList()).size() != 0 ?
                                    taskHeads.stream().filter(t -> t.getId().equals(pr.getTaskId())).collect(Collectors.toList()).get(0) : null;
                        }
                        pr.setTaskName(taskName != null ? taskName.getSummary() : null);
                    }
                });
                projectDependencies.addAll(projectDependenciesForProject);

                List<ProjectDependencies> projectDependenciesForActivity = projectDependenciesRepository.findAllByRelatedAtAndRelatedIdIn(EProjectDependency.RELATED_ACTIVITY.getAction(), activityIds);
                projectDependenciesForActivity.forEach(pd -> {
                    ActivityHead relatedIdName = activityHeads.stream().filter(a -> a.getId().equals(pd.getRelatedId())).collect(Collectors.toList()).size() != 0 ?
                            activityHeads.stream().filter(a -> a.getId().equals(pd.getRelatedId())).collect(Collectors.toList()).get(0) : null;
                    pd.setRelatedIdName(relatedIdName != null ? relatedIdName.getSummary() : null);
                    if (pd.getActivityId() != null) {
                        ActivityHead activityName = activityHeads.stream().filter(a -> a.getId().equals(pd.getActivityId())).collect(Collectors.toList()).size() != 0 ?
                                activityHeads.stream().filter(a -> a.getId().equals(pd.getActivityId())).collect(Collectors.toList()).get(0) : null;
                        pd.setActivityName(activityName != null ? activityName.getSummary() : null);
                    } else if (pd.getProjectId() != null) {
                        pd.setProjectName(projectHead.getProjectName());
                    } else if (pd.getTaskId() != null) {
                        TaskHead taskName = null;
                        if (taskHeads != null) {
                            taskName = taskHeads.stream().filter(t -> t.getId().equals(pd.getTaskId())).collect(Collectors.toList()).size() != 0 ?
                                    taskHeads.stream().filter(t -> t.getId().equals(pd.getTaskId())).collect(Collectors.toList()).get(0) : null;
                        }
                        pd.setTaskName(taskName != null ? taskName.getSummary() : null);
                    }
                });
                projectDependencies.addAll(projectDependenciesForActivity);

                List<Long> taskIds = null;
                if (taskHeads != null) {
                    taskIds = taskHeads.stream().map(TaskHead::getId).collect(Collectors.toList());
                    List<ProjectDependencies> projectDependenciesForTask = projectDependenciesRepository.findAllByRelatedAtAndRelatedIdIn(EProjectDependency.RELATED_TASK.getAction(), taskIds);
                    projectDependenciesForTask.forEach(pd -> {
                        TaskHead relatedIdName = taskHeads.stream().filter(t -> t.getId().equals(pd.getRelatedId())).collect(Collectors.toList()).size() != 0 ?
                                taskHeads.stream().filter(t -> t.getId().equals(pd.getRelatedId())).collect(Collectors.toList()).get(0) : null;
                        pd.setRelatedIdName(relatedIdName != null ? relatedIdName.getSummary() : null);
                        if (pd.getActivityId() != null) {
                            ActivityHead activityName = activityHeads.stream().filter(a -> a.getId().equals(pd.getActivityId())).collect(Collectors.toList()).size() != 0
                                    ? activityHeads.stream().filter(a -> a.getId().equals(pd.getActivityId())).collect(Collectors.toList()).get(0) : null;
                            pd.setActivityName(activityName != null ? activityName.getSummary() : null);
                        } else if (pd.getProjectId() != null) {
                            pd.setProjectName(projectHead.getProjectName());
                        } else if (pd.getTaskId() != null) {
                            TaskHead taskName = null;
                            if (taskHeads != null) {
                                taskName = taskHeads.stream().filter(t -> t.getId().equals(pd.getTaskId())).collect(Collectors.toList()).size() != 0 ?
                                        taskHeads.stream().filter(t -> t.getId().equals(pd.getTaskId())).collect(Collectors.toList()).get(0) : null;
                            }
                            pd.setTaskName(taskName != null ? taskName.getSummary() : null);
                        }
                    });
                    projectDependencies.addAll(projectDependenciesForTask);
                }
            }
        }
        return projectDependencies;
    }

    @Override
    public ProjectDependenciesViewListDTO findAllActivitiesAndTasks(Long projectId) {
        List<ActivityHead> activityHeads = activityHeadRepository.findByProjectHeadId(projectId);
        List<TaskHead> taskHeads = null;
        if (activityHeads != null) {
            List<Long> activityIds = activityHeads.stream().map(ActivityHead::getId).collect(Collectors.toList());
            taskHeads = taskHeadRepository.findByActivityHeadIdIn(activityIds);
        }
        return ProjectDependenciesViewListDTO.builder()
                .activityHeadList(activityHeads)
                .taskHeadList(taskHeads)
                .build();
    }

    @Override
    public void deleteByProjectDependenciesId(Long id) {
        projectDependenciesRepository.deleteById(id);
    }

    @Override
    public List<ProjectHeadChartDTO> ganttChart() {
        List<ProjectHeadChartDTO> projectHeadChartDTOS = new ArrayList<>();

        try {
            List<ProjectGanttChartTemplate> projectGanttChartData = projectHeadRepository.projectGanttChart();

            if (!projectGanttChartData.isEmpty()) {
                List<ProjectDependencies> projectDependencies = projectDependenciesRepository.findAll();

                Set<Long> projectIds = new HashSet<>();
                projectGanttChartData.forEach(temp -> {
                    projectIds.add(temp.getProjectId());
                });

                projectIds.forEach(projectId -> {
                    List<ProjectGanttChartTemplate> singleProject = projectGanttChartData.stream().filter(pId -> pId.getProjectId().longValue() == projectId.longValue())
                            .collect(Collectors.toList());

                    List<ProjectDependencies> projectPD = projectDependencies.stream().filter(pd -> pd.getProjectId() != null && pd.getProjectId().equals(singleProject.get(0).getProjectId())).collect(Collectors.toList());
                    List<String> dependencies = new ArrayList<>();
                    String projectDependency = "";

                    if (projectPD.size() != 0) {
                        projectPD.stream().forEach(pd -> {
                            String dependency = "";
                            if (Constants.PROJECT_DEPENDENCIES_TYPE.START_START.equalsIgnoreCase(pd.getPreDepType())) {
                                dependency = dependency.concat(pd.getRelatedId().toString()).concat(Constants.PROJECT_DEPENDENCIES_TYPE.SS);
                            } else if (Constants.PROJECT_DEPENDENCIES_TYPE.FINISH_START.equalsIgnoreCase(pd.getPreDepType())) {
                                dependency = dependency.concat(pd.getRelatedId().toString()).concat(Constants.PROJECT_DEPENDENCIES_TYPE.FS);
                            }
                            dependencies.add(dependency);
                        });
                        projectDependency = String.join(",", dependencies);
                    }

                    //projectHead
                    ProjectHeadChartDTO projectHeadChartDTO = ProjectHeadChartDTO.builder()
                            .taskId(singleProject.get(0).getProjectId())
                            .taskName(singleProject.get(0).getProjectName())
                            //.progress("")
                            .level(AppConstants.PROJECT_LEVEL)
                            .status(singleProject.get(0).getProjectStatus())
                            .duration(Long.parseLong(singleProject.get(0).getProjectActualDuration()))
                            .actualStartDate(singleProject.get(0).getProjectActualStartDate())
                            .actualEndDate(singleProject.get(0).getProjectActualEndDate())
                            .estStartDate(singleProject.get(0).getProjectEstStartDate())
                            .estEndDate(singleProject.get(0).getProjectEstEndDate())
                            .projectManager(singleProject.get(0).getProjectManager())
                            .dependency(projectDependency).build();

                    Set<Long> activityIds = new HashSet<>();
                    singleProject.forEach(temp -> {
                        activityIds.add(temp.getActivityId());
                    });

                    List<SubTasksChartDTO> subActivitiesChartDTOS = new ArrayList<>();
                    activityIds.forEach(activityId -> {
                        List<ProjectGanttChartTemplate> singleActivity = singleProject.stream().filter(acId -> acId.getActivityId().longValue() == activityId.longValue())
                                .collect(Collectors.toList());

                        List<SubTasksChartDTO> subTasksChartDTOS = new ArrayList<>();
                        singleActivity.forEach(subTask -> {

                            List<ProjectDependencies> taskPD = projectDependencies.stream().filter(pd -> pd.getTaskId() != null && pd.getTaskId().equals(subTask.getTaskId())).collect(Collectors.toList());
                            List<String> taskDependencies = new ArrayList<>();
                            String taskDependency = "";

                            if (taskPD.size() != 0) {
                                taskPD.stream().forEach(pd -> {
                                    String dependency = "";
                                    if (Constants.PROJECT_DEPENDENCIES_TYPE.START_START.equalsIgnoreCase(pd.getPreDepType())) {
                                        dependency = dependency.concat(pd.getRelatedId().toString()).concat(Constants.PROJECT_DEPENDENCIES_TYPE.SS);
                                    } else if (Constants.PROJECT_DEPENDENCIES_TYPE.FINISH_START.equalsIgnoreCase(pd.getPreDepType())) {
                                        dependency = dependency.concat(pd.getRelatedId().toString()).concat(Constants.PROJECT_DEPENDENCIES_TYPE.FS);
                                    }
                                    taskDependencies.add(dependency);
                                });
                                taskDependency = String.join(",", taskDependencies);
                            }

                            //subTasks for activity
                            subTasksChartDTOS.add(SubTasksChartDTO.builder()
                                    .taskId(subTask.getTaskId()).taskName(subTask.getTaskSummary() != null ? subTask.getTaskSummary() : "")
                                    .actualStartDate(subTask.getTaskActualStartDate() != null ? subTask.getTaskActualStartDate().substring(0, 10) : "")
                                    .actualEndDate(subTask.getTaskActualEndDate() != null ? subTask.getTaskActualEndDate().substring(0, 10) : "")
                                    .estStartDate(subTask.getTaskEstStartDate() != null ? subTask.getTaskEstStartDate().substring(0, 10) : "")
                                    .estEndDate(subTask.getTaskEstEndDate() != null ? subTask.getTaskEstEndDate().substring(0, 10) : "")
                                    .duration(Long.parseLong(subTask.getTaskActualDuration() != null ? subTask.getTaskActualDuration() : "0"))
                                    .level(AppConstants.TASK_LEVEL)
                                    .progress(Long.parseLong(subTask.getTaskProgress() != null ? subTask.getTaskProgress() : "0"))
                                    .status(subTask.getTaskStatus() != null ? subTask.getTaskStatus() : "")
                                    .dependency(taskDependency).build());
                        });

                        //activity dependency
                        List<ProjectDependencies> activityPD = projectDependencies.stream().filter(pd -> pd.getActivityId() != null && pd.getActivityId().equals(singleActivity.get(0).getActivityId())).collect(Collectors.toList());
                        List<String> activityDependencies = new ArrayList<>();
                        String activityDependency = "";

                        if (activityPD.size() != 0) {
                            activityPD.stream().forEach(pd -> {
                                String dependency = "";
                                if (Constants.PROJECT_DEPENDENCIES_TYPE.START_START.equalsIgnoreCase(pd.getPreDepType())) {
                                    dependency = dependency.concat(pd.getRelatedId().toString()).concat(Constants.PROJECT_DEPENDENCIES_TYPE.SS);
                                } else if (Constants.PROJECT_DEPENDENCIES_TYPE.FINISH_START.equalsIgnoreCase(pd.getPreDepType())) {
                                    dependency = dependency.concat(pd.getRelatedId().toString()).concat(Constants.PROJECT_DEPENDENCIES_TYPE.FS);
                                }
                                activityDependencies.add(dependency);
                            });
                            activityDependency = String.join(",", activityDependencies);
                        }

                        //subActivities
                        subActivitiesChartDTOS.add(SubTasksChartDTO.builder()
                                .taskId(singleActivity.get(0).getActivityId())
                                .taskName(singleActivity.get(0).getActivitySummary() != null ? singleActivity.get(0).getActivitySummary() : "")
                                .actualStartDate(singleActivity.get(0).getActivityActualStartDate() != null ? singleActivity.get(0).getActivityActualStartDate().substring(0, 10) : "")
                                .actualEndDate(singleActivity.get(0).getActivityActualEndDate() != null ? singleActivity.get(0).getActivityActualEndDate().substring(0, 10) : "")
                                .estStartDate(singleActivity.get(0).getActivityEstStartDate() != null ? singleActivity.get(0).getActivityEstStartDate().substring(0, 10) : "")
                                .estEndDate(singleActivity.get(0).getActivityEstEndDate() != null ? singleActivity.get(0).getActivityEstEndDate().substring(0, 10) : "")
                                .duration(Long.parseLong(singleActivity.get(0).getActivityActualDuration() != null ? singleActivity.get(0).getActivityActualDuration() : "0"))
                                .level(AppConstants.ACTIVITY_LEVEL)
                                .status(singleActivity.get(0).getActivityStatus() != null ? singleActivity.get(0).getActivityStatus() : "")
                                .subTasks(subTasksChartDTOS)
                                .dependency(activityDependency).build());
                    });
                    projectHeadChartDTO.setSubTasks(subActivitiesChartDTOS);
                    projectHeadChartDTOS.add(projectHeadChartDTO);
                });
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return projectHeadChartDTOS;
    }

    @Override
    public List<ProjectHead> unLinkedDependencies(List<LinkedUnLinkedDependenciesDTO> dependenciesDTOS) {

        List<ProjectDependencies> projectRelationFound = projectDependenciesRepository.findAllByRelatedAtAndRelatedIdAndPreDepType(dependenciesDTOS.get(0).getRelatedAt(), dependenciesDTOS.get(0).getRelatedId(), Constants.PROJECT_DEPENDENCIES_TYPE.RELATED);
        List<ProjectHead> projectHeads = null;
        //for (LinkedUnLinkedDependenciesDTO dp : dependenciesDTOS) {
        if ((Constants.PROJECT_DEPENDENCIES_TYPE.FINISH_START.equalsIgnoreCase(dependenciesDTOS.get(0).getDepType()) || Constants.PROJECT_DEPENDENCIES_TYPE.START_START.equalsIgnoreCase(dependenciesDTOS.get(0).getDepType())) && (projectRelationFound.size() > 0)) {
            List<Long> projectLinked = projectRelationFound.stream().map(pr -> pr.getProjectId()).collect(Collectors.toList());
            projectLinked.add(dependenciesDTOS.get(0).getProjectId());
            projectHeads = showLinkedDependencies(dependenciesDTOS, projectLinked);

        } else if ((Constants.PROJECT_DEPENDENCIES_TYPE.FINISH_START.equalsIgnoreCase(dependenciesDTOS.get(0).getDepType()) || Constants.PROJECT_DEPENDENCIES_TYPE.START_START.equalsIgnoreCase(dependenciesDTOS.get(0).getDepType())) && (projectRelationFound.size() == 0)) {
            projectHeads = showLinkedDependencies(dependenciesDTOS, Arrays.asList(dependenciesDTOS.get(0).getProjectId()));

            if (dependenciesDTOS.get(0).getFieldName().equalsIgnoreCase(AppConstants.PROJECT_LEVEL) && projectHeads.size() == 0) {
                throw new NotFoundException("Add Project Relation First.");
            }

        } else if (Constants.PROJECT_DEPENDENCIES_TYPE.RELATED.equalsIgnoreCase(dependenciesDTOS.get(0).getDepType()) && projectRelationFound.size() == 0) {
            //return all projects except the current one
            projectHeads = findAllProjectHeadWithActivityAndTask(null);
            projectHeads.removeIf(pr -> pr.getId().equals(dependenciesDTOS.get(0).getProjectId()));//relatedId
            projectHeads.stream().peek(p -> p.setActivityHeads(null)).collect(Collectors.toList());

        } else if (Constants.PROJECT_DEPENDENCIES_TYPE.RELATED.equalsIgnoreCase(dependenciesDTOS.get(0).getDepType()) && projectRelationFound.size() > 0) {
            //return all project except the linked one should be disabled
            projectHeads = findAllProjectHeadWithActivityAndTask(null);
            projectHeads.removeIf(pr -> pr.getId().equals(dependenciesDTOS.get(0).getProjectId()));//relatedId
            projectHeads.stream().forEach(p -> {
                if (projectRelationFound.stream().filter(pr -> pr.getProjectId().equals(p.getId())).findFirst().isPresent()) {
                    p.setIsDisable(true);
                }
                p.setActivityHeads(null);
            });
        }
        //}
        if (AppConstants.PROJECT_LEVEL.equalsIgnoreCase(dependenciesDTOS.get(0).getFieldName())) {
            projectHeads = hideTasksOnProjectLevel(projectHeads);
        }
        return projectHeads;
    }

    private List<ProjectHead> hideTasksOnProjectLevel(List<ProjectHead> projectHeads) {
        projectHeads.stream().filter(p -> p.getActivityHeads() != null)
                .flatMap(p -> p.getActivityHeads().stream()).filter(a -> a.getTaskHeads() != null)
                .flatMap(a -> a.getTaskHeads().stream()).peek(th -> th.setIsDisable(true)).collect(Collectors.toList());
        return projectHeads;
    }

    private List<ProjectHead> removeTasks(List<ProjectHead> projectHeads, List<Long> taskMatchIds) {
        projectHeads.stream().flatMap(p -> p.getActivityHeads().stream())
                .filter(ac -> ac.getTaskHeads().removeIf(th -> !taskMatchIds.contains(th.getId()))).collect(Collectors.toList());
        return projectHeads;
    }

    private List<ProjectHead> removeActivities(List<ProjectHead> projectHeads, List<Long> activityMatchIds) {
        projectHeads.stream().filter(p -> p.getActivityHeads()
                .removeIf(ac -> !activityMatchIds.contains(ac.getId()))).collect(Collectors.toList());
        return projectHeads;
    }

    private List<ProjectHead> showLinkedDependencies(List<LinkedUnLinkedDependenciesDTO> dependenciesDTOS, List<Long> projectLinkedIds) {
        if (dependenciesDTOS.get(0).getFieldName().equalsIgnoreCase(AppConstants.PROJECT_LEVEL)) {
            // all work copied and return linked project disabled
            List<ProjectHead> projectHeads = findAllProjectHeadWithActivityAndTask(projectLinkedIds).stream().peek(p -> p.setIsDisable(true)).collect(Collectors.toList());
            projectHeads.removeIf(pr -> pr.getId().equals(dependenciesDTOS.get(0).getProjectId()));

            List<ProjectDependencies> projectDependencies = projectDependenciesRepository.getAllProjectByProjectIdAndRelatedId(dependenciesDTOS.get(0).getId());
            projectDependencies.forEach(pd -> {
                if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_PROJECT.equalsIgnoreCase(pd.getRelatedAt())) {
                    if (pd.getActivityId() != null) {
                        projectHeads.stream().filter(p -> p.getActivityHeads() != null)
                                .flatMap(ac -> ac.getActivityHeads().stream()).filter(ac -> ac.getId().equals(pd.getActivityId())).peek(a -> a.setIsDisable(true)).collect(Collectors.toList());

                    } else if (pd.getProjectId() != null) {

                        if (dependenciesDTOS.get(0).getId().equals(pd.getRelatedId())) {
                            projectHeads.stream().filter(p -> p.getActivityHeads() != null)
                                    .flatMap(ac -> ac.getActivityHeads().stream()).filter(ac -> ac.getId().equals(pd.getActivityId())).peek(a -> a.setIsDisable(true)).collect(Collectors.toList());
                        } /*else {
                            Optional<ProjectHead> ph = projectHeads.stream().filter(p -> p.getId().equals(pd.getProjectId())).findFirst();
                            if (ph.isPresent()) {
                                projectHeads.remove(ph.get());
                            }
                        }*/
                    }

                } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_ACTIVITY.equalsIgnoreCase(pd.getRelatedAt())) {
                    projectHeads.stream().filter(p -> p.getActivityHeads() != null)
                            .flatMap(ac -> ac.getActivityHeads().stream()).filter(ac -> ac.getId().equals(pd.getRelatedId())).peek(a -> a.setIsDisable(true)).collect(Collectors.toList());
                }
            });
            return projectHeads;

        } else if (dependenciesDTOS.get(0).getFieldName().equalsIgnoreCase(AppConstants.ACTIVITY_LEVEL)) {
            List<ProjectHead> projectHeads = findAllProjectHeadWithActivityAndTask(projectLinkedIds);

            projectHeads.stream().filter(pp -> pp.getId().equals(dependenciesDTOS.get(0).getProjectId()) && pp.getActivityHeads() != null)
                    .peek(p -> p.setIsDisable(true))
                    .filter(p -> p.getActivityHeads().removeIf(ac -> ac.getId().equals(dependenciesDTOS.get(0).getId()))).collect(Collectors.toList());

            List<ProjectDependencies> activityDependencies = projectDependenciesRepository.getAllActivityByActivityIdAndRelatedId(dependenciesDTOS.get(0).getId());
            // cant link itself so removing that project
            ActivityHead activityHead = activityHeadRepository.findById(dependenciesDTOS.get(0).getId()).get();

            activityDependencies.forEach(act -> {
                if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_ACTIVITY.equalsIgnoreCase(act.getRelatedAt())) {
                    if (act.getActivityId() != null) {
                        if (dependenciesDTOS.get(0).getId().equals(act.getRelatedId())) {
                            projectHeads.removeIf(pr -> pr.getActivityHeads() != null && pr.getActivityHeads().contains(activityHead));
                        } else {
                            projectHeads.stream().filter(p -> p.getActivityHeads() != null && p.getActivityHeads().removeIf(ac -> ac.getId().equals(act.getRelatedId()))).collect(Collectors.toList());
                        }

                    } else if (act.getProjectId() != null) {
                        Optional<ProjectHead> ph = projectHeads.stream().filter(p -> p.getId().equals(act.getProjectId())).findFirst();
                        if (ph.isPresent()) {
                            projectHeads.remove(ph);
                        }
                    } else if (act.getTaskId() != null) {
                        projectHeads.stream().filter(pp -> pp.getActivityHeads() != null)
                                .flatMap(p -> p.getActivityHeads().stream())
                                .forEach(a -> {
                                    if (a.getTaskHeads().stream().filter(th -> th.getId().equals(act.getTaskId())).peek(th -> th.setIsDisable(true)).findFirst().isPresent())
                                        a.setIsDisable(true);
                                });
                    }

                } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_TASK.equalsIgnoreCase(act.getRelatedAt())) {
                    projectHeads.stream().filter(pp -> pp.getActivityHeads() != null)
                            .flatMap(p -> p.getActivityHeads().stream())
                            //.flatMap(a -> a.getTaskHeads().stream())
                            .forEach(a -> {
                                if (a.getTaskHeads().stream().filter(th -> th.getId().equals(act.getRelatedId())).peek(th -> th.setIsDisable(true)).findFirst().isPresent())
                                    a.setIsDisable(true);
                            });

                } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_PROJECT.equalsIgnoreCase(act.getRelatedAt())) {
                    projectHeads.removeIf(pr -> pr.getId().equals(act.getRelatedId()));
                }
            });
            return projectHeads;

        } else if (dependenciesDTOS.get(0).getFieldName().equalsIgnoreCase(AppConstants.TASK_LEVEL)) {
            List<ProjectHead> projectHeads = findAllProjectHeadWithActivityAndTask(projectLinkedIds);

            List<ProjectDependencies> taskDependencies = projectDependenciesRepository.getAllTaskByTaskIdAndRelatedId(dependenciesDTOS.get(0).getId());
            AtomicReference<Long> activityIdOfTask = new AtomicReference<>();
            projectHeads.stream().filter(pp -> pp.getId().equals(dependenciesDTOS.get(0).getProjectId()) && pp.getActivityHeads() != null)
                    .peek(p -> p.setIsDisable(true))
                    .flatMap(p -> p.getActivityHeads().stream())
                    //.flatMap(a -> a.getTaskHeads().stream())
                    .forEach(a -> {
                        if (a.getTaskHeads().stream().filter(th -> th.getId().equals(dependenciesDTOS.get(0).getId())).peek(th -> th.setIsDisable(true)).findFirst().isPresent()) {
                            a.setIsDisable(true);
                            // to check if its activity is dependent on any activity or task to avoid cycling process
                            activityIdOfTask.set(a.getId());
                        }
                    });

            if (activityIdOfTask.get() != null) {
                List<ProjectDependencies> activityDependencies = projectDependenciesRepository.getAllActivityByActivityIdAndRelatedId(activityIdOfTask.get());

                if (!activityDependencies.isEmpty()) {
                    List<Long> activitiesId = activityDependencies.stream().filter(a -> a.getActivityId() != null).map(ProjectDependencies::getActivityId).collect(Collectors.toList());
                    projectHeads.stream().filter(pp -> pp.getId().equals(dependenciesDTOS.get(0).getProjectId()) && pp.getActivityHeads() != null)
                            .flatMap(p -> p.getActivityHeads().stream())
                            .forEach(a -> {
                                if (activitiesId.contains(a.getId()))
                                    a.setIsDisable(true);
                                if (a.getTaskHeads() != null)
                                    a.getTaskHeads().stream().peek(th -> th.setIsDisable(true)).collect(Collectors.toList());
                            });
                }
            }

            taskDependencies.forEach(task -> {
                if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_ACTIVITY.equalsIgnoreCase(task.getRelatedAt())) {
                    projectHeads.stream().filter(p -> p.getActivityHeads() != null).filter(ac -> ac.getActivityHeads().removeIf(a -> a.getId().equals(task.getRelatedId()))).collect(Collectors.toList());

                } else if (Constants.PROJECT_DEPENDENCIES_RELATED_AT.RELATED_TASK.equalsIgnoreCase(task.getRelatedAt())) {
                    if (task.getTaskId() != null) {
                        if (task.getRelatedId().equals(dependenciesDTOS.get(0).getId())) {
                            projectHeads.stream().filter(pp -> pp.getActivityHeads() != null)
                                    .flatMap(p -> p.getActivityHeads().stream()).filter(ac -> ac.getTaskHeads().removeIf(th -> th.getId().equals(task.getTaskId()))).collect(Collectors.toList());
                            //pr.getActivityHeads().stream().filter(ac-> ac.getTaskHeads().removeIf(th-> th.getId().equals(task.getTaskId())));

                        } else {
                            projectHeads.stream().filter(pp -> pp.getActivityHeads() != null)
                                    .flatMap(p -> p.getActivityHeads().stream()).filter(ac -> ac.getTaskHeads().removeIf(th -> th.getId().equals(task.getRelatedId()))).collect(Collectors.toList());

                        }
                    } else {
                        //activity
                        projectHeads.stream().filter(p -> p.getActivityHeads() != null && p.getActivityHeads().removeIf(ac -> ac.getId().equals(task.getActivityId()))).collect(Collectors.toList());
                    }
                }
            });
            return projectHeads;
        }
        return null;
    }

    @Override
    public ProjectDatesValidationDTO checkDateValidation(ProjectDatesValidationDTO projectDatesValidationDTO) {

        Optional<ProjectHead> projectHead = null;
        if (AppConstants.ACTIVITY_LEVEL.equalsIgnoreCase(projectDatesValidationDTO.getLevel()) && (projectDatesValidationDTO.getMethod() != null && projectDatesValidationDTO.getMethod().equalsIgnoreCase("Create"))) {
            if (projectDatesValidationDTO.getEstStartDate() != null && projectDatesValidationDTO.getEstEndDate() != null) {
                projectHead = projectHeadRepository.findById(projectDatesValidationDTO.getProjectId());
                return checkActivityCreate(projectDatesValidationDTO, projectHead.get());
            }

        } else if (AppConstants.TASK_LEVEL.equalsIgnoreCase(projectDatesValidationDTO.getLevel()) && (projectDatesValidationDTO.getMethod() != null && projectDatesValidationDTO.getMethod().equalsIgnoreCase("Create"))) {
            if (projectDatesValidationDTO.getEstStartDate() != null && projectDatesValidationDTO.getEstEndDate() != null) {
                projectHead = projectHeadRepository.findById(projectDatesValidationDTO.getProjectId());
                Optional<ActivityHead> activityHead = activityHeadRepository.findById(projectDatesValidationDTO.getActivityId());
                return checkTaskCreate(projectDatesValidationDTO, projectHead.get(), activityHead.get());
            }

        } else if (AppConstants.PROJECT_LEVEL.equalsIgnoreCase(projectDatesValidationDTO.getLevel()) && (projectDatesValidationDTO.getMethod() != null && projectDatesValidationDTO.getMethod().equalsIgnoreCase("Create"))) {
            if (projectDatesValidationDTO.getEstStartDate() != null && projectDatesValidationDTO.getEstEndDate() != null) {
                if (projectDatesValidationDTO.getEstStartDate().after(projectDatesValidationDTO.getEstEndDate())) {
                    return ProjectDatesValidationDTO.builder().message("The estimated start date and end date at project level is illogical. Please re-enter the dates.")
                            .build();
                } else if (projectDatesValidationDTO.getEstEndDate().before(projectDatesValidationDTO.getEstStartDate())) {
                    return ProjectDatesValidationDTO.builder().message("The estimated start date and end date at project level is illogical. Please re-enter the dates.")
                            .build();
                }

            }

        } else if (AppConstants.ACTIVITY_LEVEL.equalsIgnoreCase(projectDatesValidationDTO.getLevel())) {
            projectHead = projectHeadRepository.findById(projectDatesValidationDTO.getProjectId());
            Optional<ActivityHead> activityHead = null;

            if (projectDatesValidationDTO.getActivityId() != null) {
                activityHead = activityHeadRepository.findById(projectDatesValidationDTO.getActivityId());
            }

            if (activityHead != null && (projectDatesValidationDTO.getEstStartDate() != null && projectDatesValidationDTO.getEstStartDate().after(activityHead.get().getEstEndDate()))) {
                return ProjectDatesValidationDTO.builder().message("Activity estimated start date breaches its estimated end date. Please update the date.")
                        .build();

            } else if (activityHead != null && (projectDatesValidationDTO.getEstEndDate() != null && projectDatesValidationDTO.getEstEndDate().before(activityHead.get().getEstStartDate()))) {
                return ProjectDatesValidationDTO.builder().message("Activity estimated end date breaches its estimated start date. Please update the date.")
                        .build();

            } else if (projectDatesValidationDTO.getEstEndDate() != null && projectHead.get().getEstEndDate().before(projectDatesValidationDTO.getEstEndDate())) {
                return ProjectDatesValidationDTO.builder().message("Activity estimated end date breaches the project estimated end date. Do you want to update the timeline?")
                        .projectId(projectHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstEndDate() != null && projectHead.get().getEstStartDate().after(projectDatesValidationDTO.getEstEndDate())) {
                return ProjectDatesValidationDTO.builder().message("Activity estimated end date breaches the project estimated start date. Do you want to update the timeline?")
                        .projectId(projectHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstStartDate() != null && projectHead.get().getEstStartDate().after(projectDatesValidationDTO.getEstStartDate())) {
                return ProjectDatesValidationDTO.builder().message("Activity estimated start date breaches the project estimated start date. Do you want to update the timeline?")
                        .projectId(projectHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstStartDate() != null && projectHead.get().getEstEndDate().before(projectDatesValidationDTO.getEstStartDate())) {
                return ProjectDatesValidationDTO.builder().message("Activity estimated start date breaches the project estimated end date. Do you want to update the timeline?")
                        .projectId(projectHead.get().getId()).build();
            }

        } else if (AppConstants.TASK_LEVEL.equalsIgnoreCase(projectDatesValidationDTO.getLevel())) {
            projectHead = projectHeadRepository.findById(projectDatesValidationDTO.getProjectId());
            Optional<ActivityHead> activityHead = activityHeadRepository.findById(projectDatesValidationDTO.getActivityId());
            Optional<TaskHead> taskHead = null;

            if (projectDatesValidationDTO.getTaskId() != null) {
                taskHead = taskHeadRepository.findById(projectDatesValidationDTO.getTaskId());
            }

            if (taskHead != null && (projectDatesValidationDTO.getEstStartDate() != null && projectDatesValidationDTO.getEstStartDate().after(taskHead.get().getEstEndDate()))) {
                return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches its estimated end date. Please update the date.")
                        .build();

            } else if (taskHead != null && (projectDatesValidationDTO.getEstEndDate() != null && projectDatesValidationDTO.getEstEndDate().after(taskHead.get().getEstStartDate()))) {
                return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches its estimated start date. Please update the date.")
                        .build();

            } else if (projectDatesValidationDTO.getEstEndDate() != null && activityHead.get().getEstEndDate().before(projectDatesValidationDTO.getEstEndDate())) {
                if (projectDatesValidationDTO.getEstEndDate() != null && projectHead.get().getEstEndDate().before(projectDatesValidationDTO.getEstEndDate())) {
                    return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity and project estimated end date. Do you want to update the timeline?")
                            .projectId(projectHead.get().getId()).activityId(activityHead.get().getId()).build();
                }
                return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity estimated end date. Do you want to update the timeline?")
                        .activityId(activityHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstEndDate() != null && activityHead.get().getEstStartDate().after(projectDatesValidationDTO.getEstEndDate())) {
                if (projectDatesValidationDTO.getEstEndDate() != null && projectHead.get().getEstStartDate().after(projectDatesValidationDTO.getEstEndDate())) {
                    return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity and project estimated start date. Do you want to update the timeline?")
                            .projectId(projectHead.get().getId()).activityId(activityHead.get().getId()).build();
                }
                return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity estimated start date. Do you want to update the timeline?")
                        .activityId(activityHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstStartDate() != null && activityHead.get().getEstStartDate().after(projectDatesValidationDTO.getEstStartDate())) {
                if (projectDatesValidationDTO.getEstStartDate() != null && projectHead.get().getEstStartDate().after(projectDatesValidationDTO.getEstStartDate())) {
                    return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity and project estimated start date. Do you want to update the timeline?")
                            .projectId(projectHead.get().getId()).activityId(activityHead.get().getId()).build();
                }
                return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity estimated start date. Do you want to update the timeline?")
                        .activityId(activityHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstStartDate() != null && activityHead.get().getEstEndDate().before(projectDatesValidationDTO.getEstStartDate())) {
                if (projectDatesValidationDTO.getEstStartDate() != null && projectHead.get().getEstEndDate().before(projectDatesValidationDTO.getEstStartDate())) {
                    return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity and project estimated end date. Do you want to update the timeline?")
                            .projectId(projectHead.get().getId()).activityId(activityHead.get().getId()).build();
                }
                return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity estimated end date. Do you want to update the timeline?")
                        .activityId(activityHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstStartDate() != null && projectHead.get().getEstStartDate().after(projectDatesValidationDTO.getEstStartDate())) {
                return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the project estimated start date. Do you want to update the timeline?")
                        .projectId(projectHead.get().getId()).build();

            } else if (projectDatesValidationDTO.getEstEndDate() != null && projectHead.get().getEstEndDate().before(projectDatesValidationDTO.getEstEndDate())) {
                return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the project estimated end date. Do you want to update the timeline?")
                        .projectId(projectHead.get().getId()).build();
            }

        } else if (AppConstants.PROJECT_LEVEL.equalsIgnoreCase(projectDatesValidationDTO.getLevel())) {
            projectHead = projectHeadRepository.findById(projectDatesValidationDTO.getProjectId());
            List<ProjectHead> projectHeads = findAllProjectHeadWithActivityAndTask(Arrays.asList(projectDatesValidationDTO.getProjectId()));

            if (projectDatesValidationDTO.getEstStartDate() != null) {

                if (projectDatesValidationDTO.getEstStartDate().after(projectHead.get().getEstEndDate())) {
                    return ProjectDatesValidationDTO.builder().message("Project estimated start date breaches its estimated end date. Please update the date.")
                            .build();
                }
/*

                Optional<ActivityHead> activityHead = projectHeads.stream().filter(a -> a.getActivityHeads() != null)
                        .flatMap(a -> a.getActivityHeads().stream())
                        .filter(act -> projectDatesValidationDTO.getEstStartDate().after(act.getEstStartDate())).findFirst();

                if (activityHead.isPresent()) {
                    return ProjectDatesValidationDTO.builder().message("Project estimated start date should not be greater than activity estimated end date. Please change the activity date first.")
                            .build();
                }

                Optional<TaskHead> taskHead = projectHeads.stream().filter(a -> a.getActivityHeads() != null)
                        .flatMap(a -> a.getActivityHeads().stream())
                        .filter(t -> t.getTaskHeads() != null)
                        .flatMap(t -> t.getTaskHeads().stream())
                        .filter(t -> projectDatesValidationDTO.getEstStartDate().after(t.getEstStartDate())).findFirst();

                if (taskHead.isPresent()) {
                    return ProjectDatesValidationDTO.builder().message("Project estimated start date should not be greater than task estimated end date. Please change the task date first.")
                            .build();
                }
*/

            } else if (projectDatesValidationDTO.getEstEndDate() != null) {

                if (projectDatesValidationDTO.getEstEndDate().before(projectHead.get().getEstStartDate())) {
                    return ProjectDatesValidationDTO.builder().message("Project estimated end date breaches its estimated start date. Please update the date.")
                            .build();
                }
/*
                Optional<ActivityHead> activityHead = projectHeads.stream().filter(a -> a.getActivityHeads() != null)
                        .flatMap(a -> a.getActivityHeads().stream())
                        .filter(act -> projectDatesValidationDTO.getEstEndDate().before(act.getEstStartDate())).findFirst();

                if (activityHead.isPresent()) {
                    return ProjectDatesValidationDTO.builder().message("Project estimated end date should not be less than activity estimated end date. Please change the activity date first.")
                            .build();
                }

                Optional<TaskHead> taskHead = projectHeads.stream().filter(a -> a.getActivityHeads() != null)
                        .flatMap(a -> a.getActivityHeads().stream())
                        .filter(t -> t.getTaskHeads() != null)
                        .flatMap(t -> t.getTaskHeads().stream())
                        .filter(t -> projectDatesValidationDTO.getEstEndDate().before(t.getEstStartDate())).findFirst();

                if (taskHead.isPresent()) {
                    return ProjectDatesValidationDTO.builder().message("Project estimated end date should not be less than task estimated end date. Please change the task date first.")
                            .build();
                }*/
            }
        }
        return null;
    }

    private ProjectDatesValidationDTO checkActivityCreate(ProjectDatesValidationDTO projectDatesValidationDTO, ProjectHead projectHead) {

        if (projectDatesValidationDTO.getEstStartDate().after(projectDatesValidationDTO.getEstEndDate())) {
            return ProjectDatesValidationDTO.builder().message("The estimated start date and end date at activity level is illogical. Please re-enter the dates.")
                    .build();
        }
        if (projectDatesValidationDTO.getEstEndDate().before(projectDatesValidationDTO.getEstStartDate())) {
            return ProjectDatesValidationDTO.builder().message("The estimated start date and end date at activity level is illogical. Please re-enter the dates.")
                    .build();
        }
        if (projectDatesValidationDTO.getEstStartDate().before(projectHead.getEstStartDate())) {
            return ProjectDatesValidationDTO.builder().message("Activity estimated start date breaches the project estimated start date. Do you want to update the timeline?")
                    .projectId(projectHead.getId()).build();
        }
        if (projectDatesValidationDTO.getEstStartDate().after(projectHead.getEstEndDate())) {
            return ProjectDatesValidationDTO.builder().message("Activity estimated start date breaches the project estimated end date. Do you want to update the timeline?")
                    .projectId(projectHead.getId()).build();
        }
        if (projectDatesValidationDTO.getEstEndDate().before(projectHead.getEstStartDate())) {
            return ProjectDatesValidationDTO.builder().message("Activity estimated end date breaches the project estimated start date. Do you want to update the timeline?")
                    .projectId(projectHead.getId()).build();
        }
        if (projectDatesValidationDTO.getEstEndDate().after(projectHead.getEstEndDate())) {
            return ProjectDatesValidationDTO.builder().message("Activity estimated end date breaches the project estimated end date. Do you want to update the timeline?")
                    .projectId(projectHead.getId()).build();
        }
        return null;
    }

    private ProjectDatesValidationDTO checkTaskCreate(ProjectDatesValidationDTO projectDatesValidationDTO, ProjectHead projectHead, ActivityHead activityHead) {

        if (projectDatesValidationDTO.getEstStartDate().after(projectDatesValidationDTO.getEstEndDate())) {
            return ProjectDatesValidationDTO.builder().message("The estimated start date and end date at task level is illogical. Please re-enter the dates.")
                    .build();
        } else if (projectDatesValidationDTO.getEstEndDate().before(projectDatesValidationDTO.getEstStartDate())) {
            return ProjectDatesValidationDTO.builder().message("The estimated start date and end date at task level is illogical. Please re-enter the dates.")
                    .build();

        } else if (projectDatesValidationDTO.getEstStartDate().before(activityHead.getEstStartDate())) {
            if (projectDatesValidationDTO.getEstStartDate().before(projectHead.getEstStartDate())) {
                return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity and project estimated start date. Do you want to update the timeline?")
                        .projectId(projectHead.getId()).activityId(activityHead.getId()).build();
            }
            return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity estimated start date. Do you want to update the timeline?")
                    .activityId(activityHead.getId()).build();

        } else if (projectDatesValidationDTO.getEstStartDate().after(activityHead.getEstEndDate())) {
            if (projectDatesValidationDTO.getEstStartDate().after(projectHead.getEstEndDate())) {
                return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity and project estimated end date. Do you want to update the timeline?")
                        .projectId(projectHead.getId()).activityId(activityHead.getId()).build();
            }
            return ProjectDatesValidationDTO.builder().message("Task estimated start date breaches the activity estimated end date. Do you want to update the timeline?")
                    .activityId(activityHead.getId()).build();

        } else if (projectDatesValidationDTO.getEstEndDate().before(activityHead.getEstStartDate())) {
            if (projectDatesValidationDTO.getEstEndDate().before(projectHead.getEstStartDate())) {
                return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity and project estimated start date. Do you want to update the timeline?")
                        .projectId(projectHead.getId()).activityId(activityHead.getId()).build();
            }
            return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity estimated start date. Do you want to update the timeline?")
                    .activityId(activityHead.getId()).build();

        } else if (projectDatesValidationDTO.getEstEndDate().after(activityHead.getEstEndDate())) {
            if (projectDatesValidationDTO.getEstEndDate().after(projectHead.getEstEndDate())) {
                return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity and project estimated end date. Do you want to update the timeline?")
                        .projectId(projectHead.getId()).activityId(activityHead.getId()).build();
            }
            return ProjectDatesValidationDTO.builder().message("Task estimated end date breaches the activity estimated end date. Do you want to update the timeline?")
                    .activityId(activityHead.getId()).build();

        }
        return null;
    }

    @Override
    public ProjectDatesValidationDTO updateDateValidation(ProjectDatesValidationDTO projectDatesValidationDTO) {
        if (projectDatesValidationDTO.getProjectId() != null) {
            ProjectHead projectHead = projectHeadRepository.findById(projectDatesValidationDTO.getProjectId()).orElseThrow(() -> new NotFoundException(ProjectHead.class, projectDatesValidationDTO.getProjectId()));
            projectHead.setEstStartDate(projectDatesValidationDTO.getEstStartDate() != null ? projectDatesValidationDTO.getEstStartDate() : projectHead.getEstStartDate());
            projectHead.setEstEndDate(projectDatesValidationDTO.getEstEndDate() != null ? projectDatesValidationDTO.getEstEndDate() : projectHead.getEstEndDate());
            projectHead = projectHeadRepository.save(projectHead);
            String projectMessage = "Project ".concat(projectHead.getProjectName() != null ? projectHead.getProjectName() : "").concat(" timeline is updated.");

            if (projectDatesValidationDTO.getActivityId() != null) {
                ActivityHead activityHead = activityHeadRepository.findById(projectDatesValidationDTO.getActivityId()).orElseThrow(() -> new NotFoundException(ActivityHead.class, projectDatesValidationDTO.getActivityId()));
                activityHead.setEstStartDate(projectDatesValidationDTO.getEstStartDate() != null ? projectDatesValidationDTO.getEstStartDate() : activityHead.getEstStartDate());
                activityHead.setEstEndDate(projectDatesValidationDTO.getEstEndDate() != null ? projectDatesValidationDTO.getEstEndDate() : activityHead.getEstEndDate());
                activityHead = activityHeadRepository.save(activityHead);
                String activityMessage = "Project ".concat(projectHead.getProjectName() != null ? projectHead.getProjectName() : "").concat(" and Activity " + activityHead.getSummary() != null ? activityHead.getSummary() : "" + " timelines are updated.");
                return ProjectDatesValidationDTO.builder().message(activityMessage).build();
            }
            return ProjectDatesValidationDTO.builder().message(projectMessage).build();
        }
        return null;
    }

    @Override
    public ProjectSummaryViewDTO projectSummaryFindById(Long projectId) {
        ProjectSummaryViewDTO projectSummaryView = ProjectSummaryViewDTO.builder().build();
        List<ProjectHead> projectHeads = findAllProjectHeadWithActivityAndTask(Arrays.asList(projectId));
        ProjectHead projectHead = projectHeads.get(0);
        List<ProjectResourceEngagement> pres = projectResourceEngagementRepository.findAllByProjectIdAndDesignation(projectId, AppConstants.PROJECT_MANAGER);

        if (projectHead != null) {
            if (pres != null) {
                List<HRHead> hrHeads = hrHeadService.findAllByIdIn(pres.stream().map(ProjectResourceEngagement::getResourceId).collect(Collectors.toList()));
                String projectManagers = String.join(",", hrHeads.stream().map(HRHead::getName).collect(Collectors.toList()));
                projectSummaryView.setProjectManager(projectManagers.replaceAll("\\t", ""));
            }

            projectSummaryView.setId(projectHead.getId());
            projectSummaryView.setName(projectHead.getProjectName());
            projectSummaryView.setEstStartDate(projectHead.getEstStartDate() != null ? projectHead.getEstStartDate() : null);
            projectSummaryView.setEstEndDate(projectHead.getEstEndDate() != null ? projectHead.getEstEndDate() : null);
            projectSummaryView.setActualStartDate(projectHead.getActualStartDate() != null ? projectHead.getActualStartDate() : null);
            projectSummaryView.setActualEndDate(projectHead.getActualEndDate() != null ? projectHead.getActualEndDate() : null);
            projectSummaryView.setEstBudgetCap(projectHead.getEstBudgetCap());
            projectSummaryView.setStatus(projectHead.getStatus());
            projectSummaryView.setAssignTo("");

            long projectDuration = 0l;
            if (projectHead.getEstStartDate() != null && projectHead.getEstEndDate() != null) {
                String projectStartDate = Utility.getDateString(projectHead.getEstStartDate(), Utility.SYSTEM_DATE_FORMAT);
                String projectEndDate = Utility.getDateString(projectHead.getEstEndDate(), Utility.SYSTEM_DATE_FORMAT);
                // parsing the string date into LocalDate objects.
                LocalDate projEstStartDate = LocalDate.parse(projectStartDate);
                LocalDate projEstEndDate = LocalDate.parse(projectEndDate);
                projectDuration = ChronoUnit.DAYS.between(projEstStartDate, projEstEndDate);
            }

            projectSummaryView.setDuration(projectDuration);

            List<ActivitySummaryViewDTO> activitySummaryViewDTOS = new ArrayList<>();
            if (projectHead.getActivityHeads().size() != 0) {
                AtomicInteger activityCounter = new AtomicInteger();
                projectHead.getActivityHeads().forEach(act -> {
                    long activityDuration = 0l;
                    if (act.getEstStartDate() != null && act.getEstEndDate() != null) {
                        String actStartDate = Utility.getDateString(act.getEstStartDate(), Utility.SYSTEM_DATE_FORMAT);
                        String actEndDate = Utility.getDateString(act.getEstEndDate(), Utility.SYSTEM_DATE_FORMAT);

                        // parsing the string date into LocalDate objects.
                        LocalDate actEstStartDate = LocalDate.parse(actStartDate);
                        LocalDate actEstEndDate = LocalDate.parse(actEndDate);
                        activityDuration = ChronoUnit.DAYS.between(actEstStartDate, actEstEndDate);
                    }
                    activitySummaryViewDTOS.add(ActivitySummaryViewDTO.builder().id(act.getId()).name(act.getSummary())
                            .estStartDate(act.getEstStartDate() != null ? act.getEstStartDate() : null)
                            .estEndDate(act.getEstEndDate() != null ? act.getEstEndDate() : null)
                            .actualStartDate(act.getActualStartDate() != null ? act.getActualStartDate() : null)
                            .actualEndDate(act.getActualEndDate() != null ? act.getActualEndDate() : null)
                            .assignTo("")
                            .estBudgetCap(act.getActivityEstBudgetCap())
                            .status(act.getStatus())
                            .duration(activityDuration)
                            .projectManager("").build());

                    List<TaskSummaryViewDTO> taskSummaryViewDTOS = new ArrayList<>();
                    if (act.getTaskHeads().size() != 0) {
                        act.getTaskHeads().forEach(task -> {
                            long taskDuration = 0l;
                            if (task.getEstStartDate() != null && task.getEstEndDate() != null) {
                                String taskStartDate = Utility.getDateString(task.getEstStartDate(), Utility.SYSTEM_DATE_FORMAT);
                                String taskEndDate = Utility.getDateString(task.getEstEndDate(), Utility.SYSTEM_DATE_FORMAT);

                                // parsing the string date into LocalDate objects.
                                LocalDate taskEstStartDate = LocalDate.parse(taskStartDate);
                                LocalDate taskEstEndDate = LocalDate.parse(taskEndDate);
                                taskDuration = ChronoUnit.DAYS.between(taskEstStartDate, taskEstEndDate);
                            }

                            taskSummaryViewDTOS.add(TaskSummaryViewDTO.builder().id(task.getId()).name(task.getSummary())
                                    .estStartDate(task.getEstStartDate() != null ? task.getEstStartDate() : null)
                                    .estEndDate(task.getEstEndDate() != null ? task.getEstEndDate() : null)
                                    .actualStartDate(task.getActualStartDate() != null ? task.getActualStartDate() : null)
                                    .actualEndDate(task.getActualEndDate() != null ? task.getActualEndDate() : null)
                                    .assignTo("")
                                    .estBudgetCap(task.getActivityEstBudgetCap())
                                    .status(task.getStatus())
                                    .duration(taskDuration)
                                    .projectManager("").build());

                        });
                    }
                    activitySummaryViewDTOS.get(activityCounter.get()).setTaskSummaryViewDTOS(taskSummaryViewDTOS);
                    activityCounter.getAndIncrement();
                });
            }
            projectSummaryView.setActivitySummaryViewDTOS(activitySummaryViewDTOS);
        }
        return projectSummaryView;
    }

    @Override
    public Map showProjectListings(Map response, Integer size, int pageNumber, String groupBy, String name, String status, String template, String type, String owner, String createdAt, String searchWords, Long loggedInUserAcctId, String loggedInUserPrivLevel, Long loggedInUserEntityId) {
        int privLevel = Integer.parseInt(loggedInUserPrivLevel);
        ProjectManagementPaginationTile projectManagementPaginationTile = null;
        List<ProjectManagementTile> projectManagementResponse = new ArrayList<>();
        try {
            // Check if the user is admin or super admin
            if (privLevel >= Constants.PRIVILEGE_LEVELS.ADMIN_PRIV_LEVEL) {
                projectManagementPaginationTile = dataExchange.showProjectListings(size, pageNumber, groupBy, name, status, template, type, owner, createdAt, searchWords, null);
            } else if (loggedInUserAcctId != null) {
                projectManagementPaginationTile = dataExchange.showProjectListings(size, pageNumber, groupBy, name, status, template, type, owner, createdAt, searchWords, loggedInUserAcctId);
                if (projectManagementPaginationTile != null && projectManagementPaginationTile.getData() != null) {
                    List<ProjectManagementTile> data = projectManagementPaginationTile.getData();
                    // Filter resources that are not the project owner
                    List<ProjectManagementTile> filteredResources = data.stream()
                            .filter(obj -> (obj.getProjectOwnerAcctId() == null) || (!Objects.equals(obj.getProjectOwnerAcctId().intValue(), loggedInUserAcctId)))
                            .collect(Collectors.toList());
                    if (!filteredResources.isEmpty() && loggedInUserEntityId != null) {
                        // Filter resources based on entity
                        List<String> projectIds = filteredResources.stream()
                                .map(ProjectManagementTile::getProjectId)
                                .collect(Collectors.toList());
                        List<ProjectManagementEntityTile> resources = entityGroupService.getResourceByRefIdAndEntityId(projectIds, loggedInUserEntityId);
                        for (ProjectManagementEntityTile resource : resources) {
                            Optional<ProjectManagementTile> project = filteredResources.stream()
                                    .filter(obj -> obj.getProjectId().equals(resource.getProjectId()))
                                    .findFirst();
                            project.ifPresent(projectManagementResponse::add);
                        }
                        projectManagementPaginationTile.setData(projectManagementResponse);
                    } else {
                        // If the project owner JSON is not present in the response, set it to an empty list
                        List<ProjectManagementTile> projectOwnerList = data.stream()
                                .filter(obj -> obj.getProjectOwnerAcctId() != null)
                                .collect(Collectors.toList());

                        if (projectOwnerList.size() != projectManagementPaginationTile.getData().size()) {
                            projectManagementPaginationTile.setData(projectManagementResponse);
                            return Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
                        }
                    }
                }
            }

            List<ProjectManagementTile> data = projectManagementPaginationTile.getData();
            // Update resource count and other properties
            data.forEach(project -> {
                Integer resources = entityGroupService.getResourceCountByRefId(project.getProjectId()) != null ? entityGroupService.getResourceCountByRefId(project.getProjectId()) : 0;
                project.setResources(resources);
                project.setEmployeeDetailDTO(project.getEmployeeAcctId() != null && project.getEmployeeAcctId().matches("\\d+")  ? employeeDetailRepository.findEmployeeDetailDTOByAcctId(Long.parseLong(project.getEmployeeAcctId())) : null);
                project.setEmployeeAcctId(null);
                List<DocuLibrary> docuLibraries = docuLibraryService.findByCodeRefIdAndCodeRefType(project.getProjectId(), "PROJ_THMB");
                project.setImageUrl(docuLibraries.stream().findFirst().map(DocuLibrary::getUri).orElse(null));
            });
            projectManagementPaginationTile.setData(data);
            String jsonTrackerList = gson.toJson(projectManagementPaginationTile);
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "List returned successfully", jsonTrackerList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }

        return response;
    }


    @Override
    public Map getAllProjectListingsWithFilters(Map response, Integer size, int pageNumber, String status, String template, String type, String owner, String createdAt) {

        try {

            ProjectManagementPaginationTile projectManagementPaginationTile = dataExchange.showProjectListingsWithFilters(size, pageNumber, status, template, type, owner, createdAt);
            List<ProjectManagementTile> data = projectManagementPaginationTile.getData();
            data.stream().forEach(project -> {

                Integer resources = entityGroupService.getResourceCountByRefId(project.getProjectId()) != null ?
                        entityGroupService.getResourceCountByRefId(project.getProjectId()) : 0;
                project.setResources(resources);
                project.setEmployeeDetailDTO(EmployeeDetailDTO.builder().employeeName(project.getEmployeeAcctId()).build());
                List<DocuLibrary> docuLibraries = docuLibraryService.findByCodeRefIdAndCodeRefType(project.getProjectId(), "PROJ_THMB");
                project.setImageUrl(docuLibraries.stream().findFirst().map(DocuLibrary::getUri).orElse(null));
            });
            projectManagementPaginationTile.setData(data);
            String jsonTrackerList = gson.toJson(projectManagementPaginationTile);
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "list returned successfully", jsonTrackerList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response = Utility.generateResponseMap(response, HttpStatus.NOT_FOUND.toString(), "Data Not Found", null);
        }

        return response;
    }

    @Override
    public ResponseEntity showHierarchySectionDetail(String projectId, String sectionId, String loggedInUserprivLevel, Long loggedInUserAcctId, Long loggedInUserEntityId) {
        int privLevel = Integer.parseInt(loggedInUserprivLevel);
        ResponseEntity response = null;
        // try {
        //for admin and super admin
        if (privLevel >= Constants.PRIVILEGE_LEVELS.ADMIN_PRIV_LEVEL) {
            response = dataExchange.showHierarchySectionDetail(projectId, sectionId, loggedInUserprivLevel, null);
        }
        //for project owner
        else if (loggedInUserAcctId != null) {
            response = dataExchange.showHierarchySectionDetail(projectId, sectionId, loggedInUserprivLevel, loggedInUserAcctId);
            //for resource
            if (loggedInUserAcctId != null && (response != null && response.getBody() == null) && loggedInUserEntityId != null) {
                response = dataExchange.showHierarchySectionDetail(projectId, sectionId, loggedInUserprivLevel, null);
                if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        List<JsonNode> filteredData = new ArrayList<>();
                        JsonNode jsonArray = objectMapper.readTree(response.getBody().toString());
                        for (JsonNode jsonObject : jsonArray) {
                            JsonNode tasksArray = jsonObject.get("tasks");
                            List<JsonNode> filteredTasks = new ArrayList<>();
                            List<String> tasksIds = new ArrayList<>();
                            for (JsonNode task : tasksArray) {
                                String taskId = task.get("_id").get("$oid").asText();
                                if (!tasksIds.contains(taskId)) {
                                    tasksIds.add(taskId);
                                }
                            }
                            List<ProjectManagementEntityTile> resources = entityGroupService.getResourceByRefIdAndEntityId(tasksIds, loggedInUserEntityId);
                            if (resources.size() > 0) {
                                for (JsonNode task : tasksArray) {
                                    String taskId = task.get("_id").get("$oid").asText();
                                    if (resources.stream().anyMatch(resource -> resource.getProjectId().equals(taskId))) {
                                        filteredTasks.add(task);
                                    }
                                }
                            }
                            // Update the current object with the filtered tasks
                            ((ObjectNode) jsonObject).set("tasks", objectMapper.valueToTree(filteredTasks));
                            filteredData.add(jsonObject);

                        }
                        // Serialize the List<JsonNode> to a JSON string
                        String json = objectMapper.writeValueAsString(filteredData);
                        // Create a ResponseEntity with the JSON string as the response body
                        return new ResponseEntity<>(json, HttpStatus.OK);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        response = ResponseEntity.status(HttpStatus.OK).body(response);
        return response;
    }

    @Override
    public BaseResponse showProjectListingFilterDropDown() {
        ProjectManagementFilterDTO result = new ProjectManagementFilterDTO();
        try {
            result = dataExchange.showProjectListingFilterDropDown();
            List<Long> acctIds = result.getOwner().stream().filter(s -> s.matches("\\d+")).map(Long::parseLong).collect(Collectors.toList());
            result.setOwner(null);
            result.setEmployeeDetailDTOList(employeeDetailRepository.findAllEmployeeDetailDTOByAcctIds(acctIds));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.NOT_FOUND.value()).message(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).data(result).message(AppConstants.DATA_FOUND_SUCCESSFULLY).build();
    }
}
