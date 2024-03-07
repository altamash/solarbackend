package com.solar.api.tenant.service.process.permission.navigation;

import com.solar.api.saas.model.permission.navigation.InMemoryNavigationElement;
import com.solar.api.saas.model.permission.navigation.NavigationElement;
import com.solar.api.saas.repository.permission.navigation.NavigationComponentMapRepository;
import com.solar.api.saas.repository.permission.navigation.NavigationElementRepository;
import com.solar.api.tenant.model.permission.navigation.NavigationUserMap;
import com.solar.api.tenant.repository.permission.navigation.NavigationUserMapRepository;
import com.solar.api.tenant.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserNavigationImpl implements UserNavigation {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private NavigationElementRepository repository;
    @Autowired
    private InMemoryNavigationElement inMemoryNavigationElement;
    @Autowired
    private NavigationUserMapRepository navigationUserMapRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private NavigationComponentMapRepository navigationComponentMapRepository;

    @Override
    public InMemoryNavigationElement addInMemoryNavigationElements() {
        List<NavigationElement> navigationElements = repository.findByParent(null);
        for (NavigationElement navigationElement : navigationElements) {
            try {
                setNavigationElements(navigationElement);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        inMemoryNavigationElement.getNavigationElements().clear();
        inMemoryNavigationElement.getNavigationElements().addAll(navigationElements);
        return inMemoryNavigationElement;
    }

    private void setNavigationElements(NavigationElement navigationElement) {
        List<NavigationElement> subElements = repository.findByParent(navigationElement);
        navigationElement.setSubElements(subElements);
        for (NavigationElement n : subElements) {
            subElements = repository.findByParent(n);
            if (!subElements.isEmpty()) {
                setNavigationElements(n);
            }
        }
    }

    /** All navigation elements **/
    @Override
    public List<NavigationElement> getInMemoryNavigationElements() {
        if (inMemoryNavigationElement.getNavigationElements().isEmpty()) {
            addInMemoryNavigationElements();
        }
        return inMemoryNavigationElement.getNavigationElements();
    }

    @Override
    public NavigationElement getInMemoryNavigationElementById(Long id) {
        if (inMemoryNavigationElement.getNavigationElements().isEmpty()) {
            addInMemoryNavigationElements();
        }
        return getInMemoryNavigationElementsById(id, inMemoryNavigationElement.getNavigationElements());
    }

    private NavigationElement getInMemoryNavigationElementsById(Long id, List<NavigationElement> navigationElements) {
        NavigationElement element = null;
        if (navigationElements != null) {
            for (NavigationElement n : navigationElements) {
                if (n.getId().longValue() == id) {
                    element = n;
                }
                if (element == null) {
                    element = getInMemoryNavigationElementsById(id, n.getSubElements());
                } else {
                    break;
                }
            }
        }
        return element;
    }

    /** User navigation elements **/
    @Override
    public List<NavigationElement> getUserNavigationElements() {
        return getUserNavigationElements(userService.getLoggedInUser().getUserName());
        // TODO: set favourites
    }

    @Override
    public List<NavigationElement> getUserNavigationElements(String userName) {
        List<NavigationUserMap> navigationUserMaps = navigationUserMapRepository
                .findByUser(userService.findByUserName(userName));
        List<Long> navMapIds = navigationUserMaps.stream().flatMap(m -> Stream.of(m.getNavMapId())).collect(Collectors.toList());
        return addUserNavigationElements(getUserNavIds(navMapIds));
    }

    private Set<Long> getUserNavIds(List<Long> navMapIds) {
        List<NavigationElement> navigationElements = navigationComponentMapRepository.findByIdIn(navMapIds)
                .stream().map(m -> m.getNavigationElement()).collect(Collectors.toList());
        Set<Long> ids = new HashSet<>();
        navigationElements.forEach(ne -> ids.addAll(getParentHierarchyIds(ne)));
        return ids;
    }

    private Set<Long> getParentHierarchyIds(NavigationElement navigationElement) {
        Set<Long> ids = new HashSet<>();
        ids.add(navigationElement.getId());
        while(navigationElement.getParent() != null) {
            navigationElement = navigationElement.getParent();
            ids.add(navigationElement.getId());
        }
        return ids;
    }

    private List<NavigationElement> addUserNavigationElements(Set<Long> availableNavIds) {
        List<NavigationElement> navigationElements = repository.findByParentAndIdIn(null, availableNavIds);
        for (NavigationElement navigationElement : navigationElements) {
            try {
                setUserNavigationElements(navigationElement, availableNavIds);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return navigationElements;
    }

    private void setUserNavigationElements(NavigationElement navigationElement, Set<Long> availableNavIds) {
        List<NavigationElement> subElements = repository.findByParent(navigationElement);
        subElements.removeIf(e -> !availableNavIds.contains(e.getId()));
        navigationElement.setSubElements(subElements);
        for (NavigationElement n : subElements) {
            subElements = repository.findByParent(n);
            subElements.removeIf(e -> !availableNavIds.contains(e.getId()));
            if (!subElements.isEmpty()) {
                setUserNavigationElements(n, availableNavIds);
            }
        }
    }

    /*private List<NavigationElement> getAllAllowedElements(List<NavigationElement> navigationElements) {
        Set<NavigationElement> set = new HashSet();
        navigationElements.forEach(navigationElement -> {
            NavigationElement navElement = navigationElement;
            set.add(navElement);
            while (navElement.getParent() != null) {
                set.add(navElement);
//                navElement = repository.findById(navElement.getParent()).get();
                navElement = navElement.getParent();
            }
        });
        return new ArrayList<>(set);
    }*/

}
