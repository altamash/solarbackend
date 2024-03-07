package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhaseRepository extends JpaRepository <Phase, Long> {

    List<Phase> findByRegisterHierarchyId(Long id);

    List<Phase> findByProjectHeadId(Long projectId);

    Phase findByProjectHeadIdAndPhaseName(Long projectId, String phaseName);

    Phase findByExternalReferenceId(String value);

    List<Phase> findByLevel(String level);

    List<Phase> findByParentPhaseId(Long id);
}
