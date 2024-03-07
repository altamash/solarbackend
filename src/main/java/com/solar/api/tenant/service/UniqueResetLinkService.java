package com.solar.api.tenant.service;

import com.solar.api.tenant.model.user.UniqueResetLink;

import java.util.List;

public interface UniqueResetLinkService {

    UniqueResetLink save(UniqueResetLink uniqueResetLink);

    UniqueResetLink findByUniqueText(String uniqueText);

    UniqueResetLink findByAdminAccount(Long adminAccount);

    List<UniqueResetLink> findAll();
}
