package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.mapper.extended.assetHead.AssetHeadDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectInventoryDTO {

    private Long id;
    private Long projectId;
    private Long assetId;
    private String modelNumber;
    private Long listId;
    private Long quantity;
    private String status;
    private Long taskId;
    private String location;
    private Date installDate;
    private Date activationDate;
    private String inOperation;
    private Date expirationDate;
    private String relatedProject;
    private String currency;
    private AssetHeadDTO assetHeadDTO;
    private List<ProjectInventorySerialDTO> projectInventorySerialDTOs;
    //private List<String> assetSerialNumbers;
    private String assetSerialNumber;
    private Long serialNumberCount;
}
