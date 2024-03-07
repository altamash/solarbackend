package com.solar.api.tenant.service.process.permission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.configuration.SpringContextHolder;
import com.solar.api.configuration.authorization.PermissionsUtil;
import com.solar.api.saas.model.permission.Permission;
import com.solar.api.saas.model.permission.PermissionSet;
import com.solar.api.saas.model.permission.component.ECompReference;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.model.tenant.role.TenantRole;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.process.encryption.EncryptionService;
import com.solar.api.saas.service.process.permission.PermissionSetService;
import com.solar.api.saas.service.tenantDetails.TenantDetailsImpl;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.service.userDetails.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPermissionsImpl implements UserPermissions {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private PermissionsUtil permissionsUtil;

    @Override
    public Set<String> getUserPermissions(String compReference) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Set<String> permissionSet = new HashSet<>();
        if (principal instanceof UserDetailsImpl) {
            String userName = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUserNameFetchPermissions(userName);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with userName: " + userName);
            }
            for (Role role : user.getRoles()) {
                permissionSet.addAll(getPermissions(role.getPermissionGroups(), role.getPermissionSets().stream().map(p -> p.getPermissionSetId()).collect(Collectors.toList()), compReference));
            }
            permissionSet.addAll(getPermissions(user.getPermissionGroups(), user.getPermissionSets().stream().map(p -> p.getPermissionSetId()).collect(Collectors.toList()), compReference));
            List<String> permissionsList = new ArrayList<>(permissionSet);
            Collections.sort(permissionsList);
            permissionSet = new HashSet<>(permissionsList);
            return permissionSet;
        } else if (principal instanceof TenantDetailsImpl) {
            String userName = ((TenantDetailsImpl) authentication.getPrincipal()).getUsername();
            MasterTenant tenant = masterTenantRepository.findByUserNameFetchTenantPermissions(userName);
            if (tenant == null) {
                throw new UsernameNotFoundException("MasterTenant not found with userName: " + userName);
            }
            for (TenantRole tenantRole : tenant.getTenantRoles()) {
                permissionSet.addAll(getPermissions(null, tenantRole.getPermissionSets().stream().map(p -> p.getId()).collect(Collectors.toList()), compReference));
            }
//            permissionSet.addAll(getPermissions(null, tenant.getPermission()));
            List<String> permissionsList = new ArrayList<>(permissionSet);
            Collections.sort(permissionsList);
            permissionSet = new HashSet<>(permissionsList);
            return permissionSet;
        }
        return null;
    }

    private static Set<String> getPermissions(Set<PermissionGroup> permissionGroups, List<Long> permissionSetIds, String compReference) {
        Set<Long> pSetIds = new HashSet<>();
        if (permissionGroups != null && !permissionGroups.isEmpty()) {
            pSetIds.addAll(permissionGroups.stream().flatMap(m -> m.getPermissionSets().stream()).map(p -> p.getPermissionSetId()).collect(Collectors.toSet()));
        }
        if (permissionSetIds != null && !permissionSetIds.isEmpty()) {
            pSetIds.addAll(permissionSetIds);
        }
        List<PermissionSet> permissionSets =
                SpringContextHolder.getApplicationContext().getBean(PermissionSetService.class).findByIdIn(pSetIds);
        List<Permission> permissions = permissionSets.stream().flatMap(p -> p.getPermissions().stream()).filter(p ->
                compReference.equals(p.getComponentLibrary().getComponentTypeProvision().getCompReference())).collect(Collectors.toList());
        return permissions.stream().map(p -> p.getComponentLibrary().getComponentName().replaceAll(" ", "_") + "_" + p.getName()).collect(Collectors.toSet());
    }

    @Override
    public String getPPK2() {
        return Base64.getEncoder().encodeToString(storageService.downloadToByteArray(appProfile, "saas/keyvalue", "ppk2"));
    }

    @Override
    public List<String> getUserPermissionsEncrypted(String compReference) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        Set<String> userPermissions = permissionsUtil.getPermissions(userName, ECompReference.UI.getType(), User.class);
        List<String> permissionsList = new ArrayList<>(userPermissions);
        Collections.sort(permissionsList);
        userPermissions = new HashSet<>(permissionsList);
        /*List<String> strings = new ArrayList<>();
        SecretKey secretKey = encryptionService.loadSecretKey("AES");
        strings.add(encryptionService.encrypt(Base64.getEncoder().encodeToString(secretKey.getEncoded()), "RSA/ECB/PKCS1Padding", encryptionService.loadPublicKey("RSA")));
        strings.add(encryptionService.encrypt(String.join(" ", getUserPermissions(compReference)), "AES", secretKey));*/
        List<String> strings = new ArrayList<>();
//        strings.addAll(encryptionService.encrypt(String.join(" ", getUserPermissions(compReference))));
//        strings.addAll(encryptionService.encryptWithRSAWorkflow(String.join(" ", userPermissions)));
        try {
            strings.addAll(encryptionService.encryptWithRSAWorkflow(new ObjectMapper().writeValueAsString(userPermissions)));
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        strings.add(encryptionService.getPPK2());
        return strings;
    }

}
