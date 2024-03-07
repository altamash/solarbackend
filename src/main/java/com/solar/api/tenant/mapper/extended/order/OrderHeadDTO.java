package com.solar.api.tenant.mapper.extended.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderHeadDTO {

    private Long id;
    private Long registerId;
    private Date orderDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
