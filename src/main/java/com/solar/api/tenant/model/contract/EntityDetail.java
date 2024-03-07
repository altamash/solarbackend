package com.solar.api.tenant.model.contract;

import lombok.*;

import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "entity_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="entity_id")
    private Entity entity;

/*    @Lob
    private byte[] iconData;*/
    private String uri;
    private String fileName;

}
