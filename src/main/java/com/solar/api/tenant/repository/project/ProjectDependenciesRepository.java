package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectDependencies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectDependenciesRepository extends JpaRepository<ProjectDependencies, Long> {

    ProjectDependencies findByActivityIdAndRelatedAtAndRelatedId(Long activityId, String relatedAt, Long relatedId);
    ProjectDependencies findByTaskIdAndRelatedAtAndRelatedId(Long taskId, String relatedAt, Long relatedId);
    ProjectDependencies findByProjectIdAndRelatedAtAndRelatedId(Long projectId, String relatedAt, Long relatedId);
    List<ProjectDependencies> findAllByRelatedAtAndRelatedIdIn(String relatedAt, List<Long> relatedIds);
    List<ProjectDependencies> findAllByProjectId(Long projectId);
    List<ProjectDependencies> findAllByRelatedAtAndRelatedIdAndPreDepType(String relatedAt, Long projectId, String depType);

    @Query(value = "SELECT pd FROM ProjectDependencies pd " +
            "where pd.preDepType <> 'Related' and (pd.projectId=:id OR (pd.relatedAt='related.project' and pd.relatedId=:id))")
    List<ProjectDependencies> getAllProjectByProjectIdAndRelatedId(@Param("id") Long id);

    @Query(value = "SELECT pd FROM ProjectDependencies pd " +
            "where pd.preDepType <> 'Related' and (pd.activityId=:id OR (pd.relatedAt='related.activity' and pd.relatedId=:id ))")
    List<ProjectDependencies> getAllActivityByActivityIdAndRelatedId(@Param("id") Long id);

    @Query(value = "SELECT pd FROM ProjectDependencies pd " +
            "where pd.preDepType <> 'Related' and (pd.taskId=:id OR (pd.relatedAt='related.task' and pd.relatedId =:id ))")
    List<ProjectDependencies> getAllTaskByTaskIdAndRelatedId(@Param("id") Long id);

}
