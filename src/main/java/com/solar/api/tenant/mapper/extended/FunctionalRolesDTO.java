package com.solar.api.tenant.mapper.extended;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionalRolesDTO {
    private Long id;
    private String name;
    private Long defaultPrivilegeLevel;
    private String defaultHierarchyId; //keeping sequence no 001.001
    private String defaultHierarchySeqCode; //keeping sequence no 001.001
    private String category;
    private String subCategory;
    private String hierarchyType; //project,partner,
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
