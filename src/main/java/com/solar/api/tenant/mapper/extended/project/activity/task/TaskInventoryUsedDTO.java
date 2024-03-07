package com.solar.api.tenant.mapper.extended.project.activity.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInventoryUsedDTO {

    private Long id;
    private Long asset_id;
    private Long serial_number;
    private Long listId;
    private Long quantity;
    private Long reservationBucket;
    private Long taskId;
    private String utilizationStatus;
    private Long locationId;
    private Date startDate;
    private Date expirationDate;
}
