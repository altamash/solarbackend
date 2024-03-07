package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.FunctionalRoles;

import java.util.List;

public interface FunctionalRolesService {

    FunctionalRoles saveOrUpdate(FunctionalRoles functionalRoles);
    FunctionalRoles findFunctionalRolesById(Long id);
    List<FunctionalRoles> findAllFunctionalRoles();
}
