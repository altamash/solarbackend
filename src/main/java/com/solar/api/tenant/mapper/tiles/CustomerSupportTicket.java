package com.solar.api.tenant.mapper.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSupportTicket {

    private Long ticketNumber;
    private String subject;
    private String category;
    private String priority;
    private String status;
}
