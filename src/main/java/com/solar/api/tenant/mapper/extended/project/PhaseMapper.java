package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.mapper.extended.project.activity.ActivityMapper;
import com.solar.api.tenant.model.extended.project.Phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PhaseMapper {

    public static Phase toPhase(PhaseDTO phaseDTO) {
        if (phaseDTO == null) {
            return null;
        }
        return Phase.builder()
                .id(phaseDTO.getId())
                .level(phaseDTO.getLevel())
                .registerHierarchyId(phaseDTO.getRegisterHierarchyId())
                .phaseName(phaseDTO.getPhaseName())
                .parentPhaseId(phaseDTO.getParentPhaseId())
                .externalReferenceId(phaseDTO.getExternalReferenceId())
                .activityHeads(new HashSet<>(ActivityMapper.toActivityHeadDTOs(new ArrayList(phaseDTO.getActivityHeads() != null ?
                        phaseDTO.getActivityHeads() : Collections.emptySet()))))
                .build();
    }

    public static PhaseDTO toPhaseDTO(Phase phase) {
        if (phase == null) {
            return null;
        }
        return PhaseDTO.builder()
                .id(phase.getId())
                .level(phase.getLevel())
                .registerHierarchyId(phase.getRegisterHierarchyId())
                .phaseName(phase.getPhaseName())
                .activityHeads(new HashSet<>(ActivityMapper.toActivityHeadDTOs(new ArrayList(phase.getActivityHeads()))) != null
                        ? new HashSet<>(ActivityMapper.toActivityHeadDTOs(new ArrayList(phase.getActivityHeads()))) : Collections.emptySet())
                .parentPhaseId(phase.getParentPhaseId())
                .externalReferenceId(phase.getExternalReferenceId())
                .build();
    }

    public static Phase toUpdatedPhase(Phase phase,Phase phaseUpdate) {
        phase.setRegisterHierarchyId(phaseUpdate.getRegisterHierarchyId() == null ? phase.getRegisterHierarchyId() : phaseUpdate.getRegisterHierarchyId());
        phase.setPhaseName(phaseUpdate.getPhaseName() == null ? phase.getPhaseName() : phaseUpdate.getPhaseName());
        phase.setLevel(phaseUpdate.getLevel() == null ? phase.getLevel() : phaseUpdate.getLevel());
        phase.setParentPhaseId(phaseUpdate.getParentPhaseId() == null ? phase.getParentPhaseId() : phaseUpdate.getParentPhaseId());
        phase.setExternalReferenceId(phaseUpdate.getExternalReferenceId() == null ? phase.getExternalReferenceId() : phaseUpdate.getExternalReferenceId());
        return phase;
    }

    public static List<Phase> toPhases(List<PhaseDTO> phaseDTOS) {
        return phaseDTOS.stream().map(p -> toPhase(p)).collect(Collectors.toList());
    }

    public static List<PhaseDTO> toPhaseDTOs(List<Phase> phases) {
        return phases.stream().map(p -> toPhaseDTO(p)).collect(Collectors.toList());
    }
}
