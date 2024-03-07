package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.project.ProjectLevelDoc;

import java.util.List;

public interface ProjectLevelDocService {

    ProjectLevelDoc save(ProjectLevelDoc projectLevelDoc);

    ProjectLevelDoc findById(Long id);

    List<ProjectLevelDoc> findByLevelAndLevelId(String level, String levelId);

    ProjectLevelDoc findByDocId(Long docuLibraryId);

    void delete(Long id);
}
