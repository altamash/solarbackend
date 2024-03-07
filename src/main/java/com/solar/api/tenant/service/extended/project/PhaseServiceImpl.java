package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.project.PhaseDTO;
import com.solar.api.tenant.mapper.extended.project.PhaseMapper;
import com.solar.api.tenant.model.AnalyticalCalculation;
import com.solar.api.tenant.model.extended.project.Phase;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import com.solar.api.tenant.repository.project.ActivityHeadRepository;
import com.solar.api.tenant.repository.project.PhaseRepository;
import com.solar.api.tenant.service.extended.register.RegisterHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.PhaseMapper.toPhaseDTO;

@Service
public class PhaseServiceImpl implements PhaseService {

    @Autowired
    PhaseRepository phaseRepository;

    @Autowired
    RegisterHierarchyService registerHierarchyService;

    @Autowired
    private ActivityHeadRepository activityHeadRepository;
    @Autowired
    private ProjectService projectService;


    @Override
    public Phase saveOrUpdate(Phase phase, Long projectId) {
        phase.setProjectHead(projectService.findById(projectId));
        if (phase.getId() != null) {
            Phase phaseData =
                    phaseRepository.findById(phase.getId()).orElseThrow(() -> new NotFoundException(Phase.class, phase.getId()));
            if (phaseData == null) {
                throw new NotFoundException(AnalyticalCalculation.class, phase.getId());
            }
            Phase phaseUpdate = PhaseMapper.toUpdatedPhase(phaseData, phase);
            return phaseRepository.save(phaseUpdate);
        }
        Phase phaseWithNameAndProject = findByProjectHeadIdAndPhaseName(phase.getProjectHead().getId(), phase.getPhaseName());
        if(phaseWithNameAndProject != null){
            throw new AlreadyExistsException(Phase.class, phase.getPhaseName(), String.valueOf(phase.getProjectHead().getId()));
        }
        return phaseRepository.save(phase);
    }

    @Override
    public List<Phase> saveAll(List<Phase> phases) {
        return phaseRepository.saveAll(phases);
    }

    @Override
    public Phase findById(Long id) {
        return phaseRepository.findById(id).orElseThrow(() -> new NotFoundException(Phase.class, id));
    }

    @Override
    public Phase findByProjectHeadIdAndPhaseName(Long projectId, String phaseName) {
        return phaseRepository.findByProjectHeadIdAndPhaseName(projectId, phaseName);
    }

    @Override
    public List<Phase> findByProjectHead(Long projectId) {
        return phaseRepository.findByProjectHeadId(projectId);
    }

    @Override
    public Phase findByExternalReferenceID(String value) {
        return phaseRepository.findByExternalReferenceId(value);
    }

    @Override
    public List<Phase> findByParentPhaseId(Long id) {
        return phaseRepository.findByParentPhaseId(id);
    }

    @Override
    public List<Phase> findByLevel(String level) {
        return phaseRepository.findByLevel(level);
    }

    @Override
    public List<Phase> findByRegisterHierarchyId(Long id) {
        return phaseRepository.findByRegisterHierarchyId(id);
    }

    @Override
    public List<Phase> findAll() {
        return phaseRepository.findAll();
    }

    @Override
    public PhaseDTO findAllActivityByPhaseId(String phaseId) {
        Phase phase = findById(Long.valueOf(phaseId));
        PhaseDTO phaseDTO = toPhaseDTO(phase);
        List<ActivityHead> activityHeads = activityHeadRepository.findAllActivityByPhase(phaseId);
//        phaseDTO.setActivityHeadDTOs(toActivityHeadDTOs(activityHeads));
        return phaseDTO;
    }

    @Override
    public List<PhaseDTO> findAllActivityByProjectId(Long projectId) {
        List<ActivityHead> activityHeads = activityHeadRepository.findByProjectHeadId(projectId);
        List<PhaseDTO> phaseDTOList = new ArrayList<>();
//        for (int i = 0; i <= activityHeads.size(); i++) {
//            if (activityHeads.get(i).getPhase() != null) {
//                Phase phase = findById(Long.valueOf(activityHeads.get(i).getPhase()));
//                if (Long.parseLong(activityHeads.get(i).getPhase()) == phase.getId()) {
//                    phaseDTOList.add(makeObject(activityHeads.get(i), phase));
//                }
//            }
//        }
        return null;
    }

    @Override
    public void delete(Long id) {
        phaseRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        phaseRepository.deleteAll();
    }
}
