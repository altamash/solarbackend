package com.solar.api.tenant.model.dataexport.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDataDTO {

    private Long entityId;
    private String name;
    private String image;


    public EmployeeDataDTO(Long entityId, String name, String image) {
        this.entityId = entityId;
        this.name = name;
        this.image = image;
    }

    public EmployeeDataDTO(String name) {
        this.name = name;
    }

    public EmployeeDataDTO(String name,Long entityId) {
        this.entityId = entityId;
        this.name = name;
    }
}
