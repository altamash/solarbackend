package com.solar.api.tenant.model.extended.order;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String measure;
    private String value;
    private Date updateDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
