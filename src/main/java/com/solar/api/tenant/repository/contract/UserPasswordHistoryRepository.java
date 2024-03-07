package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.model.contract.UserPasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordHistoryRepository extends JpaRepository<UserPasswordHistory, Long> {
}
