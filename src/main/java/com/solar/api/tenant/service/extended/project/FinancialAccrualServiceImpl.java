package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.extended.project.FinancialAccrualMapper;
import com.solar.api.tenant.model.extended.project.FinancialAccrual;
import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;
import com.solar.api.tenant.repository.project.FinancialAccrualRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FinancialAccrualServiceImpl implements FinancialAccrualService{

    @Autowired
    ResourceAttendanceLogService resourceAttendanceLogService;

    @Autowired
    FinancialAccrualRepository financialAccrualRepository;

    @Override
    public FinancialAccrual saveOrUpdate(FinancialAccrual financialAccrual) {
        if (financialAccrual.getId() != null) {
            FinancialAccrual financialAccrualData =
                    financialAccrualRepository.findById(financialAccrual.getId()).orElseThrow(() -> new NotFoundException(FinancialAccrual.class, financialAccrual.getId()));
            FinancialAccrual financialAccrualUpdate = FinancialAccrualMapper.toUpdatedFinancialAccrual(financialAccrualData, financialAccrual);
            return financialAccrualRepository.save(financialAccrualUpdate);
        }
        return financialAccrualRepository.save(financialAccrual);
    }

    @Override
    public void analyze(Long compKey, Long employeeId, Long taskId) {
        List<ResourceAttendanceLog> resourceAttendanceLogs = resourceAttendanceLogService.findByEmployeeIdAndTaskId(employeeId, taskId);
        double hours = 0.0;
        for(ResourceAttendanceLog resourceAttendanceLog : resourceAttendanceLogs){

            hours = hours + Double.valueOf(resourceAttendanceLog.getHours());
        }

        FinancialAccrual financialAccrual = FinancialAccrual.builder()
                .category("project")
                .refId(resourceAttendanceLogs.get(0).getProjectId())
                .subRefId(resourceAttendanceLogs.get(0).getTaskId())
                .accrualCategory("resource")
                .accruedAmount(hours)
                .accrualCategoryId(resourceAttendanceLogs.get(0).getEmployeeId())
                .accrualDatetime(String.valueOf(new Date()))
                .orgId(compKey)
                .build();
        saveOrUpdate(financialAccrual);

    }
}
