package com.solar.api.tenant.mapper.tiles.customersupportmanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSupportFiltersTile {
    private List<String> ticketType;
    private List<String> priority;
    private List<String> category;
    private List<String> subCategory;
    private List<String> status;

}
