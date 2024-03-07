package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.project.ProjectLevelDoc;
import com.solar.api.tenant.repository.project.ProjectLevelDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectLevelDocServiceImpl implements ProjectLevelDocService{

    @Autowired
    ProjectLevelDocRepository projectLevelDocRepository;

    @Override
    public ProjectLevelDoc save(ProjectLevelDoc projectLevelDoc) {
        return projectLevelDocRepository.save(projectLevelDoc);
    }

    @Override
    public ProjectLevelDoc findById(Long id) {
        return projectLevelDocRepository.findById(id).orElseThrow(() -> new NotFoundException(ProjectLevelDoc.class, id));
    }

    @Override
    public List<ProjectLevelDoc> findByLevelAndLevelId(String level, String levelId) {
        return projectLevelDocRepository.findByLevelAndLevelId(level, levelId);
    }

    @Override
    public ProjectLevelDoc findByDocId(Long docuLibraryId) {
        return projectLevelDocRepository.findByDocId(docuLibraryId);
    }


    @Override
    public void delete(Long id) {
        projectLevelDocRepository.deleteById(id);
    }
}
