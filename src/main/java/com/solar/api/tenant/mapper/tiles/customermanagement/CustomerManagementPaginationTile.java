package com.solar.api.tenant.mapper.tiles.customermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerManagementPaginationTile <T> implements Serializable {
    private  Integer totalPages;
    private Long totalElements;
    private String groupBy;
    private List<CustomerManagementTile> data;




}
