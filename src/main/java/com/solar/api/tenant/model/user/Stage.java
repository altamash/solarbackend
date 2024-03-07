package com.solar.api.tenant.model.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Stage")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stage {

    @Id
    private Long id;
    private String externalId;
    @MapsId
    @OneToOne
    @JoinColumn(name = "solar_id")
    private User user;

}
