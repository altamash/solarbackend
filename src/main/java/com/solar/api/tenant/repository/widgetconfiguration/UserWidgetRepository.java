package com.solar.api.tenant.repository.widgetconfiguration;


import com.solar.api.tenant.model.widgetconfiguration.UserWidget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWidgetRepository extends JpaRepository<UserWidget, Long> {
    List<UserWidget> findByAcctId(Long acctId);

}
