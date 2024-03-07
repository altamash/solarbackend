package com.solar.api.saas.service.process.permission;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.permission.PermissionMapper;
import com.solar.api.saas.model.permission.Permission;
import com.solar.api.saas.model.permission.component.ComponentLibrary;
import com.solar.api.saas.model.permission.component.ECompReference;
import com.solar.api.saas.repository.permission.ComponentLibraryRepository;
import com.solar.api.saas.repository.permission.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository repository;
    @Autowired
    private ComponentLibraryRepository componentLibraryRepository;

    @Override
    public Permission saveOrUpdate(Permission permission, Long componentLibraryId) {
        if (componentLibraryId != null) {
            componentLibraryRepository.findById(componentLibraryId).ifPresent(c -> permission.setComponentLibrary(c));
        }
        ComponentLibrary component = permission.getComponentLibrary();
        if (component != null) {
            component = componentLibraryRepository.findById(componentLibraryId)
                    .orElseThrow(() -> new NotFoundException(ComponentLibrary.class, componentLibraryId));
        }
        permission.setComponentLibrary(component);
        if (permission.getId() != null) {
            Permission permissionDb = findById(permission.getId());
            permissionDb = PermissionMapper.toUpdatedPermission(permissionDb, permission);
            return repository.save(permissionDb);
        }
        return repository.save(permission);
    }

    @Override
    public List<Permission> saveAll(List<Permission> permissions) {
        return repository.saveAll(permissions);
    }

    @Override
    public Permission findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Permission.class, id));
    }

    @Override
    public  List<Permission> findByIdIn(Set<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public Permission findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<Permission> findByNameIn(List<String> names) {
        return repository.findByNameIn(names);
    }

    @Override
    public List<Permission> findAll() {
        return repository.findAll();
    }

    @Override
    public Long getNextIdentifier(String compReference) {
        Long lastIdentifier = null;
        if (ECompReference.UI.getType().equals(compReference)) {
            lastIdentifier = repository.getLastIdentifier(50000l);
        } else if (ECompReference.API.getType().equals(compReference)) {
            lastIdentifier = repository.getLastIdentifier(100000l);
        }
        return lastIdentifier == null ? 1 : lastIdentifier;
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
