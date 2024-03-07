package com.solar.api.tenant.mapper.extended.register;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InMemoryRegisterHierarchyDTO {

    private List<RegisterHierarchyDTO> registerHierarchies;
}
