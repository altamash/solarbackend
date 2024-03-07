package com.solar.api.saas.model.contact;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private String department;
    @Column
    private String source;
    @Column
    private Long sourceId;




}