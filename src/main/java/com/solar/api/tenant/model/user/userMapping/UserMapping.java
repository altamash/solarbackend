package com.solar.api.tenant.model.user.userMapping;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user_mapping")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class UserMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String module;
    private String ref_id;
    @Column(name="entity_id")
    private Long entityId;

}
