package com.solar.api.tenant.mapper.extended.register;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterHierarchyDTO {

    private Long id;
    private Integer level;
    private String name;
    private String code;
    private Integer sequenceNo;
    private String alias;
    private String description;
    private String category;
    private String parent;
    private Long parentId;
    private Boolean registered;
    private List<RegisterHierarchyDTO> subHierarchies;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
