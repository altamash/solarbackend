package com.solar.api.tenant.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "api_access_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(length = 100)
    private String userName;

    private Integer status;
    @Column(length = 150)
    private String apiAccessed;
    private LocalDateTime accessedAt;
    @Column(length = 45)
    private String clientIp;
    @Column(length = 20)
    private String port;
    private String referer;
    private String platform;
    private Long time;


    private LocalDateTime timeOfLogin;
    @Column(length = 40)
    private String session;
    @Column(length = 20)
    private String browserName;
    @Column(length = 20)
    private String browserVersion;
    @Column(length = 20)
    private String osName;
    @Column(length = 10)
    private String osVersion;
    @Column(length = 20)
    private String deviceMacAddress;
    private LocalDateTime logoutDateTime;
    private Boolean forcedLogout;
}
