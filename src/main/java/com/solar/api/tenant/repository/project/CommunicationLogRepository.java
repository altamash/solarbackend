package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.CommunicationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunicationLogRepository extends JpaRepository<CommunicationLog, Long> {

    List<CommunicationLog> findAllByLevel(String level);

    CommunicationLog findByLevelAndLevelId(String projectId, Long levelId);

}
