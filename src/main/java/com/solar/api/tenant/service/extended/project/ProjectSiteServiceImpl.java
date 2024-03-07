package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.physicalLocation.Site;
import com.solar.api.tenant.model.extended.project.ProjectSite;
import com.solar.api.tenant.repository.SiteRepository;
import com.solar.api.tenant.repository.project.ProjectSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.physicalLocation.SiteMapper.toSiteDTO;

@Service
public class ProjectSiteServiceImpl implements ProjectSiteService {

    @Autowired
    private ProjectSiteRepository projectSiteRepository;
    @Autowired
    private SiteRepository siteRepository;

    @Override
    public ProjectSite save(ProjectSite projectSite) {
        return projectSiteRepository.save(projectSite);
    }

    @Override
    public List<ProjectSite> findAllByProjectId(Long projectId) {
        List<ProjectSite> projectSites = projectSiteRepository.findAllByProjectId(projectId);
        projectSites.forEach(proSite -> {
            if (proSite.getSiteId()!=null) {
               Site site = siteRepository.findById(proSite.getSiteId()).get();
                proSite.setSite(toSiteDTO(site));
            }
        });
        return projectSites;
    }

    @Override
    public List<ProjectSite> findAll() {
        List<ProjectSite> projectSites = projectSiteRepository.findAll();
        projectSites.forEach(proSite-> {
            if (proSite.getSiteId()!=null) {
                Site site = siteRepository.findById(proSite.getSiteId()).get();
                proSite.setSite(toSiteDTO(site));
            }
        });
        return projectSites;
    }

    @Override
    public ProjectSite update(ProjectSite projectSite) {
        return projectSiteRepository.save(projectSite);
    }

    @Override
    public void deleteByProjectIdAndSiteId(Long projectId, Long siteId) {
        projectSiteRepository.deleteByProjectIdAndSiteId(projectId, siteId);
    }
}
