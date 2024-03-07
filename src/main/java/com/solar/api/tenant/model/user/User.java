package com.solar.api.tenant.model.user;

import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.model.workflow.WorkflowGroupAssignment;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long acctId;
    @Transient
    private String jwtToken;
    private Long compKey;
    private String firstName;
    private String lastName;
    @Column(nullable = true, unique = true, columnDefinition = "NONE")
    private String userName;
    @Column(nullable = true)
    private String password;
    private String IdCode;       //Authority ID
    private String authorityId;  //e.g. SSN, Driving License
    private String gender;
    private Date dataOfBirth;
    private Date registerDate;
    private Date activeDate;
    private String status;
    @Column(length = 1000)
    private String notes;
    private String prospectStatus;
    private String referralEmail;
    private Date deferredContactDate;
    private String language;
    private String authentication; // standard/ NA / oauth
    private boolean isEmailVerified;
    @ManyToOne
    @JoinColumn(name = "user_type", nullable = false)
    private UserType userType;
    @ManyToMany(targetEntity = Role.class)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    @ManyToMany(targetEntity = PermissionGroup.class)
    @JoinTable(name = "user_permission_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_group_id"))
    private Set<PermissionGroup> permissionGroups;
    //    @ElementCollection
    @ManyToMany(targetEntity = AvailablePermissionSet.class)
    @JoinTable(name = "user_permission_set",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_set_id"))
    private Set<AvailablePermissionSet> permissionSets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private List<UserLevelPrivilege> userLevelPrivileges;
//    @ElementCollection
//    private Set<Long> permissionSetIds = new HashSet<>(); // Only ids because PermissionSet are in saas schema
//    @Transient
//    private Set<PermissionSet> permissionSets = new TreeSet<>();

    private String category;
    private String groupId;
    @Lob
    private byte[] photo;
    private String socialUrl;
    private String emailAddress;
    private Boolean ccd;
    @Column(name = "customer_subscriptions")
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.MERGE)
    private List<CustomerSubscription> customerSubscriptions;
    @Column(name = "addresses")
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.MERGE)
    private Set<Address> addresses;
    @OneToMany(mappedBy = "portalAccount", cascade = CascadeType.MERGE)
    private Set<PaymentInfo> paymentInfos;
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.MERGE)
    private Set<BillingHead> billingHeads;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "workflow_group_id", referencedColumnName = "id")
    private WorkflowGroupAssignment workflowGroupAssignment;
    @Transient
    private String externalId;
    @Transient
    private String action;
    @Transient
    private String uploadPassword;
    @OneToOne(mappedBy = "user")
    private Account account;
    private Integer privLevel;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public User(Long userId) {
        this.acctId= userId;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void addPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroups.add(permissionGroup);
    }

    public void removePermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroups.remove(permissionGroup);
    }

    public void addPermissionSet(AvailablePermissionSet permissionSet) {
        this.permissionSets.add(permissionSet);
    }

    public void removePermissionSet(AvailablePermissionSet permissionSet) {
        this.permissionSets.remove(permissionSet);
    }

}
