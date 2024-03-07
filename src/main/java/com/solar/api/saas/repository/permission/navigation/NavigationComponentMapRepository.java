package com.solar.api.saas.repository.permission.navigation;

import com.solar.api.saas.model.permission.component.ComponentLibrary;
import com.solar.api.saas.model.permission.navigation.NavigationComponentMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface NavigationComponentMapRepository extends JpaRepository<NavigationComponentMap, Long> {

    List<NavigationComponentMap> findByIdIn(List<Long> ids);

    @Query("SELECT ncm.id FROM NavigationComponentMap ncm WHERE ncm.componentLibrary in :componentLibraries")
    List<Long> findByComponentLibraryIn(Set<ComponentLibrary> componentLibraries);


}
