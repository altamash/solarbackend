package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.project.CommunicationLog;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.CalculationGroup;
import com.solar.api.tenant.repository.project.CommunicationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.solar.api.tenant.mapper.extended.project.ProjectMapper.toUpdatedCommunicationLog;

@Service
public class CommunicationLogsServiceImpl implements CommunicationLogsService {
    
    @Autowired
    CommunicationLogRepository communicationLogRepository;
    
    @Override
    public CommunicationLog addOrUpdate(CommunicationLog communicationLog) throws AlreadyExistsException {
        if (communicationLog.getId() != null) {
            CommunicationLog communicationLogData = findById(communicationLog.getId());
            if (communicationLogData == null) {
                throw new NotFoundException(CommunicationLog.class, communicationLog.getId());
            }
            return communicationLogRepository.save(toUpdatedCommunicationLog(communicationLogData,
                    communicationLog));
        }
        return communicationLogRepository.save(communicationLog);
    }

    @Override
    public CommunicationLog findById(Long id) {
        return communicationLogRepository.findById(id).orElseThrow(() -> new NotFoundException(CalculationGroup.class, id));

    }

    @Override
    public List<CommunicationLog> findAllByLevel(String level) {
        return communicationLogRepository.findAllByLevel(level);
    }

    @Override
    public CommunicationLog findByLevelAndLevelId(String projectId, Long levelId) {
        return communicationLogRepository.findByLevelAndLevelId(projectId, levelId);
    }

    @Override
    public List<CommunicationLog> findAll() {
        return communicationLogRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        communicationLogRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        communicationLogRepository.deleteAll();
    }
}
