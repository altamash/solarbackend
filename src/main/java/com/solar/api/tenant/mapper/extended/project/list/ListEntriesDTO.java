package com.solar.api.tenant.mapper.extended.project.list;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListEntriesDTO {

    private Long id;
    private Long listId;
    private String title;
    private String description;
    private Long sequence;
    private Boolean mandatory;
    private Boolean enabled;
    private String type;
    private Long docuId;
    private String assignedTo;

}
