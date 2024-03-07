package com.solar.api.tenant.mapper.tiles.projectmanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.employee.EmployeeDetailDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectManagementTile {

    private String projectName;
    private EmployeeDetailDTO employeeDetailDTO;
    private String status;
    private String template;
    private String type;
    private Integer activities;
    private Integer tasks;
    private Integer resources;
    private String portfolio;
    private String marketplace;
    private Boolean isLeaf;
    private Long employeeEntityId;
    private String employeeAcctId;
    private String groupBy;
    private String projectId;
    private String availableCredit;
    private String projectSize;
    private String projectUom;
    private String utilityCompany;
    private String projectLiveDate;
    private String projectDescription;
    private String imageUrl;
    private String federalItcAvailableCredit; // federal ITC measure
    private  String availableCreditForSale;

    private  Integer projectOwnerAcctId;


}
