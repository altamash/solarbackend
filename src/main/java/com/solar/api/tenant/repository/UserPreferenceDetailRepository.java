package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.UserPreferenceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceDetailRepository extends JpaRepository<UserPreferenceDetail, Long> {
}
