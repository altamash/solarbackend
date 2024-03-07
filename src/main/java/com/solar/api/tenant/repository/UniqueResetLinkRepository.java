package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.user.UniqueResetLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniqueResetLinkRepository extends JpaRepository<UniqueResetLink, Long> {

    UniqueResetLink findByUniqueText(String uniqueText);

    UniqueResetLink findByAdminAccount(Long adminAccount);
}
