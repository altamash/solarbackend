package com.solar.api.tenant.service.process.permission;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.permission.PermissionSet;
import com.solar.api.saas.service.process.permission.PermissionSetService;
import com.solar.api.tenant.mapper.permission.PermissionGroupMapper;
import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.repository.UserTypeRepository;
import com.solar.api.tenant.repository.permission.AvailablePermissionSetRepository;
import com.solar.api.tenant.repository.permission.PermissionGroupRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionGroupServiceImpl implements PermissionGroupService {

    @Autowired
    private PermissionGroupRepository repository;
    @Autowired
    private PermissionSetService permissionSetService;
    @Autowired
    private AvailablePermissionSetRepository availablePermissionSetRepository;
    @Autowired
    private UserTypeRepository userTypeRepository;

    @Override
    public Set<AvailablePermissionSet> getAvailablePermissionSets() {
        return new HashSet<>(availablePermissionSetRepository.findAll());
    }

    // TODO: Redundant
    @Override
    public Set<AvailablePermissionSet> getAvailablePermissionSetsByUserLevels(List<String> names) {
        Set<AvailablePermissionSet> permissionSets = new HashSet<>();
        names.forEach(name -> permissionSets.addAll(new HashSet(availablePermissionSetRepository.findByUserLevelsContaining(
                Arrays.asList(userTypeRepository.findByName(EUserType.get(name)).get())))));
        return permissionSets;
    }

    @Override
    public void addAvailablePermissionSet(Long permissionSetId) {
        PermissionSet permissionSet = permissionSetService.findByIdFetchPermissions(permissionSetId);
        availablePermissionSetRepository.save(AvailablePermissionSet.builder()
                .permissionSetId(permissionSetId)
                .name(permissionSet.getName())
                .description(permissionSet.getDescription())
                .userLevels(permissionSet.getUserLevels().stream()
                        .map(level -> level.getName().getName())
                        .map(name -> userTypeRepository.findByName(EUserType.get(name)).get())
                        .collect(Collectors.toSet()))
                .build());
    }

    @Override
    public void addAvailablePermissionSet(String permissionSetIdCSV) {
        List<Long> permissionSetIds =
                Arrays.stream(permissionSetIdCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        List<AvailablePermissionSet> availablePermissionSets = new ArrayList<>();
        permissionSetIds.forEach(id -> {
            PermissionSet permissionSet = permissionSetService.findByIdFetchPermissions(id);
            availablePermissionSets.add(AvailablePermissionSet.builder()
                    .permissionSetId(id)
                    .name(permissionSet.getName())
                    .description(permissionSet.getDescription())
                    .userLevels(permissionSet.getUserLevels().stream()
                            .map(level -> level.getName().getName())
                            .map(name -> userTypeRepository.findByName(EUserType.get(name)).get())
                            .collect(Collectors.toSet()))
                    .build());
        });
        availablePermissionSetRepository.saveAll(availablePermissionSets);
    }

    @Override
    public void removeAvailablePermissionSet(Long permissionSetId) {
        availablePermissionSetRepository.deleteById(permissionSetId);
    }

    @Override
    public PermissionGroup saveOrUpdate(PermissionGroup permissionGroup) {
        Set<AvailablePermissionSet> permissionSets = new HashSet<>();
        permissionGroup.getPermissionSets().forEach(p -> {
            permissionSets.add(availablePermissionSetRepository.findById(p.getId()).get());
        });
        permissionGroup.setPermissionSets(permissionSets);
        UserType userLevel = userTypeRepository.findByName(EUserType.get(permissionGroup.getUserLevelName())).get();
        if (permissionGroup.getId() != null) {
            PermissionGroup permissionGroupDb = findById(permissionGroup.getId());
            permissionGroupDb = PermissionGroupMapper.toUpdatedPermissionGroup(permissionGroupDb, permissionGroup);
            permissionGroup = repository.save(permissionGroupDb);
            permissionGroup.setUserLevelName(userLevel.getName().getName());
            return permissionGroup;
        }
        permissionGroup.setUserLevel(userLevel);
        return repository.save(permissionGroup);
    }

    @Override
    public Set<AvailablePermissionSet> addPermissionSetId(Long permissionGroupId, Long availablePermissionSetId) {
        /*if (!getAvailablePermissionSets().stream().filter(a -> a.getPermissionSetId().longValue() == availablePermissionSetId.longValue()).findAny().isPresent()) {
            throw new ForbiddenException("permission_set_id", availablePermissionSetId);
        }*/
        PermissionGroup permissionGroup = findById(permissionGroupId);
        AvailablePermissionSet availablePermissionSet =
                availablePermissionSetRepository.findById(availablePermissionSetId)
                        .orElseThrow(() -> new NotFoundException(AvailablePermissionSet.class, availablePermissionSetId));
        permissionGroup.addPermissionSet(availablePermissionSet);
        permissionGroup = repository.save(permissionGroup);
//        permissionGroup.setPermissionSets(findPermissionsByIds(permissionGroup.getPermissionSetIds()));
        return permissionGroup.getPermissionSets();
    }

    @Override
    public Set<AvailablePermissionSet> removePermissionSetId(Long permissionGroupId, Long availablePermissionSetId) {
        PermissionGroup permissionGroup = findById(permissionGroupId);
        AvailablePermissionSet availablePermissionSet =
                availablePermissionSetRepository.findById(availablePermissionSetId)
                        .orElseThrow(() -> new NotFoundException(AvailablePermissionSet.class, availablePermissionSetId));
        permissionGroup.removePermissionSet(availablePermissionSet);
        permissionGroup = repository.save(permissionGroup);
//        permissionGroup.setPermissionSets(findPermissionsByIds(permissionGroup.getPermissionSetIds()));
        return permissionGroup.getPermissionSets();
    }

    /*@Override
    public List<PermissionSet> addPermissionSet(Long permissionGroupId, PermissionSet permissionSet) {
        PermissionGroup permissionGroup = findById(permissionGroupId);
        permissionGroup.addPermissionSet(permissionSet);
        return permissionGroup.getPermissionSets();
    }

    @Override
    public List<PermissionSet> removePermissionSet(Long permissionGroupId, PermissionSet permissionSet) {
        PermissionGroup permissionGroup = findById(permissionGroupId);
        permissionGroup.removePermissionSet(permissionSet);
        return permissionGroup.getPermissionSets();
    }*/

    @Override
    public PermissionGroup findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(PermissionGroup.class, id));
    }

    @Override
    public PermissionGroup findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Set<PermissionGroup> findByIdIn(List<Long> name) {
        return repository.findByIdIn(name);
    }

    @Override
    public List<PermissionGroup> findByUserLevel(String userLevel) {
        return repository.findByUserLevel(userTypeRepository.findByName(EUserType.get(userLevel)).get());
    }

    @Override
    public List<PermissionGroup> findByUserLevel(String userLevel, boolean getRemainingPermissionSets) {
        if (getRemainingPermissionSets) {
            List<PermissionGroup> permissionGroups = new ArrayList<>();
            List<AvailablePermissionSet> allAvailablePermissionSet = availablePermissionSetRepository
                    .findByUserLevelsContaining(Arrays.asList(userTypeRepository.findByName(EUserType.get(userLevel))
                            .get()));
            findByUserLevel(userLevel).forEach(p -> {
                p.setRemainingPermissionSets(new HashSet<>(CollectionUtils.subtract(allAvailablePermissionSet,
                        p.getPermissionSets())));
                permissionGroups.add(p);
            });
            return permissionGroups;
        }
        return findByUserLevel(userLevel);
    }

    @Override
    public List<PermissionGroup> findAll(boolean getRemainingPermissionSets) {
        List<PermissionGroup> permissionGroups = new ArrayList<>();
        if (getRemainingPermissionSets) {
            Map<UserType, List<AvailablePermissionSet>> allAvailablePermissionSetMap = new HashMap<>();
            Arrays.asList(EUserType.values()).forEach(type -> {
                UserType userType = userTypeRepository.findByName(type).orElse(null);
                if (userType != null) {
                    allAvailablePermissionSetMap.put(userType, availablePermissionSetRepository
                            .findByUserLevelsContaining(Arrays.asList(userType)));
                }
            });
            repository.findAll().forEach(group -> {
                if (group.getUserLevel() != null) {
                    group.setRemainingPermissionSets(new HashSet<>(CollectionUtils.subtract(allAvailablePermissionSetMap.get(group.getUserLevel()),
                            group.getPermissionSets())));
                    group.setUserLevelName(group.getUserLevel().getName().getName());
                }
                permissionGroups.add(group);
            });
            return permissionGroups;
        }
        repository.findAll().forEach(group -> {
            group.setUserLevelName(group.getUserLevel() != null ? group.getUserLevel().getName().getName() : null);
            permissionGroups.add(group);
        });
        return permissionGroups;
    }

    private List<PermissionSet> findPermissionsByIds(Set<Long> ids) {
        return permissionSetService.findByIdIn(ids);
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
