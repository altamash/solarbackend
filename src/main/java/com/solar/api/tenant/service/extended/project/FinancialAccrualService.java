package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.project.FinancialAccrual;

public interface FinancialAccrualService {

    FinancialAccrual saveOrUpdate(FinancialAccrual financialAccrual);
    void analyze(Long compKey, Long employeeId, Long taskId);

}
