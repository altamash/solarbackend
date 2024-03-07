package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.mapper.extended.project.ProjectAssociatedResourceDTO;
import com.solar.api.tenant.model.extended.project.ProjectResourceEngagement;

import java.util.List;

public interface ProjectResourceEngagementService {

    ProjectResourceEngagement save(ProjectResourceEngagement projectResourceEngagement);

    List<ProjectResourceEngagement> findAllByProjectId(Long projectId);

    List<ProjectResourceEngagement> findAllByTaskId(Long taskId);

    List<ProjectAssociatedResourceDTO> findResourceByProjectId(Long projectId);

    ProjectAssociatedResourceDTO findOneResourceByProjectId(Long projectEngagementId);

    List<ProjectResourceEngagement> findByProjectIdAndResourceId(Long projectId, Long resourceId);

    ProjectResourceEngagement findByProjectIdAndResourceIdAndTaskId(Long projectId, Long resourceId, Long taskId);

    ProjectResourceEngagement findByProjectIdAndResourceIdAndEngagementRoleId(Long projectId, Long resourceId, Long engagementRoleId);

    ProjectResourceEngagement findByProjectIdAndResourceIdAndEngagementRoleIdAndTaskId(Long projectId,
                                                                                       Long resourceId,
                                                                                       Long engagementRoleId,
                                                                                       Long taskId);

    List<ProjectAssociatedResourceDTO> findResourceByTaskId(Long taskId);

    List<ProjectAssociatedResourceDTO> findTaskByResourceId(Long projectId, Long resourceId);

    ProjectResourceEngagement update(ProjectResourceEngagement projectResourceEngagement);

    ProjectResourceEngagement findById(Long id);

    List<ProjectResourceEngagement> findAllProjectByResourceId(Long loginId);

    void deleteById(Long id);

    List<ProjectResourceEngagement> findAllByProjectIdAndDesignation(Long projectId, String designation);
}
