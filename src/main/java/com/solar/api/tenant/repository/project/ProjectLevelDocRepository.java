package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.ProjectLevelDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectLevelDocRepository extends JpaRepository<ProjectLevelDoc, Long> {

    ProjectLevelDoc findByDocId(Long docuLibraryId);

    List<ProjectLevelDoc> findByLevelAndLevelId(String level, String levelId);
}
