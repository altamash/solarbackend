package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectResourceEngagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectResourceEngagementRepository extends JpaRepository<ProjectResourceEngagement, Long> {

    List<ProjectResourceEngagement> findAllByProjectId(Long projectId);

    List<ProjectResourceEngagement> findAllByTaskId(Long taskId);

    List<ProjectResourceEngagement> findByProjectIdAndResourceId(Long projectId, Long resourceId);

    ProjectResourceEngagement findByProjectIdAndResourceIdAndTaskId(Long projectId, Long resourceId, Long taskId);

    ProjectResourceEngagement findByProjectIdAndResourceIdAndEngagementRoleId(Long projectId, Long resourceId, Long engagementRoleId);

    ProjectResourceEngagement findByProjectIdAndResourceIdAndEngagementRoleIdAndTaskId(Long projectId,
                                                                                       Long resourceId,
                                                                                       Long engagementRoleId,
                                                                                       Long taskId);

    ProjectResourceEngagement findByTaskId(Long taskId);

    List<ProjectResourceEngagement> findAllByResourceId(Long resourceId);

    List<ProjectResourceEngagement> findAllByProjectIdAndDesignation(Long projectId, String designation);

}
