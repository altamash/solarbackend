package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.project.EngagementRoleMapper;
import com.solar.api.tenant.model.AnalyticalCalculation;
import com.solar.api.tenant.model.extended.project.EngagementRole;
import com.solar.api.tenant.repository.project.EngagementRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EngagementRoleServiceImpl implements EngagementRoleService {

    @Autowired
    EngagementRoleRepository engagementRoleRepository;

    @Override
    public EngagementRole saveOrUpdate(EngagementRole engagementRole) {
        if (engagementRole.getId() != null) {
            EngagementRole engagementRoleData =
                    engagementRoleRepository.findById(engagementRole.getId()).orElseThrow(() -> new NotFoundException(EngagementRole.class, engagementRole.getId()));
            if (engagementRoleData == null) {
                throw new NotFoundException(AnalyticalCalculation.class, engagementRole.getId());
            }
            EngagementRole engagementRoleUpdate = EngagementRoleMapper.toUpdatedEngagementRole(engagementRoleData, engagementRole);
            return engagementRoleRepository.save(engagementRoleUpdate);
        }
        return engagementRoleRepository.save(engagementRole);
    }

    @Override
    public List<EngagementRole> findByGlReferenceCode(Long glReferenceCode) {
        return engagementRoleRepository.findByGlReferenceCode(glReferenceCode);
    }

    @Override
    public EngagementRole findById(Long id) {
        Optional<EngagementRole> engagementRole = engagementRoleRepository.findById(id);
        if (!engagementRole.isPresent()) {
            throw new NotFoundException(EngagementRole.class, id);
        }

        EngagementRole engagementRole1 = engagementRole.get();
        return engagementRole1;
    }

    @Override
    public List<EngagementRole> findByGlReferenceCodeAndRoleName(Long glReferenceCode, String roleName) {
        return engagementRoleRepository.findByGlReferenceCodeAndRoleName(glReferenceCode, roleName);
    }

    @Override
    public List<EngagementRole> findByExternalRoleIdAndRoleName(String externalRoleId, String phaseName) {
        return engagementRoleRepository.findByExternalRoleIdAndRoleName(externalRoleId, phaseName);
    }

    @Override
    public EngagementRole findByExternalRoleId(String externalRoleId) {
        return engagementRoleRepository.findByExternalRoleId(externalRoleId);
    }

    @Override
    public List<EngagementRole> findAll() {
        return engagementRoleRepository.findAll();
    }
}
