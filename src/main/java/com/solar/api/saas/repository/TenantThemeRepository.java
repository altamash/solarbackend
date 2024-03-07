package com.solar.api.saas.repository;

import com.solar.api.saas.model.preferences.TenantTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantThemeRepository extends JpaRepository<TenantTheme, Long> {
}
