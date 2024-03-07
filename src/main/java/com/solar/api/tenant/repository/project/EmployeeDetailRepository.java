package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.mapper.employee.EmployeeDetailDTO;
import com.solar.api.tenant.mapper.tiles.dataexport.employeedetail.DataExportEmployeeTile;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeDetailRepository extends JpaRepository<EmployeeDetail, Long>  {

    EmployeeDetail findByPersonalEmail(String emailId);
    List<EmployeeDetail> findAllByEntityIdIn(List<Long> ids);

    EmployeeDetail findByEntityId(Long id);

//    @Query("")
//    Page<DataExportCustomerTile> customerDataExport(@Params("customerType") List<String> designation,
//                                                    @Params("states") List<String> employementType,
//                                                    @Params("states") List<String> reportingManager,
//                                                    @Params("startDate") String startDate,
//                                                    @Params("endDate") String endDate,
//                                                    Pageable pageable);
//    select * from employee_detail where first_name like '%%' or last_name like '%as%';
    @Query("SELECT new com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO(e.entityId, concat(e.firstName , concat(' ',e.lastName)),ed.uri ) " +
            "FROM EmployeeDetail e " +
            "Left Join EntityDetail ed " +
            "on ed.entity.id = e.entityId " +
            "inner join Entity et " +
            "on et.id = e.entityId " +
            "where e.employmentType in(:employeementType) " +
            "and (et.isDeleted is null or et.isDeleted = false) " +
            "group by e.entityId , e.firstName , e.lastName , ed.uri ")
    List<EmployeeDataDTO> findAllUniqueEmployeeById(@Param("employeementType") List<String> employeementType);

    @Query("SELECT distinct e.employmentType from EmployeeDetail e")
    List<String> findAllUniqueEmployeeType();
    @Query("SELECT new com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO(ed.reportingManager) " +
            "FROM EmployeeDetail ed " +
            "inner join Entity e " +
            "on e.id = ed.entityId " +
            " where ed.employmentType in(:employeementType)  " +
            "group by ed.reportingManager")
    List<EmployeeDataDTO> findAllUniqueReportingManager(@Param("employeementType") List<String> employeementType);

    @Query("select new com.solar.api.tenant.mapper.tiles.dataexport.employeedetail.DataExportEmployeeTile(concat(ed.firstName , concat(' ' ,ed.lastName))  , ed.personalEmail , ed.designation , ed.phoneNumber , " +
            "ed.employmentType , ed.reportingManager , DATE_FORMAT(ed.dateOfJoining, '%Y-%m-%d') )" +
            " from " +
            "EmployeeDetail ed " +
            "inner join Entity e " +
            "on e.id = ed.entityId " +
            "where ed.entityId in (:employeeIds) " +
            "and ed.employmentType in (:employementType)" +
            "and ((ed.reportingManager in (:reportingManager) and 'NA' not in (:reportingManager)) " +
            "or (ed.reportingManager is null and 'NA' in (:reportingManager)))" +
            "and (e.isDeleted is null or e.isDeleted = false) " +
            "and function('STR_TO_DATE', ed.dateOfJoining, '%Y-%m-%d %H:%i:%s') between function('STR_TO_DATE', :startDate, '%b %d, %Y') and function('STR_TO_DATE', :endDate, '%b %d, %Y') ")
    Page<DataExportEmployeeTile> employeeDataExport(@Param("employeeIds") List<Long> employeeIds,
                                                    @Param("employementType") List<String> employementType,
                                                    @Param("reportingManager") List<String> reportingManager,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate,
                                                    Pageable pageable);

//    @Query("SELECT new com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO(e.id, e.entityId, e.firstName, e.lastName) " +
//            "FROM EmployeeDetail e where e.reportingManager LIKE %:text% ")
//    List<EmployeeDataDTO> findManagerById(String text);
//DATE_FORMAT(ed.dateOfJoining, '%Y-%m-%d') = :joiningDate

    @Query("select new com.solar.api.tenant.mapper.employee.EmployeeDetailDTO(ulp.user.acctId,ulp.entity.entityName,ed.uri) " +
            "from UserLevelPrivilege ulp " +
            "left join EntityDetail ed on ed.entity.id = ulp.entity.id " +
            "where ulp.user.acctId in (:acctIds) and ulp.entity.entityType ='Employee' and ulp.entity.isDeleted = false " +
            "and ulp.role is null" )
    List<EmployeeDetailDTO> findAllEmployeeDetailDTOByAcctIds(@Param("acctIds") List<Long> acctIds);
    @Query("select new com.solar.api.tenant.mapper.employee.EmployeeDetailDTO(ulp.user.acctId,ulp.entity.entityName,ed.uri) " +
            "from UserLevelPrivilege ulp " +
            "left join EntityDetail ed on ed.entity.id = ulp.entity.id " +
            "where ulp.user.acctId = :acctId and ulp.entity.entityType ='Employee' and ulp.entity.isDeleted = false " +
            "and ulp.role is null" )
    EmployeeDetailDTO findEmployeeDetailDTOByAcctId(@Param("acctId") Long acctIds);
}
