package com.solar.api.tenant.mapper.extended.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.employee.EmployeeDetailDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDTO {
    private Long id;
    private String name;
    private String type;
    private String size;
    private String file;
    private String status;
    private LocalDateTime updatedAt;
    private String format;
    private EmployeeDetailDTO employeeDetailDTO;

    public DocumentDTO(Long id, String name, String type, String size, String file, String status, String employeeName, String employeeImage,
                       Long entityId, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.file = file;
        this.updatedAt = updatedAt;
        this.status =status;
        this.employeeDetailDTO = EmployeeDetailDTO.builder().entityId(entityId).employeeName(employeeName).profileUrl(employeeImage).build();
    }
}
