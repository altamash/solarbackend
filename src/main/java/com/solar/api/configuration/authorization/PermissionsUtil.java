package com.solar.api.configuration.authorization;

import com.solar.api.configuration.SpringContextHolder;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.permission.Permission;
import com.solar.api.saas.model.permission.PermissionSet;
import com.solar.api.saas.model.permission.component.ComponentLibrary;
import com.solar.api.saas.model.permission.component.ECompReference;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.repository.permission.PermissionSetRepository;
import com.solar.api.saas.repository.permission.navigation.NavigationComponentMapRepository;
import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.navigation.NavigationUserMap;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.repository.permission.navigation.NavigationUserMapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PermissionsUtil {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private PermissionSetRepository permissionSetRepository;
    @Autowired
    private NavigationComponentMapRepository navigationComponentMapRepository;
    @Autowired
    private NavigationUserMapRepository navigationUserMapRepository;
    @Autowired
    private UserRepository userRepository;

    public Set<String> getPermissions(String userName, String compReference, Class clazz) {
        Set<Long> permissionSetIds = getPermissionSetIds(userName, clazz);
        List<PermissionSet> permissionSets = permissionSetRepository.findByIdIn(permissionSetIds);
        List<Permission> permissions = permissionSets.stream()
                .flatMap(p -> p.getPermissions().stream())
                .filter(p -> compReference.equals(p.getComponentLibrary().getComponentTypeProvision().getCompReference())).collect(Collectors.toList());
        if (ECompReference.UI.getType().equals(compReference)) {
            return permissions.stream()
                    .map(p -> p.getName().replaceAll(" ", "_")).collect(Collectors.toSet());
        }
        return permissions.stream().map(Permission::getName).collect(Collectors.toSet());
    }

    public Set<Long> getPermissionSetIds(String userName, Class clazz) {
        if (clazz == User.class) {
            return getPermissionSetIds(SpringContextHolder.getApplicationContext().getBean(UserRepository.class).findByUserNameFetchPermissions(userName), clazz);
        } else if (clazz == MasterTenant.class) {
            return getPermissionSetIds(SpringContextHolder.getApplicationContext().getBean(MasterTenantRepository.class).findByUserNameFetchTenantPermissions(userName), clazz);
        }
        return Collections.emptySet();
    }

    private Set<Long> getPermissionSetIds(Object userObject, Class clazz) {
        Set<Long> permissionSetIds = new HashSet<>();
        if (clazz == User.class) {
            User user = (User) userObject;
            Set<Long> userRolesGroupsPermissionSetIds = user.getRoles() != null ? user.getRoles().stream()
                    .flatMap(r -> r.getPermissionGroups().stream()).flatMap(g -> g.getPermissionSets().stream())
                    .filter(AvailablePermissionSet::isEnabled)
                    .map(AvailablePermissionSet::getPermissionSetId)
                    .collect(Collectors.toSet()) : Collections.emptySet();
            Set<Long> userRolesPermissionSetIds = user.getRoles() != null ? user.getRoles().stream()
                    .flatMap(r -> r.getPermissionSets().stream())
                    .filter(AvailablePermissionSet::isEnabled)
                    .map(AvailablePermissionSet::getPermissionSetId)
                    .collect(Collectors.toSet()) : Collections.emptySet();
            Set<Long> userGroupsPermissionSetIds = user.getPermissionGroups() != null ? user.getPermissionGroups()
                    .stream().flatMap(g -> g.getPermissionSets().stream())
                    .filter(AvailablePermissionSet::isEnabled)
                    .map(AvailablePermissionSet::getPermissionSetId).collect(Collectors.toSet()) : Collections.emptySet();
            Set<Long> userPermissionSetIds = user.getPermissionSets() != null ? user.getPermissionSets().stream()
                    .filter(AvailablePermissionSet::isEnabled)
                    .map(AvailablePermissionSet::getPermissionSetId).collect(Collectors.toSet()) : Collections.emptySet();
            permissionSetIds.addAll(userRolesGroupsPermissionSetIds);
            permissionSetIds.addAll(userRolesPermissionSetIds);
            permissionSetIds.addAll(userGroupsPermissionSetIds);
            permissionSetIds.addAll(userPermissionSetIds);
        } else if (clazz == MasterTenant.class) {
            MasterTenant tenant  = (MasterTenant) userObject;
            Set<Long> userRolesPermissionSetIds = tenant.getTenantRoles() != null ? tenant.getTenantRoles().stream()
                    .flatMap(r -> r.getPermissionSets().stream())
                    .map(PermissionSet::getId).collect(Collectors.toSet()) : Collections.emptySet();
            Set<Long> userPermissionSetIds = tenant.getPermissionSets() != null ? tenant.getPermissionSets().stream()
                    .map(PermissionSet::getId).collect(Collectors.toSet()) : Collections.emptySet();
            permissionSetIds.addAll(userRolesPermissionSetIds);
            permissionSetIds.addAll(userPermissionSetIds);
        }
        return permissionSetIds;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResponse setNavigationUserMap() {
        return modifyNavigationUserMap();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Async
    public BaseResponse setNavigationUserMapAsync() {
        return modifyNavigationUserMap();
    }

    private BaseResponse modifyNavigationUserMap() {
        long start = System.currentTimeMillis();
        List<NavigationUserMap> navigationUserMaps = new ArrayList<>();
        String step = null;
        try {
            step = "one";
            List<User> users = userRepository.findAll();
            long findAllTime = System.currentTimeMillis() - start;
            LOGGER.info("findAll took {}",  Utility.getFormattedMillis(findAllTime));
            for (User user : users) {
                try {
                    step = "two-" + user.getAcctId();
                    LOGGER.info(">>>>>>>>>>>>>>>>>> Calculating component_library records for user {}", user.getAcctId());
                    Set<ComponentLibrary> componentLibraries = getUserUIComponents(user, User.class);
                    step = "three-" + user.getAcctId();
                    LOGGER.info(">>>>>>>>>>>>>>>>>> Calculating nav_comp_map ids for user {}", user.getAcctId());
                    List<Long> navigationComponentMapIds =
                            navigationComponentMapRepository.findByComponentLibraryIn(componentLibraries);
                    step = "four-" + user.getAcctId();
                    LOGGER.info(">>>>>>>>>>>>>>>>>> Generating nav_user_map records for user {}", user.getAcctId());
                    for (int j = 0; j < navigationComponentMapIds.size(); j++) {
                        navigationUserMaps.add(NavigationUserMap
                                .builder()
                                .user(user)
                                .navMapId(navigationComponentMapIds.get(j))
                                .build());
                    }
                } catch (Exception e) {
                    LOGGER.error(">>>>>>>>>>>>>>>>>> Error at step {}", step, e);
                }
            }
            step = "five";
            LOGGER.info(">>>>>>>>>>>>>>>>>> Deleting all nav_user_map records");
            navigationUserMapRepository.deleteAll();
            step = "six";
            LOGGER.info(">>>>>>>>>>>>>>>>>> Saving all new nav_user_map records");
            navigationUserMapRepository.saveAll(navigationUserMaps);
        } catch (Exception e) {
            LOGGER.error(">>>>>>>>>>>>>>>>>> Error at step {}", step, e);
            return BaseResponse.builder()
                    .code(200)
                    .message("Error at step " + step + "\n" + e.getMessage())
                    .build();
        }
        LOGGER.info("setNavigationUserMap took {}", Utility.getFormattedMillis(System.currentTimeMillis() - start));
        return BaseResponse.builder()
                .code(200)
                .message("SUCCESS")
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<NavigationUserMap> setNavigationUserMap(String userName, Class clazz) {
        List<NavigationUserMap> navigationUserMaps = new ArrayList<>();
        User user = userRepository.findByUserNameFetchPermissions(userName);
        Set<ComponentLibrary> componentLibraries = getUserUIComponents(user, clazz);
        List<Long> navigationComponentMapIds = navigationComponentMapRepository.findByComponentLibraryIn(componentLibraries);
        navigationComponentMapIds.forEach(navMapId -> navigationUserMaps.add(NavigationUserMap.builder()
                .user(user)
                .navMapId(navMapId)
                .build()));
        navigationUserMapRepository.deleteByUser(user);
        return navigationUserMapRepository.saveAll(navigationUserMaps);
    }

    private Set<ComponentLibrary> getUserUIComponents(User user, Class clazz) {
        Set<Long> permissionSetIds = getPermissionSetIds(user, clazz);
        List<PermissionSet> permissionSets = permissionSetRepository.findByIdIn(permissionSetIds);
        List<Permission> permissions = permissionSets.stream()
                .flatMap(p -> p.getPermissions().stream())
                .filter(p -> ECompReference.UI.getType().equals(p.getComponentLibrary().getComponentTypeProvision()
                        .getCompReference())).collect(Collectors.toList());
        return permissions.stream()
                .map(p -> p.getComponentLibrary())
                .filter(comp -> comp.getEnabled() && (comp.getTestMode() == null || !comp.getTestMode() || (!EProfile.PROD.getName().equals(appProfile) && !EProfile.NEWPROD.getName().equals(appProfile))))
                .collect(Collectors.toSet());
    }
}
