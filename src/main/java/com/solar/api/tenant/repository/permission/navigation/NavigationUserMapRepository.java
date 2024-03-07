package com.solar.api.tenant.repository.permission.navigation;

import com.solar.api.tenant.model.permission.navigation.NavigationUserMap;
import com.solar.api.tenant.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NavigationUserMapRepository extends JpaRepository<NavigationUserMap, Long> {

    List<NavigationUserMap> findByUser(User user);
    void deleteByUser(User user);
    void deleteByNavMapIdIn(List<Long> navMapIds);
}
