package com.solar.api.tenant.service.process.permission.navigation;

import com.solar.api.saas.model.permission.navigation.InMemoryNavigationElement;
import com.solar.api.saas.model.permission.navigation.NavigationElement;

import java.util.List;

public interface UserNavigation {

    InMemoryNavigationElement addInMemoryNavigationElements();

    List<NavigationElement> getInMemoryNavigationElements();

    NavigationElement getInMemoryNavigationElementById(Long id);

    List<NavigationElement> getUserNavigationElements();

    List<NavigationElement> getUserNavigationElements(String userName);
}
