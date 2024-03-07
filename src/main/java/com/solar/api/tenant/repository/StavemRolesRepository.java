package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.stavem.StavemRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StavemRolesRepository  extends JpaRepository<StavemRoles, Long> {

    StavemRoles findByEmployeeNameAndPhaseId(String employeeName, String phaseId);

    List<StavemRoles> findByEmployeeName(String employeeName);
}
