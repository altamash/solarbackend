package com.solar.api.tenant.service.extended.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerTile;
import com.solar.api.tenant.mapper.tiles.dataexport.employeedetail.DataExportEmployeePaginationTile;
import com.solar.api.tenant.mapper.tiles.dataexport.employeedetail.DataExportEmployeeTile;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO;
import com.solar.api.tenant.model.dataexport.employee.EmployeeExportDTO;
import com.solar.api.tenant.model.dataexport.payment.PaymentExportData;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.repository.project.EmployeeDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeDetailServiceImpl implements EmployeeDetailService {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;

    @Override
    public EmployeeDetail save(EmployeeDetail employeeDetail) {
        /*if (isValidateEmail(employeeDetail.getPersonalEmail())) {
            throw new AlreadyExistsException(EmployeeDetail.class, "email", employeeDetail.getPersonalEmail());
        }*/
        return employeeDetailRepository.save(employeeDetail);
    }

    @Override
    public boolean isValidateEmail(String emailId) {
        if (employeeDetailRepository.findByPersonalEmail(emailId) != null) {
            return true;
        }
        return false;
    }

    @Override
    public EmployeeDetail findById(Long id) {
        return employeeDetailRepository.findById(id).orElseThrow(() -> new NotFoundException(EmployeeDetail.class, id));
    }

    @Override
    public List<EmployeeDetail> findAllByEntityIdIn(List<Long> entityIds) {
        return employeeDetailRepository.findAllByEntityIdIn(entityIds);
    }

    @Override
    public EmployeeDetail findByEntityId(Long id) {
        return employeeDetailRepository.findByEntityId(id);
    }

    @Override
    public BaseResponse loadFilterEmployeeData(String exportDTO) {
        EmployeeExportDTO filterDTO = new EmployeeExportDTO();

        try {
            if (exportDTO != null) {
                filterDTO = new ObjectMapper().readValue(exportDTO, EmployeeExportDTO.class);
                if (filterDTO.getEmployeementType() != null && !filterDTO.getEmployeementType().isEmpty()) {
                    filterDTO.setEmployee(employeeDetailRepository.findAllUniqueEmployeeById(filterDTO.getEmployeementType()));
                    List<EmployeeDataDTO> allUniqueReportingManager = employeeDetailRepository.findAllUniqueReportingManager(filterDTO.getEmployeementType());
                    if(allUniqueReportingManager == null){
                        allUniqueReportingManager = new ArrayList<>();
                    }
                    allUniqueReportingManager.add(EmployeeDataDTO.builder().name("NA").build());
                    filterDTO.setReportingManager(allUniqueReportingManager);
                }

            } else {
                filterDTO.setEmployeementType(employeeDetailRepository.findAllUniqueEmployeeType());
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(filterDTO).build();
    }

    @Override
    public BaseResponse getEmployeeReadingExportData(List<Long> employeeIds, List<String> employementType, List<String> reportingManager, String startDate, String endDate, Integer pageNumber, Integer pageSize) {
        DataExportEmployeePaginationTile result = new DataExportEmployeePaginationTile();
        try {
            Page<DataExportEmployeeTile> page = null;
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            page = employeeDetailRepository.employeeDataExport(employeeIds, employementType, reportingManager, startDate, endDate, pageRequest);
            result.setDataExportEmployeeTileList(page.getContent());
            result.setTotalPages(page.getTotalPages());
            result.setTotalElements(page.getTotalElements());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public EmployeeDetail findByEntity(Entity entity) {
        return employeeDetailRepository.findByEntityId(entity.getId());
    }
}
