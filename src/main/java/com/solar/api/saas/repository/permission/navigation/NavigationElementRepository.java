package com.solar.api.saas.repository.permission.navigation;

import com.solar.api.saas.model.permission.navigation.NavigationElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface NavigationElementRepository extends JpaRepository<NavigationElement, Long> {

    List<NavigationElement> findByParent(NavigationElement parent);
    List<NavigationElement> findByParentAndIdIn(Long id, Set<Long> ids);

//    List<NavigationElement> findByParentIn(List<Long> ids);
}
