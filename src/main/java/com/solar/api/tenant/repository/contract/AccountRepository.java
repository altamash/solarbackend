package com.solar.api.tenant.repository.contract;

import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUser(User user);


}
