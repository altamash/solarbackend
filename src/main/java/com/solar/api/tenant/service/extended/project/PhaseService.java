package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.mapper.extended.project.PhaseDTO;
import com.solar.api.tenant.model.extended.project.Phase;

import java.util.List;

public interface PhaseService {

    Phase saveOrUpdate(Phase phase, Long projectId);

    List<Phase> saveAll(List<Phase> phases);

    Phase findById(Long id);

    Phase findByProjectHeadIdAndPhaseName(Long projectId, String phaseName);

    List<Phase> findByProjectHead(Long projectId);

    Phase findByExternalReferenceID(String value);

    List<Phase> findByParentPhaseId(Long id);

    List<Phase> findByLevel(String level);

    List<Phase> findByRegisterHierarchyId(Long id);

    List<Phase> findAll();

    PhaseDTO findAllActivityByPhaseId(String phaseId);

    List<PhaseDTO> findAllActivityByProjectId(Long projectId);


    void delete(Long id);

    void deleteAll();
}
