package com.solar.api.tenant.mapper.extended.register;

import com.solar.api.tenant.model.extended.register.RegisterDetail;
import com.solar.api.tenant.model.extended.register.RegisterHead;
import com.solar.api.tenant.model.extended.register.RegisterHierarchy;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterHierarchies {

    private String code;
    private RegisterHead registerHead;
    private List<RegisterDetail> registerDetail;
    private List<RegisterHierarchy> registerHierarchies;
}
