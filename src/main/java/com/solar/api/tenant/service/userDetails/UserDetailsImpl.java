package com.solar.api.tenant.service.userDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private User user;
    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password, User user,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.user = user;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        /*for (String permission : getPermissions(user)) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }*/

        return new UserDetailsImpl(
                user.getAcctId(),
                user.getUserName(),
                user.getEmailAddress(),
                user.getPassword(),
                user,
                authorities);
    }

    /*private static Set<String> getPermissions(User user) {
        *//*Set<String> permissionSet = new HashSet<>();
        for (Role role : user.getRoles()) {
            permissionSet.addAll(getPermissions(role.getPermissionGroups(), role.getPermissionSets()));
        }
        permissionSet.addAll(getPermissions(user.getPermissionGroups(), user.getPermissionSets()));
        List<String> permissionsList = new ArrayList<>(permissionSet);
        Collections.sort(permissionsList);
        permissionSet = new HashSet<>(permissionsList);
        return permissionSet;*//*

        *//*Set<Long> permissionSetIds = new HashSet<>();
        Set<Long> userRolesGroupsPermissionSetIds = user.getRoles().stream().flatMap(r -> r.getPermissionGroups()
                .stream()).flatMap(g -> g.getPermissionSets().stream()).map(s -> s.getPermissionSetId()).collect(Collectors.toSet());
        Set<Long> userRolesPermissionSetIds = user.getRoles().stream().flatMap(r -> r.getPermissionSets()
                .stream()).map(s -> s.getPermissionSetId()).collect(Collectors.toSet());
        Set<Long> userGroupsPermissionSetIds = user.getPermissionGroups().stream().flatMap(g -> g.getPermissionSets().stream()).map(s -> s.getPermissionSetId()).collect(Collectors.toSet());
        Set<Long> userPermissionSetIds = user.getPermissionSets().stream().map(s -> s.getPermissionSetId()).collect(Collectors.toSet());
        permissionSetIds.addAll(userRolesGroupsPermissionSetIds);
        permissionSetIds.addAll(userRolesPermissionSetIds);
        permissionSetIds.addAll(userGroupsPermissionSetIds);
        permissionSetIds.addAll(userPermissionSetIds);*//*
        Set<String> userPermissions =
                SpringContextHolder.getApplicationContext().getBean(PermissionsUtil.class).getPermissions(user.getUserName(), ECompReference.API.getType(), User.class);
        List<String> permissionsList = new ArrayList<>(userPermissions);
        Collections.sort(permissionsList);
        return new HashSet<>(permissionsList);
    }*/

    /*private static Set<String> getPermissions(Set<PermissionGroup> permissionGroups, Set<AvailablePermissionSet> availablePermissionSets) {
        Set<Long> permissionSetIds =
                permissionGroups.stream().flatMap(m -> m.getPermissionSets().stream()).map(p -> p.getPermissionSetId()).collect(Collectors.toSet());
        if (!availablePermissionSets.isEmpty()) {
            permissionSetIds.addAll(availablePermissionSets.stream().map(p -> p.getPermissionSetId()).collect(Collectors.toList()));
        }
        List<PermissionSet> permissionSets =
                SpringContextHolder.getApplicationContext().getBean(PermissionSetService.class).findByIdIn(permissionSetIds);
        List<Permission> permissions = permissionSets.stream().flatMap(p -> p.getPermissions().stream()).collect(Collectors.toList());
        return permissions.stream().map(p -> p.getComponentLibrary().getComponentName().replaceAll(" ", "_") + "_" + p.getName()).collect(Collectors.toSet());
    }*/

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus().equals(EUserStatus.ACTIVE.getStatus());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
