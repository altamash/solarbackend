package com.solar.api.tenant.model.extended.register;

import com.solar.api.saas.configuration.DBContextHolder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@ApplicationScope
@Component
public class InMemoryRegisterHierarchy {

    private Map<String, ArrayList<RegisterHierarchy>> registerHierarchiesMap = new HashMap<>();

    public void addHierarchies(List<RegisterHierarchy> registerHierarchies) {
        if (registerHierarchiesMap.get(DBContextHolder.getTenantName()) == null) {
            registerHierarchiesMap.put(DBContextHolder.getTenantName(), (ArrayList<RegisterHierarchy>) registerHierarchies);
        } else {
            registerHierarchiesMap.get(DBContextHolder.getTenantName()).addAll(registerHierarchies);
        }
    }

    public void clearHierarchies() {
        if (registerHierarchiesMap.get(DBContextHolder.getTenantName()) != null) {
            registerHierarchiesMap.put(DBContextHolder.getTenantName(), new ArrayList());
        }
    }

    public List<RegisterHierarchy> getRegisterHierarchies() {
        if (registerHierarchiesMap.get(DBContextHolder.getTenantName()) == null) {
            return Collections.emptyList();
        } else {
            return registerHierarchiesMap.get(DBContextHolder.getTenantName());
        }
    }
}
