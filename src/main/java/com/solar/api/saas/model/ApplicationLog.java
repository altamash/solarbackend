package com.solar.api.saas.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "application_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String compKey;
    private Date dateTime;
    private String logger;
    private String level;
    @Column(length = 3000)
    private String message;
    @Column(length = 5000)
    private String throwable;
}
