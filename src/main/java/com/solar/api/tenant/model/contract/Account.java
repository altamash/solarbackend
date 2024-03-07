package com.solar.api.tenant.model.contract;

import com.solar.api.tenant.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@javax.persistence.Entity
@Table(name = "account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "acctId")
    private User user;
    @Column(unique = true)
    private String userName;
    @Column(unique = true)
    private String emailAddress;
    private Date dob;
    private String firstName;
    private String middleName;
    private String lastName;
    private String userLevel;
    private String status;
    private Boolean isDocAttached;
//    @OneToMany(mappedBy = "account", cascade = CascadeType.MERGE)
//    private List<UserLevelPrivilege> userLevelPrivileges;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
