package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.tenant.model.extended.project.CommunicationLog;

import java.util.List;

public interface CommunicationLogsService {

    CommunicationLog addOrUpdate(CommunicationLog communicationLog) throws AlreadyExistsException;

    CommunicationLog findById(Long id);

    List<CommunicationLog> findAllByLevel(String level);

    CommunicationLog findByLevelAndLevelId(String projectId, Long levelId);

    List<CommunicationLog> findAll();

    void delete(Long id);

    void deleteAll();
}
