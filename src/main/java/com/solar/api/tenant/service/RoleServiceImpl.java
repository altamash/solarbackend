package com.solar.api.tenant.service;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.AllocHead;
import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.repository.RoleRepository;
import com.solar.api.tenant.repository.UserTypeRepository;
import com.solar.api.tenant.repository.permission.AvailablePermissionSetRepository;
import com.solar.api.tenant.service.process.permission.PermissionGroupService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@Transactional("tenantTransactionManager")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository repository;
    @Autowired
    private PermissionGroupService permissionGroupService;
    @Autowired
    private AvailablePermissionSetRepository availablePermissionSetRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;

    @Override
    public Role saveOrUpdate(Role role) {
        Optional<Role> roleDb = repository.findByName(role.getName());
        if ((role.getId() == null && roleDb.isPresent()) || (roleDb.isPresent() && roleDb.get().getId() != role.getId())) {
            throw new AlreadyExistsException(Role.class, "name", role.getName());
        }
        UserType userLevel = userTypeRepository.findByName(EUserType.get(role.getUserLevelName())).get();
        role.setUserLevel(userLevel);
        role.setUserLevelName(userLevel.getName().getName());
        return repository.save(role);
    }

    @Override
    public Role findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(AllocHead.class, id));
    }

    @Override
    public Role findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new NotFoundException(Role.class, "name", name));
    }

    @Override
    public List<Role> findByUserLevel(String userLevel) {
        return repository.findByUserLevel(userTypeRepository.findByName(EUserType.get(userLevel)).get());
    }

    @Override
    public List<Role> findByUserLevel(String userLevel, boolean getRemainingPermissionGroups) {
        if (getRemainingPermissionGroups) {
            List<Role> permissionGroups = new ArrayList<>();
            List<PermissionGroup> allAvailablePermissionGroup = permissionGroupService.findByUserLevel(userLevel);
            findByUserLevel(userLevel).forEach(r -> {
                r.setRemainingPermissionGroups(new HashSet<>(CollectionUtils.subtract(allAvailablePermissionGroup, r.getPermissionGroups())));
                permissionGroups.add(r);
            });
            return permissionGroups;
        }
        return findByUserLevel(userLevel);
    }

    @Override
    public List<Role> findAll(boolean getRemainingPermissionGroups) {
        List<Role> roles = new ArrayList<>();
        if (getRemainingPermissionGroups) {
            Map<UserType, List<PermissionGroup>> allPermissionGroupsMap = new HashMap<>();
            userTypeRepository.findAll().forEach(type -> {
                allPermissionGroupsMap.put(type, permissionGroupService.findByUserLevel(type.getName().getName()));
            });
            repository.findAll().forEach(role -> {
                if (role.getUserLevel() != null) {
                    role.setRemainingPermissionGroups(new HashSet<>(CollectionUtils.subtract(allPermissionGroupsMap.get(role.getUserLevel()),
                            role.getPermissionGroups())));
                    role.setUserLevelName(role.getUserLevel().getName().getName());
                }
                roles.add(role);
            });
            return roles;
        }
        repository.findAll().forEach(role -> {
            role.setUserLevelName(role.getUserLevel() != null ? role.getUserLevel().getName().getName() : null);
            roles.add(role);
        });
        return roles;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(String name) {
        repository.deleteByName(name);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    // Role permissions
    @Override
    public Set<PermissionGroup> addPermissionGroup(Long roleId, Long permissionGroupId) {
        Role role = findById(roleId);
        PermissionGroup permissionGroup = permissionGroupService.findById(permissionGroupId);
        role.addPermissionGroup(permissionGroup);
        role = saveOrUpdate(role);
        return role.getPermissionGroups();
    }

    @Override
    public Set<PermissionGroup> removePermissionGroup(Long roleId, Long permissionGroupId) {
        Role role = findById(roleId);
        PermissionGroup permissionGroup = permissionGroupService.findById(permissionGroupId);
        role.removePermissionGroup(permissionGroup);
        role = saveOrUpdate(role);
        return role.getPermissionGroups();
    }

    @Override
    public Set<AvailablePermissionSet> addPermissionSet(Long roleId, Long availablePermissionSetId) {
        Role role = findById(roleId);
        AvailablePermissionSet availablePermissionSet = availablePermissionSetRepository.findById(availablePermissionSetId)
                .orElseThrow(() -> new NotFoundException(AvailablePermissionSet.class, availablePermissionSetId));
        role.addPermissionSet(availablePermissionSet);
        role = saveOrUpdate(role);
        return role.getPermissionSets();
    }

    @Override
    public Set<AvailablePermissionSet> removePermissionSet(Long roleId, Long availablePermissionSetId) {
        Role role = findById(roleId);
        AvailablePermissionSet availablePermissionSet = availablePermissionSetRepository.findById(availablePermissionSetId)
                .orElseThrow(() -> new NotFoundException(AvailablePermissionSet.class, availablePermissionSetId));
        role.removePermissionSet(availablePermissionSet);
        role = saveOrUpdate(role);
        return role.getPermissionSets();
    }
        public static void main(String[] a) {
        System.out.println((Boolean.TRUE == null && true) || (true && true != true));
    }
}
