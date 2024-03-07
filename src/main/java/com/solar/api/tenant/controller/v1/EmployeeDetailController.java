package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.extended.project.EmployeeDetailDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeDetailMapper;
import com.solar.api.tenant.service.extended.project.EmployeeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EmployeeDetailController")
@RequestMapping(value = "/employeeDetail")
public class EmployeeDetailController {

    @Autowired
    private EmployeeDetailService employeeDetailService;

    @PostMapping("/save")
    public EmployeeDetailDTO addEmployeeManagement(@RequestBody EmployeeDetailDTO employeeDetailDTO) {
        return EmployeeDetailMapper.toEmployeeDetailDTO(employeeDetailService.save(EmployeeDetailMapper.toEmployeeDetail(employeeDetailDTO)));
    }

    @GetMapping("/validateEmail/{emailId}")
    public boolean validateEmail(@PathVariable String emailId) {
        return employeeDetailService.isValidateEmail(emailId);
    }

    @GetMapping("/loadFilterEmployeeData")
    public BaseResponse loadFilterEmployeeData(@RequestParam(value = "exportDTO", required = false) String exportDTO) {
        return employeeDetailService.loadFilterEmployeeData(exportDTO);
    }

        @GetMapping("/getEmployeeReadingExportData")
    public BaseResponse getEmployeeReadingExportData(@RequestParam("employeeIds") List<Long> employeeIds,
                                                     @RequestParam("employementType") List<String> employementType,
                                                     @RequestParam("reportingManager") List<String> reportingManager,
                                                     @RequestParam("startDate") String startDate,
                                                     @RequestParam("endDate") String endDate,
                                                    @RequestParam("pageNumber") Integer pageNumber,
                                                    @RequestParam("pageSize") Integer pageSize) {
        return employeeDetailService.getEmployeeReadingExportData(employeeIds, employementType, reportingManager ,startDate,endDate, pageNumber, pageSize);
    }

}
