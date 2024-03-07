package com.solar.api.tenant.model.contract;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@javax.persistence.Entity
@Table(name = "user_password_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long accountId;
    private String authPlatform;
    private String oAuthCode;
    private String password;
    private LocalDateTime futureExpiryDate;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
