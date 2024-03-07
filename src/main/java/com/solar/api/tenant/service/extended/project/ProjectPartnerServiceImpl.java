package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.partner.PartnerMapper;
import com.solar.api.tenant.model.extended.partner.PartnerHead;
import com.solar.api.tenant.model.extended.project.ProjectPartner;
import com.solar.api.tenant.repository.PartnerHeadRepository;
import com.solar.api.tenant.repository.project.ProjectPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectPartnerServiceImpl implements ProjectPartnerService{

    @Autowired
    private ProjectPartnerRepository projectPartnerRepository;
    @Autowired
    private PartnerHeadRepository partnerHeadRepository;

    @Override
    public List<ProjectPartner> findAllByProjectId(Long projectId) {
        List<ProjectPartner> projectPartners = projectPartnerRepository.findAllByProjectId(projectId);
        projectPartners.forEach(partner-> {
            if (partner.getPartnerId() != null) {
                PartnerHead partnerHead = partnerHeadRepository.findById(partner.getPartnerId()).get();
                partner.setPartnerHead(PartnerMapper.toPartnerHeadDTO(partnerHead));
            }
        });
        return projectPartners;
    }

    @Override
    public ProjectPartner save(ProjectPartner projectPartner) {
        return projectPartnerRepository.save(projectPartner);
    }

    @Override
    public void deleteById(Long id) {
        ProjectPartner projectPartner = findById(id);
        projectPartnerRepository.delete(projectPartner);
    }

    @Override
    public ProjectPartner findById(Long id) {
        return projectPartnerRepository.findById(id).orElseThrow(() -> new NotFoundException(ProjectPartner.class, id));
    }

}
