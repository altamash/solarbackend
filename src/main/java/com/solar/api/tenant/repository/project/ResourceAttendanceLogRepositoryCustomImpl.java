package com.solar.api.tenant.repository.project;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class ResourceAttendanceLogRepositoryCustomImpl implements ResourceAttendanceLogRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Double hoursByRole(Long employeeId, Long roleId, Long taskId) {
        Query query = em.createNativeQuery("SELECT sum(hours) as total_hours FROM resource_attendance_log " +
                "where external_role_id = :roleId and employee_id = :employeeId and task_id = :taskId");
        query.setParameter("roleId", roleId);
        query.setParameter("employeeId", employeeId);
        query.setParameter("taskId", taskId);
        return (Double) query.getSingleResult();
    }

    @Override
    public Double hoursByEmployeeIdAndProjectId(Long employeeId, Long projectId) {
        Query query = em.createNativeQuery("SELECT sum(hours) as total_hours FROM resource_attendance_log where employee_id = :employeeId" +
                " and project_id = :projectId");
        query.setParameter("projectId", projectId);
        query.setParameter("employeeId", employeeId);
        return (Double) query.getSingleResult();
    }

    @Override
    public Double hoursByTaskId(Long taskId) {
        Query query = em.createNativeQuery("SELECT sum(hours) as total_hours FROM resource_attendance_log " +
                "where task_id = :taskId");
        query.setParameter("taskId", taskId);
        return (Double) query.getSingleResult();
    }

//    @Override
//    public List<ResourceAttendanceLog> findByEmployeeIdAndTaskIdAndWorkDate(Long employeeId, Long taskId, String workDate) {
//        Query query = em.createNativeQuery("SELECT * FROM resource_attendance_log where employee_id = :employeeId " +
//                "and task_id = :taskId and work_date = :workDate");
//        query.setParameter("employeeId", employeeId);
//        query.setParameter("taskId", taskId);
//        query.setParameter("workDate", workDate);
//        return query.getResultList();
//    }
}
