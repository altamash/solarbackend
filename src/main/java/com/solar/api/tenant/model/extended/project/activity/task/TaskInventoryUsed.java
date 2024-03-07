package com.solar.api.tenant.model.extended.project.activity.task;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "task_inventory_used")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInventoryUsed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
