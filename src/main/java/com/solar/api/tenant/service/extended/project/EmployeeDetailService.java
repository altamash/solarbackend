package com.solar.api.tenant.service.extended.project;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;

import java.util.List;

public interface EmployeeDetailService {

    EmployeeDetail save(EmployeeDetail employeeDetail);

    boolean isValidateEmail(String emailId);

    EmployeeDetail findById(Long id);

    List<EmployeeDetail> findAllByEntityIdIn(List<Long> entityIds);

    EmployeeDetail findByEntityId(Long id);

    BaseResponse loadFilterEmployeeData(String exportDTO);

    BaseResponse getEmployeeReadingExportData(List<Long> employeeIds, List<String> employementType, List<String> reportingManager, String startDate,String endDate, Integer pageNumber, Integer pageSize);

    EmployeeDetail findByEntity(Entity entity);
}
