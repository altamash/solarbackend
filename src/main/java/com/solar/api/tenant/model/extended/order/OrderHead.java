package com.solar.api.tenant.model.extended.order;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "order_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long registerId;
    private Date orderDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
