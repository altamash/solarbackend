package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.model.contract.UserPasswordHead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordHeadRepository extends JpaRepository<UserPasswordHead, Long> {
}
