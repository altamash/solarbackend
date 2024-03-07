package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectGanttChartTemplate;
import com.solar.api.tenant.model.extended.project.ProjectHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectHeadRepository extends JpaRepository<ProjectHead, Long> {

    List<ProjectHead> findAllById(Long id);

    List<ProjectHead> findAllByRegisterId(Long registerHeadId);

    ProjectHead findByExternalReferenceId(String value);

    @Query(value = "SELECT ph.id projectId, ah.id activityId, th.id taskId, ph.project_name projectName, ah.summary activitySummary, th.summary taskSummary, \n" +
            "th.task_type taskType, ph.status projectStatus, ah.status activityStatus, th.status taskStatus," +
            "case \n" +
            "when th.status = 'Completed' then '100' \n" +
            "when th.status = 'Overdue' then '95' \n" +
            "when th.status = 'In Plan/Not Started' then '0' \n" +
            "when th.status = 'At Risk' then '30' \n" +
            "when th.status in ('Progress on Track') then '50' \n" +
            "end taskProgress,\n" +
            "ph.project_manager projectManager, \n" +
            "cast(ph.actual_start_date as date) projectActualStartDate, cast(ph.actual_end_date as date) projectActualEndDate, \n" +
            "cast(ph.est_start_date as date) projectEstStartDate, cast(ph.est_end_date as date) projectEstEndDate,\n" +
            "cast(ah.actual_start_date as date) activityActualStartDate, cast(ah.actual_end_date as date) activityActualEndDate, \n" +
            "cast(ah.est_start_date as date) activityEstStartDate, cast(ah.est_end_date as date) activityEstEndDate, \n" +
            "cast(th.actual_start_date as date) taskActualStartDate, cast(th.actual_end_date as date) taskActualEndDate, \n" +
            "cast(th.est_start_date as date) taskEstStartDate, cast(th.est_end_date as date) taskEstEndDate,\n" +
            "datediff(th.actual_end_date, th.est_end_date) taskOverdue, \n" +
            "datediff(ph.actual_end_date, ph.actual_start_date) projectActualDuration, \n" +
            "datediff(ph.est_end_date, ph.est_start_date) projectEstDuration, \n" +
            "datediff(ah.actual_end_date, ah.actual_start_date) activityActualDuration, \n" +
            "datediff(ah.est_end_date, ah.est_start_date) activityEstDuration, \n" +
            "datediff(th.actual_end_date, th.actual_start_date) taskActualDuration, \n" +
            "datediff(th.est_end_date, th.est_start_date) taskEstDuration \n" +
            "FROM project_head ph \n" +
            "inner join activity_head ah \n" +
            "on ph.id = ah.project_id \n" +
            "inner join task_head th \n" +
            " on ah.id = th.activity_id ",
            //" where ph.id in (126, 133,125) ",
            nativeQuery = true)
    List<ProjectGanttChartTemplate> projectGanttChart();

    List<ProjectHead> findAllByIdIn(List<Long> ids);

    @Query("SELECT ph FROM ProjectHead ph " +
            "LEFT JOIN FETCH ph.phases ps " +
            "LEFT JOIN FETCH ps.activityHeads ah " +
            "LEFT JOIN FETCH ah.taskHeads th " +
            "where ph.id = :id")
    ProjectHead findByIdFetchAll(Long id);
}
