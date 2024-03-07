package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.extended.project.PhaseDTO;
import com.solar.api.tenant.service.extended.project.PhaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.PhaseMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PhaseController")
@RequestMapping(value = "/phases")
public class PhaseController {

    @Autowired
    private PhaseService phaseService;

    @PostMapping("/saveOrUpdate/{projectHeadId}")
    public PhaseDTO addPhase(@RequestBody PhaseDTO phaseDTO, @PathVariable Long projectHeadId) {
        return toPhaseDTO(phaseService.saveOrUpdate(toPhase(phaseDTO), projectHeadId));
    }

    @GetMapping("/head/{id}")
    public PhaseDTO findPhaseById(@PathVariable Long id) {

        return toPhaseDTO(phaseService.findById(id));
    }

    @GetMapping("/findByRegisterHierarchy/{id}")
    public List<PhaseDTO> findByRegisterHierarchy(@PathVariable Long id) {
        return toPhaseDTOs(phaseService.findByRegisterHierarchyId(id));
    }

    @GetMapping("/findByLevel/{level}")
    public List<PhaseDTO> findByLevel(@PathVariable String level) {
        return toPhaseDTOs(phaseService.findByLevel(level));
    }

    @GetMapping("/findActivityByPhase/{phaseId}")
    public PhaseDTO findActivityByPhase(@PathVariable String phaseId) {
        return phaseService.findAllActivityByPhaseId(phaseId);
    }

    @GetMapping("/findActivityByProjectId/{projectId}")
    public List<PhaseDTO> findActivityByPhase(@PathVariable Long projectId) {
        return phaseService.findAllActivityByProjectId(projectId);
    }

    @GetMapping("/findByParentPhaseId/{parentId}")
    public List<PhaseDTO> findByParentPhaseId(@PathVariable Long parentId) {
        return toPhaseDTOs(phaseService.findByParentPhaseId(parentId));
    }

    @GetMapping("/findByProjectId/{projectId}")
    public List<PhaseDTO> findByProjectId(@PathVariable Long projectId) {
        return toPhaseDTOs(phaseService.findByProjectHead(projectId));
    }

    @GetMapping("/findByProjectIdAndPhaseName/{projectId}/{phaseName}")
    public PhaseDTO findByProjectHeadIdAndPhaseName(@PathVariable Long projectId, @PathVariable String phaseName) {
        return toPhaseDTO(phaseService.findByProjectHeadIdAndPhaseName(projectId, phaseName));
    }

    @GetMapping("/getAll")
    public List<PhaseDTO> findAllPhases() {
        return toPhaseDTOs(phaseService.findAll());
    }
}
