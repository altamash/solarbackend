package com.solar.api.tenant.mapper.ca;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaUserTemplatePaginationDTO {
    private Integer totalPages;
    private Long totalElements;
    private List<CaUserTemplateDTO> caUserTemplateDTOList;
}
