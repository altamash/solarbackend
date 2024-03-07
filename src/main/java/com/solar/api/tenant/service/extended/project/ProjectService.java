package com.solar.api.tenant.service.extended.project;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.extended.project.*;
import com.solar.api.tenant.model.extended.project.ProjectDependencies;
import com.solar.api.tenant.model.extended.project.ProjectDetail;
import com.solar.api.tenant.model.extended.project.ProjectHead;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProjectService {

    /**
     * Project Head
     */
    ProjectHead saveProjectHead(ProjectHead projectHead);

    ProjectHead updateProjectHead(ProjectHead projectHead);

    ProjectHead findById(Long id);

    ProjectHead findByExternalReferenceId(String value);

    List<ProjectHead> findAllProjectHead();

    List<ProjectHead> findAllProjectHeadWithActivityAndTask(List<Long> projectIds);

    List<ProjectHead> findAllByProjectHeadId(Long id);

    List<ProjectHead> findAllByRegisterId(Long registerId);

    ProjectHead findByIdFetchAll(Long projectId);


    void deleteProjectHead(Long id);

    void deleteAllProjectHead();

    /**
     * Project Detail
     */
    ProjectDetail saveProjectDetail(ProjectDetail projectDetail);

    //ProjectDetail updateProjectDetail(ProjectDetail projectDetail);

    ProjectDetail findProjectDetailById(Long id);

    List<ProjectDetail> findAllProjectDetail();

    List<ProjectDetail> updateProjectDetails(List<ProjectDetail> projectDetails, ProjectHead projectHead);

    void deleteProjectDetail(Long id);

    void deleteAllProjectDetail();

    /**
     * Project Dependencies
     */

    List<ProjectDependencies> saveProjectDependencies(List<ProjectDependencies> projectDependencies);

    List<ProjectDependencies> updateProjectDependencies(List<ProjectDependencies> projectDependenciesList);

    List<ProjectDependencies> findAllProjectDependenciesByProjectId(Long projectId);

    ProjectDependenciesViewListDTO findAllActivitiesAndTasks(Long projectId);

    void deleteByProjectDependenciesId(Long id);

    List<ProjectHeadChartDTO> ganttChart();

    List<ProjectHead> findAllProjectDependenciesById(Long id, String fieldName);

    List<ProjectHead> unLinkedDependencies(List<LinkedUnLinkedDependenciesDTO> dependenciesDTOS);

    ProjectDatesValidationDTO checkDateValidation(ProjectDatesValidationDTO projectDatesValidationDTO);

    ProjectDatesValidationDTO updateDateValidation(ProjectDatesValidationDTO projectDatesValidationDTO);

    ProjectSummaryViewDTO projectSummaryFindById(Long projectId);

    Map showProjectListings(Map response, Integer size, int pageNumber, String groupBy, String name, String status, String template, String type, String owner, String createdAt, String searchWords, Long loggedInUserAcctId, String privLevel, Long loggedInUserEntityId);

    Map getAllProjectListingsWithFilters(Map response, Integer size, int pageNumber, String status, String template, String type, String owner, String createdAt);

    ResponseEntity showHierarchySectionDetail(String projectId, String sectionId, String loggedInUserprivLevel, Long loggedInUserAcctId, Long loggedInUserEntityId);
    BaseResponse showProjectListingFilterDropDown();
}
