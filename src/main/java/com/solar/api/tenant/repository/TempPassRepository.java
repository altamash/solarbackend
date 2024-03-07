package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.user.TempPass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempPassRepository extends JpaRepository<TempPass, Long> {
}
