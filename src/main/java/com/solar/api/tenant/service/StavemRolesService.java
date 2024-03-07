package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.stavem.StavemRoles;

import java.util.List;

public interface StavemRolesService {

    List<StavemRoles> saveAll(List<StavemRoles> stavemRoles);

    List<StavemRoles> findByEmployeeName(String employeeName);

    void deleteAll();

    ObjectNode dumpEngagementRoles();

    List<StavemRoles> getAll();

    StavemRoles findByEmployeeNameAndPhaseId(String employeeName, String phaseId);

    ObjectNode dumpProjectData();

    ObjectNode dumpAttendanceLogs();

    ObjectNode dumpPhases();

}
